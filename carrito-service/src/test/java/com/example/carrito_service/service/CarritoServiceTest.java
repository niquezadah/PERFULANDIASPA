package com.example.carrito_service.service;

import com.example.carrito_service.dto.CarritoDTO;
import com.example.carrito_service.dto.ProductoDTO;
import com.example.carrito_service.dto.UsuarioDTO;
import com.example.carrito_service.model.Carrito;
import com.example.carrito_service.repository.CarritoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
class CarritoServiceTest {

    private static final String URL_USUARIO_1 = "http://localhost:8082/api/usuarios/1";
    private static final String URL_PRODUCTO_1 = "http://localhost:8092/api/v1/productos/1";

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CarritoService carritoService;

    @Test
    void listarCarritos_deberiaRetornarListaDeCarritos() {
        //given
        Carrito carrito = crearCarrito(1L, true);
        when(carritoRepository.findAll()).thenReturn(List.of(carrito));

        //when
        List<CarritoDTO> resultado = carritoService.listarCarritos();

        //then
        assertAll(
                () -> assertEquals(1, resultado.size()),
                () -> assertEquals(1L, resultado.get(0).getIdCarrito()),
                () -> assertEquals(1L, resultado.get(0).getIdCliente()),
                () -> assertEquals(1L, resultado.get(0).getIdProducto()),
                () -> assertEquals("EAU DE PARFUM ROSAS DEL SUR", resultado.get(0).getNombreProducto()),
                () -> assertEquals(2, resultado.get(0).getCantidad()),
                () -> assertEquals(24990.0, resultado.get(0).getPrecioUnitario()),
                () -> assertEquals(49980.0, resultado.get(0).getSubtotal()),
                () -> assertTrue(resultado.get(0).getActivo())
        );

        verify(carritoRepository).findAll();
    }

    @Test
    void buscarCarritoPorId_cuandoExiste_deberiaRetornarCarrito() {
        //given
        Long id = 1L;
        Carrito carrito = crearCarrito(id, true);

        when(carritoRepository.findById(id)).thenReturn(Optional.of(carrito));

        //when
        Optional<CarritoDTO> resultado = carritoService.buscarCarritoPorId(id);

        //then
        assertTrue(resultado.isPresent());
        assertAll(
                () -> assertEquals(id, resultado.get().getIdCarrito()),
                () -> assertEquals(1L, resultado.get().getIdCliente()),
                () -> assertEquals("EAU DE PARFUM ROSAS DEL SUR", resultado.get().getNombreProducto()),
                () -> assertEquals(49980.0, resultado.get().getSubtotal())
        );

        verify(carritoRepository).findById(id);
    }

    @Test
    void buscarCarritoPorId_cuandoNoExiste_deberiaRetornarOptionalVacio() {
        //given
        Long id = 99L;

        when(carritoRepository.findById(id)).thenReturn(Optional.empty());

        //when
        Optional<CarritoDTO> resultado = carritoService.buscarCarritoPorId(id);

        //then
        assertTrue(resultado.isEmpty());

        verify(carritoRepository).findById(id);
    }

