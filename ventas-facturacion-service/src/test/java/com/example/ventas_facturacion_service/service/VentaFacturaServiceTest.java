package com.example.ventas_facturacion_service.service;

import com.example.ventas_facturacion_service.dto.EstadoVentaDTO;
import com.example.ventas_facturacion_service.dto.VentaFacturaDTO;
import com.example.ventas_facturacion_service.model.VentaFactura;
import com.example.ventas_facturacion_service.repository.VentaFacturaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaFacturaServiceTest {

    @Mock
    private VentaFacturaRepository ventaFacturaRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private VentaFacturaService ventaFacturaService;

    @Test
    void listarVentasFacturas_deberiaRetornarListaVentas() {
        //given
        VentaFactura venta = crearVentaFactura(1L, "PAGADA", true);

        when(ventaFacturaRepository.findAll()).thenReturn(List.of(venta));

        //when
        List<VentaFacturaDTO> resultado = ventaFacturaService.listarVentasFacturas();

        //then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getIdVenta());
        assertEquals(1L, resultado.get(0).getIdCliente());
        assertEquals("PAGADA", resultado.get(0).getEstadoVenta());
        assertTrue(resultado.get(0).getFacturada());
        verify(ventaFacturaRepository).findAll();
    }

    @Test
    void buscarVentaFacturaPorId_cuandoExiste_deberiaRetornarVenta() {
        //given
        VentaFactura venta = crearVentaFactura(1L, "PAGADA", true);

        when(ventaFacturaRepository.findById(1L)).thenReturn(Optional.of(venta));

        //when
        Optional<VentaFacturaDTO> resultado = ventaFacturaService.buscarVentaFacturaPorId(1L);

        //then
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdVenta());
        assertEquals("PAGADA", resultado.get().getEstadoVenta());
        verify(ventaFacturaRepository).findById(1L);
    }

    @Test
    void buscarVentaFacturaPorId_cuandoNoExiste_deberiaRetornarOptionalVacio() {
        //given
        when(ventaFacturaRepository.findById(99L)).thenReturn(Optional.empty());

        //when
        Optional<VentaFacturaDTO> resultado = ventaFacturaService.buscarVentaFacturaPorId(99L);

        //then
        assertTrue(resultado.isEmpty());
        verify(ventaFacturaRepository).findById(99L);
    }

    @Test
    void guardarVentaFactura_cuandoDatosValidos_deberiaGuardarVenta() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, "PAGADA", true);

        when(restTemplate.getForObject(
                "http://localhost:8094/api/v1/carrito/cliente/1/total",
                Map.class
        )).thenReturn(Map.of("total", 69970.0));

        when(ventaFacturaRepository.save(any(VentaFactura.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        VentaFacturaDTO resultado = ventaFacturaService.guardarVentaFactura(ventaDTO);

        //then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdCliente());
        assertEquals(69970.0, resultado.getTotalVenta());
        assertEquals("TARJETA", resultado.getMetodoPago());
        assertEquals("PAGADA", resultado.getEstadoVenta());
        assertTrue(resultado.getFacturada());
        assertNotNull(resultado.getFechaVenta());
        assertTrue(resultado.getNumeroFactura().startsWith("PF-1-"));
        verify(restTemplate).getForObject(
                "http://localhost:8094/api/v1/carrito/cliente/1/total",
                Map.class
        );
        verify(ventaFacturaRepository).save(any(VentaFactura.class));
    }

    @Test
    void guardarVentaFactura_cuandoEstadoEsNulo_deberiaAsignarPagada() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, null, true);

        when(restTemplate.getForObject(
                "http://localhost:8094/api/v1/carrito/cliente/1/total",
                Map.class
        )).thenReturn(Map.of("total", 69970.0));

        when(ventaFacturaRepository.save(any(VentaFactura.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        VentaFacturaDTO resultado = ventaFacturaService.guardarVentaFactura(ventaDTO);

        //then
        assertNotNull(resultado);
        assertEquals("PAGADA", resultado.getEstadoVenta());
        assertTrue(resultado.getFacturada());
    }

    @Test
    void guardarVentaFactura_cuandoEstadoEstaEnBlanco_deberiaAsignarPagada() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, "   ", true);

        when(restTemplate.getForObject(
                "http://localhost:8094/api/v1/carrito/cliente/1/total",
                Map.class
        )).thenReturn(Map.of("total", 69970.0));

        when(ventaFacturaRepository.save(any(VentaFactura.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        VentaFacturaDTO resultado = ventaFacturaService.guardarVentaFactura(ventaDTO);

        //then
        assertNotNull(resultado);
        assertEquals("PAGADA", resultado.getEstadoVenta());
    }

    @Test
    void guardarVentaFactura_cuandoEstadoVieneConValor_deberiaMantenerEstado() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, "PENDIENTE", true);

        when(restTemplate.getForObject(
                "http://localhost:8094/api/v1/carrito/cliente/1/total",
                Map.class
        )).thenReturn(Map.of("total", 69970.0));

        when(ventaFacturaRepository.save(any(VentaFactura.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        VentaFacturaDTO resultado = ventaFacturaService.guardarVentaFactura(ventaDTO);

        //then
        assertNotNull(resultado);
        assertEquals("PENDIENTE", resultado.getEstadoVenta());
    }

    @Test
    void guardarVentaFactura_cuandoFacturadaEsNula_deberiaAsignarTrue() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, "PAGADA", null);

        when(restTemplate.getForObject(
                "http://localhost:8094/api/v1/carrito/cliente/1/total",
                Map.class
        )).thenReturn(Map.of("total", 69970.0));

        when(ventaFacturaRepository.save(any(VentaFactura.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        VentaFacturaDTO resultado = ventaFacturaService.guardarVentaFactura(ventaDTO);

        //then
        assertNotNull(resultado);
        assertTrue(resultado.getFacturada());
    }

    @Test
    void guardarVentaFactura_cuandoFacturadaEsFalse_deberiaMantenerFalse() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, "PENDIENTE", false);

        when(restTemplate.getForObject(
                "http://localhost:8094/api/v1/carrito/cliente/1/total",
                Map.class
        )).thenReturn(Map.of("total", 69970.0));

        when(ventaFacturaRepository.save(any(VentaFactura.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        VentaFacturaDTO resultado = ventaFacturaService.guardarVentaFactura(ventaDTO);

        //then
        assertNotNull(resultado);
        assertFalse(resultado.getFacturada());
    }

    @Test
    void actualizarVentaFactura_deberiaGuardarVentaActualizada() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(1L, "PENDIENTE", false);

        when(restTemplate.getForObject(
                "http://localhost:8094/api/v1/carrito/cliente/1/total",
                Map.class
        )).thenReturn(Map.of("total", 69970.0));

        when(ventaFacturaRepository.save(any(VentaFactura.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        VentaFacturaDTO resultado = ventaFacturaService.actualizarVentaFactura(ventaDTO);

        //then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdVenta());
        assertEquals("PENDIENTE", resultado.getEstadoVenta());
        assertFalse(resultado.getFacturada());
        verify(ventaFacturaRepository).save(any(VentaFactura.class));
    }

    @Test
    void existeVentaFacturaPorId_cuandoExiste_deberiaRetornarTrue() {
        //given
        when(ventaFacturaRepository.existsById(1L)).thenReturn(true);

        //when
        boolean resultado = ventaFacturaService.existeVentaFacturaPorId(1L);

        //then
        assertTrue(resultado);
        verify(ventaFacturaRepository).existsById(1L);
    }

    @Test
    void existeVentaFacturaPorId_cuandoNoExiste_deberiaRetornarFalse() {
        //given
        when(ventaFacturaRepository.existsById(99L)).thenReturn(false);

        //when
        boolean resultado = ventaFacturaService.existeVentaFacturaPorId(99L);

        //then
        assertFalse(resultado);
        verify(ventaFacturaRepository).existsById(99L);
    }

    @Test
    void eliminarVentaFactura_deberiaEliminarPorId() {
        //given
        doNothing().when(ventaFacturaRepository).deleteById(1L);

        //when
        ventaFacturaService.eliminarVentaFactura(1L);

        //then
        verify(ventaFacturaRepository).deleteById(1L);
    }

    @Test
    void listarVentasPorCliente_deberiaRetornarListaVentasCliente() {
        //given
        VentaFactura venta = crearVentaFactura(1L, "PAGADA", true);

        when(ventaFacturaRepository.findByIdCliente(1L)).thenReturn(List.of(venta));

        //when
        List<VentaFacturaDTO> resultado = ventaFacturaService.listarVentasPorCliente(1L);

        //then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getIdCliente());
        verify(ventaFacturaRepository).findByIdCliente(1L);
    }

    @Test
    void listarVentasPorEstado_deberiaRetornarListaVentasEstado() {
        //given
        VentaFactura venta = crearVentaFactura(1L, "PAGADA", true);

        when(ventaFacturaRepository.findByEstadoVenta("PAGADA")).thenReturn(List.of(venta));

        //when
        List<VentaFacturaDTO> resultado = ventaFacturaService.listarVentasPorEstado("PAGADA");

        //then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("PAGADA", resultado.get(0).getEstadoVenta());
        verify(ventaFacturaRepository).findByEstadoVenta("PAGADA");
    }

    @Test
    void listarVentasFacturadas_deberiaRetornarListaVentasFacturadas() {
        //given
        VentaFactura venta = crearVentaFactura(1L, "PAGADA", true);

        when(ventaFacturaRepository.findByFacturada(true)).thenReturn(List.of(venta));

        //when
        List<VentaFacturaDTO> resultado = ventaFacturaService.listarVentasFacturadas();

        //then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getFacturada());
        verify(ventaFacturaRepository).findByFacturada(true);
    }

    @Test
    void actualizarEstadoVenta_cuandoExiste_deberiaActualizarEstado() {
        //given
        VentaFactura venta = crearVentaFactura(1L, "PAGADA", true);
        EstadoVentaDTO estadoVentaDTO = new EstadoVentaDTO("ANULADA");

        when(ventaFacturaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(ventaFacturaRepository.save(any(VentaFactura.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        VentaFacturaDTO resultado = ventaFacturaService.actualizarEstadoVenta(1L, estadoVentaDTO);

        //then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdVenta());
        assertEquals("ANULADA", resultado.getEstadoVenta());
        verify(ventaFacturaRepository).findById(1L);
        verify(ventaFacturaRepository).save(any(VentaFactura.class));
    }

    @Test
    void actualizarEstadoVenta_cuandoNoExiste_deberiaLanzarRuntimeException() {
        //given
        EstadoVentaDTO estadoVentaDTO = new EstadoVentaDTO("ANULADA");

        when(ventaFacturaRepository.findById(99L)).thenReturn(Optional.empty());

        //when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> ventaFacturaService.actualizarEstadoVenta(99L, estadoVentaDTO));

        //then
        assertEquals("La venta con ID 99 no existe", exception.getMessage());
        verify(ventaFacturaRepository).findById(99L);
        verify(ventaFacturaRepository, never()).save(any(VentaFactura.class));
    }

    @Test
    void guardarVentaFactura_cuandoCarritoNoExiste_deberiaLanzarRuntimeException() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, "PAGADA", true);

        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                null,
                null,
                null
        );

        when(restTemplate.getForObject(
                "http://localhost:8094/api/v1/carrito/cliente/1/total",
                Map.class
        )).thenThrow(exception);

        //when
        RuntimeException resultado = assertThrows(RuntimeException.class,
                () -> ventaFacturaService.guardarVentaFactura(ventaDTO));

        //then
        assertEquals("El carrito del cliente con ID 1 no existe", resultado.getMessage());
        verify(ventaFacturaRepository, never()).save(any(VentaFactura.class));
    }

    @Test
    void guardarVentaFactura_cuandoRespuestaEsNula_deberiaLanzarRuntimeException() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, "PAGADA", true);

        when(restTemplate.getForObject(
                "http://localhost:8094/api/v1/carrito/cliente/1/total",
                Map.class
        )).thenReturn(null);

        //when
        RuntimeException resultado = assertThrows(RuntimeException.class,
                () -> ventaFacturaService.guardarVentaFactura(ventaDTO));

        //then
        assertEquals("No se pudo obtener el total del carrito del cliente con ID 1", resultado.getMessage());
        verify(ventaFacturaRepository, never()).save(any(VentaFactura.class));
    }

    @Test
    void guardarVentaFactura_cuandoTotalEsNulo_deberiaLanzarRuntimeException() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, "PAGADA", true);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("total", null);

        when(restTemplate.getForObject(
                "http://localhost:8094/api/v1/carrito/cliente/1/total",
                Map.class
        )).thenReturn(respuesta);

        //when
        RuntimeException resultado = assertThrows(RuntimeException.class,
                () -> ventaFacturaService.guardarVentaFactura(ventaDTO));

        //then
        assertEquals("No se pudo obtener el total del carrito del cliente con ID 1", resultado.getMessage());
        verify(ventaFacturaRepository, never()).save(any(VentaFactura.class));
    }

    @Test
    void guardarVentaFactura_cuandoTotalEsCero_deberiaLanzarRuntimeException() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, "PAGADA", true);

        when(restTemplate.getForObject(
                "http://localhost:8094/api/v1/carrito/cliente/1/total",
                Map.class
        )).thenReturn(Map.of("total", 0.0));

        //when
        RuntimeException resultado = assertThrows(RuntimeException.class,
                () -> ventaFacturaService.guardarVentaFactura(ventaDTO));

        //then
        assertEquals("El carrito del cliente con ID 1 está vacío", resultado.getMessage());
        verify(ventaFacturaRepository, never()).save(any(VentaFactura.class));
    }

    @Test
    void guardarVentaFactura_cuandoTotalEsNegativo_deberiaLanzarRuntimeException() {
        //given
        VentaFacturaDTO ventaDTO = crearVentaFacturaDTO(null, "PAGADA", true);

        when(restTemplate.getForObject(
                "http://localhost:8094/api/v1/carrito/cliente/1/total",
                Map.class
        )).thenReturn(Map.of("total", -1000.0));

        //when
        RuntimeException resultado = assertThrows(RuntimeException.class,
                () -> ventaFacturaService.guardarVentaFactura(ventaDTO));

        //then
        assertEquals("El carrito del cliente con ID 1 está vacío", resultado.getMessage());
        verify(ventaFacturaRepository, never()).save(any(VentaFactura.class));
    }

    private VentaFactura crearVentaFactura(Long id, String estado, Boolean facturada) {
        return new VentaFactura(
                id,
                1L,
                LocalDateTime.of(2026, 6, 23, 12, 0),
                69970.0,
                "TARJETA",
                estado,
                "PF-1-20260623",
                "cliente@correo.cl",
                "Compra realizada desde carrito web.",
                facturada
        );
    }

    private VentaFacturaDTO crearVentaFacturaDTO(Long id, String estado, Boolean facturada) {
        return new VentaFacturaDTO(
                id,
                1L,
                LocalDateTime.of(2026, 6, 23, 12, 0),
                69970.0,
                "TARJETA",
                estado,
                "PF-1-20260623",
                "cliente@correo.cl",
                "Compra realizada desde carrito web.",
                facturada
        );
    }
}