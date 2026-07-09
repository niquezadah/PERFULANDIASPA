package com.example.ventas_facturacion_service.service;

import java.util.HashMap;
import com.example.ventas_facturacion_service.dto.EstadoVentaDTO;
import com.example.ventas_facturacion_service.dto.UsuarioDTO;
import com.example.ventas_facturacion_service.dto.VentaFacturaDTO;
import com.example.ventas_facturacion_service.model.VentaFactura;
import com.example.ventas_facturacion_service.repository.VentaFacturaRepository;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaFacturaServiceTest {

    private static final String URL_USUARIO_1 = "http://localhost:8082/api/usuarios/1";
    private static final String URL_CARRITO_TOTAL_1 = "http://localhost:8094/api/v1/carrito/cliente/1/total";

    @Mock
    private VentaFacturaRepository ventaFacturaRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private VentaFacturaService ventaFacturaService;

    @Test
    void listarVentasFacturas_deberiaRetornarListaVentas() {
        //given
        VentaFactura ventaFactura = crearVentaFactura(1L, "PAGADA", true);
        when(ventaFacturaRepository.findAll()).thenReturn(List.of(ventaFactura));

        //when
        List<VentaFacturaDTO> resultado = ventaFacturaService.listarVentasFacturas();

        //then
        assertAll(
                () -> assertEquals(1, resultado.size()),
                () -> assertEquals(1L, resultado.get(0).getIdVenta()),
                () -> assertEquals(1L, resultado.get(0).getIdCliente()),
                () -> assertEquals(69970.0, resultado.get(0).getTotalVenta()),
                () -> assertEquals("TARJETA", resultado.get(0).getMetodoPago()),
                () -> assertEquals("PAGADA", resultado.get(0).getEstadoVenta()),
                () -> assertEquals("PF-1-20260623", resultado.get(0).getNumeroFactura()),
                () -> assertEquals("nicolas.quezada@perfulandia.cl", resultado.get(0).getCorreoCliente()),
                () -> assertTrue(resultado.get(0).getFacturada())
        );

        verify(ventaFacturaRepository).findAll();
    }

    @Test
    void buscarVentaFacturaPorId_cuandoExiste_deberiaRetornarVenta() {
        //given
        Long id = 1L;
        VentaFactura ventaFactura = crearVentaFactura(id, "PAGADA", true);

        when(ventaFacturaRepository.findById(id)).thenReturn(Optional.of(ventaFactura));

        //when
        Optional<VentaFacturaDTO> resultado = ventaFacturaService.buscarVentaFacturaPorId(id);

        //then
        assertTrue(resultado.isPresent());
        assertAll(
                () -> assertEquals(id, resultado.get().getIdVenta()),
                () -> assertEquals(1L, resultado.get().getIdCliente()),
                () -> assertEquals("PAGADA", resultado.get().getEstadoVenta()),
                () -> assertEquals(69970.0, resultado.get().getTotalVenta())
        );

        verify(ventaFacturaRepository).findById(id);
    }

    @Test
    void buscarVentaFacturaPorId_cuandoNoExiste_deberiaRetornarOptionalVacio() {
        //given
        Long id = 99L;

        when(ventaFacturaRepository.findById(id)).thenReturn(Optional.empty());

        //when
        Optional<VentaFacturaDTO> resultado = ventaFacturaService.buscarVentaFacturaPorId(id);

        //then
        assertTrue(resultado.isEmpty());

        verify(ventaFacturaRepository).findById(id);
    }

    @Test
    void guardarVentaFactura_cuandoClienteYCarritoSonValidos_deberiaGuardarVenta() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, "PAGADA", true);

        simularClienteValido();
        simularTotalCarrito(69970.0);

        when(ventaFacturaRepository.save(any(VentaFactura.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        VentaFacturaDTO resultado = ventaFacturaService.guardarVentaFactura(ventaDTO);

        //then
        assertAll(
                () -> assertEquals(1L, resultado.getIdCliente()),
                () -> assertNotNull(resultado.getFechaVenta()),
                () -> assertEquals(69970.0, resultado.getTotalVenta()),
                () -> assertEquals("TARJETA", resultado.getMetodoPago()),
                () -> assertEquals("PAGADA", resultado.getEstadoVenta()),
                () -> assertTrue(resultado.getNumeroFactura().startsWith("PF-1-")),
                () -> assertEquals("nicolas.quezada@perfulandia.cl", resultado.getCorreoCliente()),
                () -> assertEquals("Compra realizada desde carrito web.", resultado.getObservacion()),
                () -> assertTrue(resultado.getFacturada())
        );

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate).getForObject(URL_CARRITO_TOTAL_1, Map.class);
        verify(ventaFacturaRepository).save(any(VentaFactura.class));
    }

    @Test
    void guardarVentaFactura_cuandoEstadoEsNulo_deberiaGuardarEstadoPagada() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, null, true);

        simularClienteValido();
        simularTotalCarrito(69970.0);

        when(ventaFacturaRepository.save(any(VentaFactura.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        VentaFacturaDTO resultado = ventaFacturaService.guardarVentaFactura(ventaDTO);

        //then
        assertEquals("PAGADA", resultado.getEstadoVenta());

        verify(ventaFacturaRepository).save(any(VentaFactura.class));
    }

    @Test
    void guardarVentaFactura_cuandoEstadoEstaEnBlanco_deberiaGuardarEstadoPagada() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, "   ", true);

        simularClienteValido();
        simularTotalCarrito(69970.0);

        when(ventaFacturaRepository.save(any(VentaFactura.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        VentaFacturaDTO resultado = ventaFacturaService.guardarVentaFactura(ventaDTO);

        //then
        assertEquals("PAGADA", resultado.getEstadoVenta());

        verify(ventaFacturaRepository).save(any(VentaFactura.class));
    }

    @Test
    void guardarVentaFactura_cuandoEstadoTieneValor_deberiaGuardarEseEstado() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, "PENDIENTE", true);

        simularClienteValido();
        simularTotalCarrito(69970.0);

        when(ventaFacturaRepository.save(any(VentaFactura.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        VentaFacturaDTO resultado = ventaFacturaService.guardarVentaFactura(ventaDTO);

        //then
        assertEquals("PENDIENTE", resultado.getEstadoVenta());

        verify(ventaFacturaRepository).save(any(VentaFactura.class));
    }

    @Test
    void guardarVentaFactura_cuandoFacturadaEsNula_deberiaGuardarTrue() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, "PAGADA", null);

        simularClienteValido();
        simularTotalCarrito(69970.0);

        when(ventaFacturaRepository.save(any(VentaFactura.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        VentaFacturaDTO resultado = ventaFacturaService.guardarVentaFactura(ventaDTO);

        //then
        assertTrue(resultado.getFacturada());

        verify(ventaFacturaRepository).save(any(VentaFactura.class));
    }

    @Test
    void guardarVentaFactura_cuandoFacturadaEsFalse_deberiaGuardarFalse() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, "PAGADA", false);

        simularClienteValido();
        simularTotalCarrito(69970.0);

        when(ventaFacturaRepository.save(any(VentaFactura.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        VentaFacturaDTO resultado = ventaFacturaService.guardarVentaFactura(ventaDTO);

        //then
        assertFalse(resultado.getFacturada());

        verify(ventaFacturaRepository).save(any(VentaFactura.class));
    }

    @Test
    void actualizarVentaFactura_deberiaGuardarVentaActualizada() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(1L, "PAGADA", true);

        simularClienteValido();
        simularTotalCarrito(69970.0);

        when(ventaFacturaRepository.save(any(VentaFactura.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        VentaFacturaDTO resultado = ventaFacturaService.actualizarVentaFactura(ventaDTO);

        //then
        assertAll(
                () -> assertEquals(1L, resultado.getIdVenta()),
                () -> assertEquals(1L, resultado.getIdCliente()),
                () -> assertEquals("PAGADA", resultado.getEstadoVenta()),
                () -> assertEquals(69970.0, resultado.getTotalVenta())
        );

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate).getForObject(URL_CARRITO_TOTAL_1, Map.class);
        verify(ventaFacturaRepository).save(any(VentaFactura.class));
    }

    @Test
    void existeVentaFacturaPorId_cuandoExiste_deberiaRetornarTrue() {
        //given
        Long id = 1L;

        when(ventaFacturaRepository.existsById(id)).thenReturn(true);

        //when
        boolean resultado = ventaFacturaService.existeVentaFacturaPorId(id);

        //then
        assertTrue(resultado);

        verify(ventaFacturaRepository).existsById(id);
    }

    @Test
    void existeVentaFacturaPorId_cuandoNoExiste_deberiaRetornarFalse() {
        //given
        Long id = 99L;

        when(ventaFacturaRepository.existsById(id)).thenReturn(false);

        //when
        boolean resultado = ventaFacturaService.existeVentaFacturaPorId(id);

        //then
        assertFalse(resultado);

        verify(ventaFacturaRepository).existsById(id);
    }

    @Test
    void eliminarVentaFactura_deberiaEliminarPorId() {
        //given
        Long id = 1L;

        //when
        ventaFacturaService.eliminarVentaFactura(id);

        //then
        verify(ventaFacturaRepository).deleteById(id);
    }

    @Test
    void listarVentasPorCliente_deberiaRetornarVentasDelCliente() {
        //given
        Long idCliente = 1L;
        VentaFactura ventaFactura = crearVentaFactura(1L, "PAGADA", true);

        when(ventaFacturaRepository.findByIdCliente(idCliente)).thenReturn(List.of(ventaFactura));

        //when
        List<VentaFacturaDTO> resultado = ventaFacturaService.listarVentasPorCliente(idCliente);

        //then
        assertEquals(1, resultado.size());
        assertEquals(idCliente, resultado.get(0).getIdCliente());

        verify(ventaFacturaRepository).findByIdCliente(idCliente);
    }

    @Test
    void listarVentasPorEstado_deberiaRetornarVentasPorEstado() {
        //given
        String estado = "PAGADA";
        VentaFactura ventaFactura = crearVentaFactura(1L, estado, true);

        when(ventaFacturaRepository.findByEstadoVenta(estado)).thenReturn(List.of(ventaFactura));

        //when
        List<VentaFacturaDTO> resultado = ventaFacturaService.listarVentasPorEstado(estado);

        //then
        assertEquals(1, resultado.size());
        assertEquals(estado, resultado.get(0).getEstadoVenta());

        verify(ventaFacturaRepository).findByEstadoVenta(estado);
    }

    @Test
    void listarVentasFacturadas_deberiaRetornarVentasFacturadas() {
        //given
        VentaFactura ventaFactura = crearVentaFactura(1L, "PAGADA", true);

        when(ventaFacturaRepository.findByFacturada(true)).thenReturn(List.of(ventaFactura));

        //when
        List<VentaFacturaDTO> resultado = ventaFacturaService.listarVentasFacturadas();

        //then
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getFacturada());

        verify(ventaFacturaRepository).findByFacturada(true);
    }

    @Test
    void actualizarEstadoVenta_cuandoExiste_deberiaActualizarEstado() {
        //given
        Long id = 1L;
        VentaFactura ventaFactura = crearVentaFactura(id, "PAGADA", true);
        EstadoVentaDTO estadoVentaDTO = new EstadoVentaDTO("ANULADA");

        when(ventaFacturaRepository.findById(id)).thenReturn(Optional.of(ventaFactura));
        when(ventaFacturaRepository.save(any(VentaFactura.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        VentaFacturaDTO resultado = ventaFacturaService.actualizarEstadoVenta(id, estadoVentaDTO);

        //then
        assertEquals("ANULADA", resultado.getEstadoVenta());

        verify(ventaFacturaRepository).findById(id);
        verify(ventaFacturaRepository).save(ventaFactura);
    }

    @Test
    void actualizarEstadoVenta_cuandoNoExiste_deberiaLanzarRuntimeException() {
        //given
        Long id = 99L;
        EstadoVentaDTO estadoVentaDTO = new EstadoVentaDTO("ANULADA");

        when(ventaFacturaRepository.findById(id)).thenReturn(Optional.empty());

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> ventaFacturaService.actualizarEstadoVenta(id, estadoVentaDTO)
        );

        //then
        assertEquals("La venta con ID 99 no existe", exception.getMessage());

        verify(ventaFacturaRepository).findById(id);
        verify(ventaFacturaRepository, never()).save(any(VentaFactura.class));
    }

    @Test
    void guardarVentaFactura_cuandoClienteNoExiste_deberiaLanzarRuntimeException() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, "PAGADA", true);

        when(restTemplate.getForObject(URL_USUARIO_1, UsuarioDTO.class)).thenThrow(crearErrorNotFound());

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> ventaFacturaService.guardarVentaFactura(ventaDTO)
        );

        //then
        assertEquals("El cliente con ID 1 no existe", exception.getMessage());

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate, never()).getForObject(URL_CARRITO_TOTAL_1, Map.class);
        verify(ventaFacturaRepository, never()).save(any(VentaFactura.class));
    }

    @Test
    void guardarVentaFactura_cuandoUsuarioServiceNoResponde_deberiaLanzarRuntimeException() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, "PAGADA", true);

        when(restTemplate.getForObject(URL_USUARIO_1, UsuarioDTO.class))
                .thenThrow(new RestClientException("Error de conexión"));

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> ventaFacturaService.guardarVentaFactura(ventaDTO)
        );

        //then
        assertEquals("No se pudo validar el cliente con ID 1", exception.getMessage());

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate, never()).getForObject(URL_CARRITO_TOTAL_1, Map.class);
        verify(ventaFacturaRepository, never()).save(any(VentaFactura.class));
    }

    @Test
    void guardarVentaFactura_cuandoClienteRetornaNull_deberiaLanzarRuntimeException() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, "PAGADA", true);

        when(restTemplate.getForObject(URL_USUARIO_1, UsuarioDTO.class)).thenReturn(null);

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> ventaFacturaService.guardarVentaFactura(ventaDTO)
        );

        //then
        assertEquals("El cliente con ID 1 no existe", exception.getMessage());

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate, never()).getForObject(URL_CARRITO_TOTAL_1, Map.class);
        verify(ventaFacturaRepository, never()).save(any(VentaFactura.class));
    }

    @Test
    void guardarVentaFactura_cuandoClienteEstaInactivo_deberiaLanzarRuntimeException() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, "PAGADA", true);

        when(restTemplate.getForObject(URL_USUARIO_1, UsuarioDTO.class)).thenReturn(crearUsuarioDTO(false));

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> ventaFacturaService.guardarVentaFactura(ventaDTO)
        );

        //then
        assertEquals("El cliente con ID 1 está inactivo", exception.getMessage());

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate, never()).getForObject(URL_CARRITO_TOTAL_1, Map.class);
        verify(ventaFacturaRepository, never()).save(any(VentaFactura.class));
    }

    @Test
    void guardarVentaFactura_cuandoCarritoNoExiste_deberiaLanzarRuntimeException() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, "PAGADA", true);

        simularClienteValido();
        when(restTemplate.getForObject(URL_CARRITO_TOTAL_1, Map.class)).thenThrow(crearErrorNotFound());

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> ventaFacturaService.guardarVentaFactura(ventaDTO)
        );

        //then
        assertEquals("El carrito del cliente con ID 1 no existe", exception.getMessage());

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate).getForObject(URL_CARRITO_TOTAL_1, Map.class);
        verify(ventaFacturaRepository, never()).save(any(VentaFactura.class));
    }

    @Test
    void guardarVentaFactura_cuandoCarritoServiceNoResponde_deberiaLanzarRuntimeException() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, "PAGADA", true);

        simularClienteValido();
        when(restTemplate.getForObject(URL_CARRITO_TOTAL_1, Map.class))
                .thenThrow(new RestClientException("Error de conexión"));

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> ventaFacturaService.guardarVentaFactura(ventaDTO)
        );

        //then
        assertEquals("No se pudo obtener el total del carrito del cliente con ID 1", exception.getMessage());

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate).getForObject(URL_CARRITO_TOTAL_1, Map.class);
        verify(ventaFacturaRepository, never()).save(any(VentaFactura.class));
    }

        @Test
        void guardarVentaFactura_cuandoRespuestaCarritoEsNula_deberiaLanzarRuntimeException() {
                //given
                VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, "PAGADA", true);

                simularClienteValido();
                when(restTemplate.getForObject(URL_CARRITO_TOTAL_1, Map.class)).thenReturn(null);

                //when
                RuntimeException exception = assertThrows(
                        RuntimeException.class,
                        () -> ventaFacturaService.guardarVentaFactura(ventaDTO)
                );

                //then
                assertEquals("No se pudo obtener el total del carrito del cliente con ID 1", exception.getMessage());

                verify(ventaFacturaRepository, never()).save(any(VentaFactura.class));
        }