    @Test
    void guardarCarrito_cuandoClienteYProductoSonValidos_deberiaGuardarYRetornarCarritoDTO() {
        //given
        CarritoDTO carritoDTO = crearCarritoDTO(null, true);
        Carrito carritoGuardado = crearCarrito(1L, true);

        simularClienteValido();
        when(restTemplate.getForObject(URL_PRODUCTO_1, ProductoDTO.class)).thenReturn(crearProductoDTO(true, 10));
        when(carritoRepository.save(any(Carrito.class))).thenReturn(carritoGuardado);

        //when
        CarritoDTO resultado = carritoService.guardarCarrito(carritoDTO);

        //then
        assertAll(
                () -> assertEquals(1L, resultado.getIdCarrito()),
                () -> assertEquals(1L, resultado.getIdCliente()),
                () -> assertEquals(1L, resultado.getIdProducto()),
                () -> assertEquals("EAU DE PARFUM ROSAS DEL SUR", resultado.getNombreProducto()),
                () -> assertEquals(2, resultado.getCantidad()),
                () -> assertEquals(24990.0, resultado.getPrecioUnitario()),
                () -> assertEquals(49980.0, resultado.getSubtotal()),
                () -> assertTrue(resultado.getActivo())
        );

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate).getForObject(URL_PRODUCTO_1, ProductoDTO.class);
        verify(carritoRepository).save(any(Carrito.class));
    }

    @Test
    void guardarCarrito_cuandoClienteNoExiste_deberiaLanzarRuntimeException() {
        //given
        CarritoDTO carritoDTO = crearCarritoDTO(null, true);

        HttpClientErrorException errorNotFound = crearErrorNotFound();

        when(restTemplate.getForObject(URL_USUARIO_1, UsuarioDTO.class)).thenThrow(errorNotFound);

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> carritoService.guardarCarrito(carritoDTO)
        );

        //then
        assertEquals("El cliente con ID 1 no existe", exception.getMessage());

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate, never()).getForObject(URL_PRODUCTO_1, ProductoDTO.class);
        verify(carritoRepository, never()).save(any(Carrito.class));
    }

    @Test
    void guardarCarrito_cuandoUsuarioServiceNoResponde_deberiaLanzarRuntimeException() {
        //given
        CarritoDTO carritoDTO = crearCarritoDTO(null, true);

        when(restTemplate.getForObject(URL_USUARIO_1, UsuarioDTO.class))
                .thenThrow(new RestClientException("Error de conexión"));

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> carritoService.guardarCarrito(carritoDTO)
        );

        //then
        assertEquals("No se pudo validar el cliente con ID 1", exception.getMessage());

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate, never()).getForObject(URL_PRODUCTO_1, ProductoDTO.class);
        verify(carritoRepository, never()).save(any(Carrito.class));
    }

    @Test
    void guardarCarrito_cuandoClienteRetornaNull_deberiaLanzarRuntimeException() {
        //given
        CarritoDTO carritoDTO = crearCarritoDTO(null, true);

        when(restTemplate.getForObject(URL_USUARIO_1, UsuarioDTO.class)).thenReturn(null);

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> carritoService.guardarCarrito(carritoDTO)
        );

        //then
        assertEquals("El cliente con ID 1 no existe", exception.getMessage());

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate, never()).getForObject(URL_PRODUCTO_1, ProductoDTO.class);
        verify(carritoRepository, never()).save(any(Carrito.class));
    }

    @Test
    void guardarCarrito_cuandoClienteEstaInactivo_deberiaLanzarRuntimeException() {
        //given
        CarritoDTO carritoDTO = crearCarritoDTO(null, true);

        when(restTemplate.getForObject(URL_USUARIO_1, UsuarioDTO.class)).thenReturn(crearUsuarioDTO(false));

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> carritoService.guardarCarrito(carritoDTO)
        );

        //then
        assertEquals("El cliente con ID 1 está inactivo", exception.getMessage());

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate, never()).getForObject(URL_PRODUCTO_1, ProductoDTO.class);
        verify(carritoRepository, never()).save(any(Carrito.class));
    }

    @Test
    void guardarCarrito_cuandoProductoNoExiste_deberiaLanzarRuntimeException() {
        //given
        CarritoDTO carritoDTO = crearCarritoDTO(null, true);

        simularClienteValido();
        when(restTemplate.getForObject(URL_PRODUCTO_1, ProductoDTO.class)).thenThrow(crearErrorNotFound());

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> carritoService.guardarCarrito(carritoDTO)
        );

        //then
        assertEquals("El producto con ID 1 no existe", exception.getMessage());

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate).getForObject(URL_PRODUCTO_1, ProductoDTO.class);
        verify(carritoRepository, never()).save(any(Carrito.class));
    }

    @Test
    void guardarCarrito_cuandoProductoRetornaNull_deberiaLanzarRuntimeException() {
        //given
        CarritoDTO carritoDTO = crearCarritoDTO(null, true);

        simularClienteValido();
        when(restTemplate.getForObject(URL_PRODUCTO_1, ProductoDTO.class)).thenReturn(null);

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> carritoService.guardarCarrito(carritoDTO)
        );

        //then
        assertEquals("El producto con ID 1 no existe", exception.getMessage());

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate).getForObject(URL_PRODUCTO_1, ProductoDTO.class);
        verify(carritoRepository, never()).save(any(Carrito.class));
    }

    @Test
    void guardarCarrito_cuandoProductoNoDisponible_deberiaLanzarRuntimeException() {
        //given
        CarritoDTO carritoDTO = crearCarritoDTO(null, true);

        simularClienteValido();
        when(restTemplate.getForObject(URL_PRODUCTO_1, ProductoDTO.class)).thenReturn(crearProductoDTO(false, 10));

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> carritoService.guardarCarrito(carritoDTO)
        );

        //then
        assertEquals("El producto con ID 1 no está disponible", exception.getMessage());

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate).getForObject(URL_PRODUCTO_1, ProductoDTO.class);
        verify(carritoRepository, never()).save(any(Carrito.class));
    }

    @Test
    void guardarCarrito_cuandoNoHayStockSuficiente_deberiaLanzarRuntimeException() {
        //given
        CarritoDTO carritoDTO = crearCarritoDTO(null, true);

        simularClienteValido();
        when(restTemplate.getForObject(URL_PRODUCTO_1, ProductoDTO.class)).thenReturn(crearProductoDTO(true, 1));

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> carritoService.guardarCarrito(carritoDTO)
        );

        //then
        assertEquals("No hay stock suficiente para el producto con ID 1", exception.getMessage());

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate).getForObject(URL_PRODUCTO_1, ProductoDTO.class);
        verify(carritoRepository, never()).save(any(Carrito.class));
    }

    @Test
    void actualizarCarrito_deberiaActualizarYRetornarCarritoDTO() {
        //given
        CarritoDTO carritoDTO = crearCarritoDTO(1L, true);
        Carrito carritoGuardado = crearCarrito(1L, true);

        simularClienteValido();
        when(restTemplate.getForObject(URL_PRODUCTO_1, ProductoDTO.class)).thenReturn(crearProductoDTO(true, 10));
        when(carritoRepository.save(any(Carrito.class))).thenReturn(carritoGuardado);

        //when
        CarritoDTO resultado = carritoService.actualizarCarrito(carritoDTO);

        //then
        assertEquals(1L, resultado.getIdCarrito());
        assertEquals("EAU DE PARFUM ROSAS DEL SUR", resultado.getNombreProducto());

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate).getForObject(URL_PRODUCTO_1, ProductoDTO.class);
        verify(carritoRepository).save(any(Carrito.class));
    }

    @Test
    void existeCarritoPorId_cuandoExiste_deberiaRetornarTrue() {
        //given
        Long id = 1L;
        when(carritoRepository.existsById(id)).thenReturn(true);

        //when
        boolean resultado = carritoService.existeCarritoPorId(id);

        //then
        assertTrue(resultado);

        verify(carritoRepository).existsById(id);
    }

    @Test
    void existeCarritoPorId_cuandoNoExiste_deberiaRetornarFalse() {
        //given
        Long id = 99L;
        when(carritoRepository.existsById(id)).thenReturn(false);

        //when
        boolean resultado = carritoService.existeCarritoPorId(id);

        //then
        assertFalse(resultado);

        verify(carritoRepository).existsById(id);
    }

    @Test
    void eliminarCarrito_deberiaEliminarCarritoPorId() {
        //given
        Long id = 1L;

        //when
        carritoService.eliminarCarrito(id);

        //then
        verify(carritoRepository).deleteById(id);
    }

    @Test
    void listarCarritosPorCliente_deberiaRetornarCarritosDelCliente() {
        //given
        Long idCliente = 1L;
        Carrito carrito = crearCarrito(1L, true);

        when(carritoRepository.findByIdCliente(idCliente)).thenReturn(List.of(carrito));

        //when
        List<CarritoDTO> resultado = carritoService.listarCarritosPorCliente(idCliente);

        //then
        assertEquals(1, resultado.size());
        assertEquals(idCliente, resultado.get(0).getIdCliente());

        verify(carritoRepository).findByIdCliente(idCliente);
    }

    @Test
    void listarCarritosPorProducto_deberiaRetornarCarritosDelProducto() {
        //given
        Long idProducto = 1L;
        Carrito carrito = crearCarrito(1L, true);

        when(carritoRepository.findByIdProducto(idProducto)).thenReturn(List.of(carrito));

        //when
        List<CarritoDTO> resultado = carritoService.listarCarritosPorProducto(idProducto);

        //then
        assertEquals(1, resultado.size());
        assertEquals(idProducto, resultado.get(0).getIdProducto());

        verify(carritoRepository).findByIdProducto(idProducto);
    }

    @Test
    void listarCarritosActivosPorCliente_deberiaRetornarCarritosActivos() {
        //given
        Long idCliente = 1L;
        Carrito carrito = crearCarrito(1L, true);

        when(carritoRepository.findByIdClienteAndActivo(idCliente, true)).thenReturn(List.of(carrito));

        //when
        List<CarritoDTO> resultado = carritoService.listarCarritosActivosPorCliente(idCliente);

        //then
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getActivo());

        verify(carritoRepository).findByIdClienteAndActivo(idCliente, true);
    }

    @Test
    void calcularTotalCarritoPorCliente_deberiaRetornarTotal() {
        //given
        Long idCliente = 1L;
        Carrito carritoUno = crearCarrito(1L, true);
        Carrito carritoDos = new Carrito(
                2L,
                1L,
                2L,
                "EAU DE TOILETTE CITRUS FRESH",
                1,
                19990.0,
                19990.0,
                true
        );

        when(carritoRepository.findByIdClienteAndActivo(idCliente, true)).thenReturn(List.of(carritoUno, carritoDos));

        //when
        Double resultado = carritoService.calcularTotalCarritoPorCliente(idCliente);

        //then
        assertEquals(69970.0, resultado);

        verify(carritoRepository).findByIdClienteAndActivo(idCliente, true);
    }

    @Test
    void calcularTotalCarritoPorCliente_cuandoNoTieneProductosActivos_deberiaRetornarCero() {
        //given
        Long idCliente = 1L;

        when(carritoRepository.findByIdClienteAndActivo(idCliente, true)).thenReturn(List.of());

        //when
        Double resultado = carritoService.calcularTotalCarritoPorCliente(idCliente);

        //then
        assertEquals(0.0, resultado);

        verify(carritoRepository).findByIdClienteAndActivo(idCliente, true);
    }

    @Test
    void vaciarCarritoPorCliente_deberiaEliminarCarritoDelCliente() {
        //given
        Long idCliente = 1L;

        //when
        carritoService.vaciarCarritoPorCliente(idCliente);

        //then
        verify(carritoRepository).deleteByIdCliente(idCliente);
    }

    private void simularClienteValido() {
        when(restTemplate.getForObject(URL_USUARIO_1, UsuarioDTO.class)).thenReturn(crearUsuarioDTO(true));
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

    private Carrito crearCarrito(Long id, Boolean activo) {
        return new Carrito(
                id,
                1L,
                1L,
                "EAU DE PARFUM ROSAS DEL SUR",
                2,
                24990.0,
                49980.0,
                activo
        );
    }

    private CarritoDTO crearCarritoDTO(Long id, Boolean activo) {
        return new CarritoDTO(
                id,
                1L,
                1L,
                "EAU DE PARFUM ROSAS DEL SUR",
                2,
                24990.0,
                49980.0,
                activo
        );
    }

    private ProductoDTO crearProductoDTO(Boolean disponible, Integer stock) {
        return new ProductoDTO(
                1L,
                "EAU DE PARFUM ROSAS DEL SUR",
                "Perfume floral de larga duración para uso diario",
                "PERFUMERIA",
                stock,
                24990.0,
                disponible,
                1L
        );
    }
}