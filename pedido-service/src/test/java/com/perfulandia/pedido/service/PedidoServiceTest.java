package com.perfulandia.pedido.service;

import com.perfulandia.pedido.dto.ActualizarEstadoPedidoRequest;
import com.perfulandia.pedido.dto.CrearPedidoRequest;
import com.perfulandia.pedido.dto.DetallePedidoRequest;
import com.perfulandia.pedido.dto.UsuarioDTO;
import com.perfulandia.pedido.exception.RecursoNoEncontradoException;
import com.perfulandia.pedido.exception.ReglaNegocioException;
import com.perfulandia.pedido.model.EstadoPedido;
import com.perfulandia.pedido.model.Pedido;
import com.perfulandia.pedido.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    private static final String USUARIO_URL = "http://localhost:8082/api/usuarios";
    private static final String TIENDA_URL = "http://localhost:8091/api/v1/tiendas";
    private static final String PRODUCTO_URL = "http://localhost:8092/api/v1/productos";

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PedidoService pedidoService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(pedidoService, "usuarioServiceUrl", USUARIO_URL);
        ReflectionTestUtils.setField(pedidoService, "tiendaServiceUrl", TIENDA_URL);
        ReflectionTestUtils.setField(pedidoService, "productoServiceUrl", PRODUCTO_URL);
    }

    @Test
    void crearPedido_cuandoDatosSonValidos_deberiaCrearPedidoConTotalCorrecto() {
        //given
        CrearPedidoRequest request = crearPedidoRequestValido();

        simularUsuarioValido(1L);
        simularTiendaValida(1L);
        simularProductoValido(1L);
        simularProductoValido(2L);

        when(pedidoRepository.save(any(Pedido.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        Pedido resultado = pedidoService.crearPedido(request);

        //then
        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(1L, resultado.getIdUsuario()),
                () -> assertEquals(1L, resultado.getIdTienda()),
                () -> assertEquals(EstadoPedido.PENDIENTE, resultado.getEstado()),
                () -> assertEquals(25000, resultado.getTotal()),
                () -> assertEquals(2, resultado.getDetalles().size())
        );

        verify(restTemplate).getForObject(USUARIO_URL + "/1", UsuarioDTO.class);
        verify(restTemplate).getForObject(TIENDA_URL + "/1", Object.class);
        verify(restTemplate).getForObject(PRODUCTO_URL + "/1", Object.class);
        verify(restTemplate).getForObject(PRODUCTO_URL + "/2", Object.class);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void crearPedido_sinDetalles_deberiaLanzarReglaNegocioException() {
        //given
        CrearPedidoRequest request = new CrearPedidoRequest();
        request.setIdUsuario(1L);
        request.setIdTienda(1L);
        request.setDetalles(List.of());

        //when
        ReglaNegocioException exception = assertThrows(
                ReglaNegocioException.class,
                () -> pedidoService.crearPedido(request)
        );

        //then
        assertEquals("El pedido debe tener al menos un producto", exception.getMessage());

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void crearPedido_conDetallesNull_deberiaLanzarReglaNegocioException() {
        //given
        CrearPedidoRequest request = new CrearPedidoRequest();
        request.setIdUsuario(1L);
        request.setIdTienda(1L);
        request.setDetalles(null);

        //when
        ReglaNegocioException exception = assertThrows(
                ReglaNegocioException.class,
                () -> pedidoService.crearPedido(request)
        );

        //then
        assertEquals("El pedido debe tener al menos un producto", exception.getMessage());

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void crearPedido_cuandoUsuarioNoExiste_deberiaLanzarReglaNegocioException() {
        //given
        CrearPedidoRequest request = crearPedidoRequestValido();
        request.setIdUsuario(99L);

        when(restTemplate.getForObject(USUARIO_URL + "/99", UsuarioDTO.class))
                .thenThrow(crearErrorNotFound());

        //when
        ReglaNegocioException exception = assertThrows(
                ReglaNegocioException.class,
                () -> pedidoService.crearPedido(request)
        );

        //then
        assertEquals("El usuario con ID 99 no existe", exception.getMessage());

        verify(restTemplate).getForObject(USUARIO_URL + "/99", UsuarioDTO.class);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void crearPedido_cuandoUsuarioServiceNoResponde_deberiaLanzarReglaNegocioException() {
        //given
        CrearPedidoRequest request = crearPedidoRequestValido();

        when(restTemplate.getForObject(USUARIO_URL + "/1", UsuarioDTO.class))
                .thenThrow(new RestClientException("Error de conexión"));

        //when
        ReglaNegocioException exception = assertThrows(
                ReglaNegocioException.class,
                () -> pedidoService.crearPedido(request)
        );

        //then
        assertEquals("No se pudo validar el usuario con ID 1", exception.getMessage());

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void crearPedido_cuandoUsuarioRetornaNull_deberiaLanzarReglaNegocioException() {
        //given
        CrearPedidoRequest request = crearPedidoRequestValido();

        when(restTemplate.getForObject(USUARIO_URL + "/1", UsuarioDTO.class))
                .thenReturn(null);

        //when
        ReglaNegocioException exception = assertThrows(
                ReglaNegocioException.class,
                () -> pedidoService.crearPedido(request)
        );

        //then
        assertEquals("El usuario con ID 1 no existe", exception.getMessage());

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void crearPedido_cuandoUsuarioEstaInactivo_deberiaLanzarReglaNegocioException() {
        //given
        CrearPedidoRequest request = crearPedidoRequestValido();

        when(restTemplate.getForObject(USUARIO_URL + "/1", UsuarioDTO.class))
                .thenReturn(crearUsuarioDTO(false));

        //when
        ReglaNegocioException exception = assertThrows(
                ReglaNegocioException.class,
                () -> pedidoService.crearPedido(request)
        );

        //then
        assertEquals("El usuario con ID 1 está inactivo", exception.getMessage());

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void crearPedido_cuandoTiendaNoExiste_deberiaLanzarReglaNegocioException() {
        //given
        CrearPedidoRequest request = crearPedidoRequestValido();
        request.setIdTienda(99L);

        simularUsuarioValido(1L);

        when(restTemplate.getForObject(TIENDA_URL + "/99", Object.class))
                .thenThrow(crearErrorNotFound());

        //when
        ReglaNegocioException exception = assertThrows(
                ReglaNegocioException.class,
                () -> pedidoService.crearPedido(request)
        );

        //then
        assertEquals("La tienda con ID 99 no existe", exception.getMessage());

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void crearPedido_cuandoProductoNoExiste_deberiaLanzarReglaNegocioException() {
        //given
        CrearPedidoRequest request = crearPedidoRequestValido();

        simularUsuarioValido(1L);
        simularTiendaValida(1L);
        simularProductoValido(1L);

        when(restTemplate.getForObject(PRODUCTO_URL + "/2", Object.class))
                .thenThrow(crearErrorNotFound());

        //when
        ReglaNegocioException exception = assertThrows(
                ReglaNegocioException.class,
                () -> pedidoService.crearPedido(request)
        );

        //then
        assertEquals("El producto con ID 2 no existe", exception.getMessage());

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void listarPedidos_deberiaRetornarListaDePedidos() {
        //given
        Pedido pedido1 = crearPedido(1L, 1L, EstadoPedido.PENDIENTE, 10000);
        Pedido pedido2 = crearPedido(2L, 2L, EstadoPedido.CONFIRMADO, 20000);

        when(pedidoRepository.findAll()).thenReturn(List.of(pedido1, pedido2));

        //when
        List<Pedido> resultado = pedidoService.listarPedidos();

        //then
        assertEquals(2, resultado.size());

        verify(pedidoRepository).findAll();
    }

    @Test
    void buscarPorId_cuandoExiste_deberiaRetornarPedido() {
        //given
        Pedido pedido = crearPedido(1L, 1L, EstadoPedido.PENDIENTE, 15000);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        //when
        Pedido resultado = pedidoService.buscarPorId(1L);

        //then
        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(1L, resultado.getIdPedido()),
                () -> assertEquals(15000, resultado.getTotal())
        );

        verify(pedidoRepository).findById(1L);
    }

    @Test
    void buscarPorId_cuandoNoExiste_deberiaLanzarRecursoNoEncontradoException() {
        //given
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        //when
        RecursoNoEncontradoException exception = assertThrows(
                RecursoNoEncontradoException.class,
                () -> pedidoService.buscarPorId(99L)
        );

        //then
        assertEquals("Pedido no encontrado con ID: 99", exception.getMessage());

        verify(pedidoRepository).findById(99L);
    }

    @Test
    void buscarPorUsuario_cuandoUsuarioExiste_deberiaRetornarPedidosDelUsuario() {
        //given
        Pedido pedido = crearPedido(1L, 5L, EstadoPedido.PENDIENTE, 12000);

        simularUsuarioValido(5L);
        when(pedidoRepository.findByIdUsuario(5L)).thenReturn(List.of(pedido));

        //when
        List<Pedido> resultado = pedidoService.buscarPorUsuario(5L);

        //then
        assertEquals(1, resultado.size());
        assertEquals(5L, resultado.get(0).getIdUsuario());

        verify(restTemplate).getForObject(USUARIO_URL + "/5", UsuarioDTO.class);
        verify(pedidoRepository).findByIdUsuario(5L);
    }

    @Test
    void buscarPorTienda_cuandoTiendaExiste_deberiaRetornarPedidosDeLaTienda() {
        //given
        Pedido pedido = crearPedido(1L, 1L, EstadoPedido.PENDIENTE, 18000);

        simularTiendaValida(3L);
        when(pedidoRepository.findByIdTienda(3L)).thenReturn(List.of(pedido));

        //when
        List<Pedido> resultado = pedidoService.buscarPorTienda(3L);

        //then
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getIdUsuario());

        verify(restTemplate).getForObject(TIENDA_URL + "/3", Object.class);
        verify(pedidoRepository).findByIdTienda(3L);
    }

    @Test
    void buscarPorEstado_deberiaRetornarPedidosPorEstado() {
        //given
        Pedido pedido = crearPedido(1L, 1L, EstadoPedido.CONFIRMADO, 30000);

        when(pedidoRepository.findByEstado(EstadoPedido.CONFIRMADO)).thenReturn(List.of(pedido));

        //when
        List<Pedido> resultado = pedidoService.buscarPorEstado(EstadoPedido.CONFIRMADO);

        //then
        assertEquals(1, resultado.size());
        assertEquals(EstadoPedido.CONFIRMADO, resultado.get(0).getEstado());

        verify(pedidoRepository).findByEstado(EstadoPedido.CONFIRMADO);
    }

    @Test
    void actualizarEstado_cuandoPedidoExiste_deberiaActualizarEstadoCorrectamente() {
        //given
        Pedido pedido = crearPedido(1L, 1L, EstadoPedido.PENDIENTE, 10000);

        ActualizarEstadoPedidoRequest request = new ActualizarEstadoPedidoRequest();
        request.setEstado(EstadoPedido.CONFIRMADO);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        Pedido resultado = pedidoService.actualizarEstado(1L, request);

        //then
        assertEquals(EstadoPedido.CONFIRMADO, resultado.getEstado());

        verify(pedidoRepository).findById(1L);
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void actualizarEstado_cuandoPedidoEstaCancelado_deberiaLanzarReglaNegocioException() {
        //given
        Pedido pedido = crearPedido(1L, 1L, EstadoPedido.CANCELADO, 10000);

        ActualizarEstadoPedidoRequest request = new ActualizarEstadoPedidoRequest();
        request.setEstado(EstadoPedido.CONFIRMADO);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        //when
        ReglaNegocioException exception = assertThrows(
                ReglaNegocioException.class,
                () -> pedidoService.actualizarEstado(1L, request)
        );

        //then
        assertEquals("No se puede modificar un pedido cancelado", exception.getMessage());

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void actualizarEstado_cuandoPedidoEstaEntregado_deberiaLanzarReglaNegocioException() {
        //given
        Pedido pedido = crearPedido(1L, 1L, EstadoPedido.ENTREGADO, 10000);

        ActualizarEstadoPedidoRequest request = new ActualizarEstadoPedidoRequest();
        request.setEstado(EstadoPedido.CONFIRMADO);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        //when
        ReglaNegocioException exception = assertThrows(
                ReglaNegocioException.class,
                () -> pedidoService.actualizarEstado(1L, request)
        );

        //then
        assertEquals("No se puede modificar un pedido entregado", exception.getMessage());

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void cancelarPedido_cuandoPedidoEstaPendiente_deberiaCambiarEstadoACancelado() {
        //given
        Pedido pedido = crearPedido(1L, 1L, EstadoPedido.PENDIENTE, 10000);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        Pedido resultado = pedidoService.cancelarPedido(1L);

        //then
        assertEquals(EstadoPedido.CANCELADO, resultado.getEstado());

        verify(pedidoRepository).findById(1L);
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void cancelarPedido_cuandoPedidoEstaEntregado_deberiaLanzarReglaNegocioException() {
        //given
        Pedido pedido = crearPedido(1L, 1L, EstadoPedido.ENTREGADO, 10000);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        //when
        ReglaNegocioException exception = assertThrows(
                ReglaNegocioException.class,
                () -> pedidoService.cancelarPedido(1L)
        );

        //then
        assertEquals("No se puede cancelar un pedido entregado", exception.getMessage());

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    private CrearPedidoRequest crearPedidoRequestValido() {
        DetallePedidoRequest detalle1 = new DetallePedidoRequest();
        detalle1.setIdProducto(1L);
        detalle1.setCantidad(2);
        detalle1.setPrecioUnitario(10000);

        DetallePedidoRequest detalle2 = new DetallePedidoRequest();
        detalle2.setIdProducto(2L);
        detalle2.setCantidad(1);
        detalle2.setPrecioUnitario(5000);

        CrearPedidoRequest request = new CrearPedidoRequest();
        request.setIdUsuario(1L);
        request.setIdTienda(1L);
        request.setDetalles(List.of(detalle1, detalle2));

        return request;
    }

    private Pedido crearPedido(Long idPedido, Long idUsuario, EstadoPedido estado, Integer total) {
        return Pedido.builder()
                .idPedido(idPedido)
                .idUsuario(idUsuario)
                .idTienda(1L)
                .estado(estado)
                .total(total)
                .build();
    }

    private void simularUsuarioValido(Long idUsuario) {
        when(restTemplate.getForObject(USUARIO_URL + "/" + idUsuario, UsuarioDTO.class))
                .thenReturn(crearUsuarioDTO(true));
    }

    private void simularTiendaValida(Long idTienda) {
        when(restTemplate.getForObject(TIENDA_URL + "/" + idTienda, Object.class))
                .thenReturn(new Object());
    }

    private void simularProductoValido(Long idProducto) {
        when(restTemplate.getForObject(PRODUCTO_URL + "/" + idProducto, Object.class))
                .thenReturn(new Object());
    }

    private UsuarioDTO crearUsuarioDTO(Boolean estado) {
        return new UsuarioDTO(
                1L,
                "Nicolás",
                "Quezada",
                "nicolas.quezada@perfulandia.cl",
                "Concepción, Región del Biobío",
                estado,
                1L,
                "CLIENTE"
        );
    }

    private HttpClientErrorException crearErrorNotFound() {
        return HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                HttpHeaders.EMPTY,
                new byte[0],
                StandardCharsets.UTF_8
        );
    }
}