@Test
void guardarVentaFactura_cuandoTotalCarritoEsNulo_deberiaLanzarRuntimeException() {
    //given
    VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, "PAGADA", true);

    Map<String, Object> respuesta = new HashMap<>();
    respuesta.put("total", null);

    simularClienteValido();
    when(restTemplate.getForObject(URL_CARRITO_TOTAL_1, Map.class)).thenReturn(respuesta);

    //when
    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> ventaFacturaService.guardarVentaFactura(ventaDTO)
    );

    //then
    assertEquals("No se pudo obtener el total del carrito del cliente con ID 1", exception.getMessage());

    verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
    verify(restTemplate).getForObject(URL_CARRITO_TOTAL_1, Map.class);
    verify(ventaFacturaRepository, never()).save(any(VentaFactura.class));
}

    @Test
    void guardarVentaFactura_cuandoTotalCarritoEsCero_deberiaLanzarRuntimeException() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, "PAGADA", true);

        simularClienteValido();
        simularTotalCarrito(0.0);

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> ventaFacturaService.guardarVentaFactura(ventaDTO)
        );

        //then
        assertEquals("El carrito del cliente con ID 1 está vacío", exception.getMessage());

        verify(ventaFacturaRepository, never()).save(any(VentaFactura.class));
    }

    @Test
    void guardarVentaFactura_cuandoTotalCarritoEsNegativo_deberiaLanzarRuntimeException() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, "PAGADA", true);

        simularClienteValido();
        simularTotalCarrito(-1000.0);

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> ventaFacturaService.guardarVentaFactura(ventaDTO)
        );

        //then
        assertEquals("El carrito del cliente con ID 1 está vacío", exception.getMessage());

        verify(ventaFacturaRepository, never()).save(any(VentaFactura.class));
    }

    private void simularClienteValido() {
        when(restTemplate.getForObject(URL_USUARIO_1, UsuarioDTO.class)).thenReturn(crearUsuarioDTO(true));
    }

    private void simularTotalCarrito(Double total) {
        when(restTemplate.getForObject(URL_CARRITO_TOTAL_1, Map.class)).thenReturn(Map.of("total", total));
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

    private VentaFactura crearVentaFactura(Long id, String estadoVenta, Boolean facturada) {
        return new VentaFactura(
                id,
                1L,
                LocalDateTime.of(2026, 6, 23, 12, 0),
                69970.0,
                "TARJETA",
                estadoVenta,
                "PF-1-20260623",
                "nicolas.quezada@perfulandia.cl",
                "Compra realizada desde carrito web.",
                facturada
        );
    }

    private VentaFacturaDTO crearVentaFacturaDTO(Long id, String estadoVenta, Boolean facturada) {
        return new VentaFacturaDTO(
                id,
                1L,
                null,
                null,
                "TARJETA",
                estadoVenta,
                null,
                "nicolas.quezada@perfulandia.cl",
                "Compra realizada desde carrito web.",
                facturada
        );
    }
}