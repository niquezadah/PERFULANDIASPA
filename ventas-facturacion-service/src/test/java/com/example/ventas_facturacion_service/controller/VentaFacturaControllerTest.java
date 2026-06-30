package com.example.ventas_facturacion_service.controller;

import com.example.ventas_facturacion_service.dto.EstadoVentaDTO;
import com.example.ventas_facturacion_service.dto.VentaFacturaDTO;
import com.example.ventas_facturacion_service.service.VentaFacturaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaFacturaControllerDirectTest {

    @Mock
    private VentaFacturaService ventaFacturaService;

    private VentaFacturaController ventaFacturaController;

    @BeforeEach
    void setUp() {
        ventaFacturaController = new VentaFacturaController(ventaFacturaService);
    }

    @Test
    void listarVentasFacturas_deberiaRetornarLista() {
        //given
        VentaFacturaDTO venta = crearVentaFacturaDTO(1L, "PAGADA", true);

        when(ventaFacturaService.listarVentasFacturas()).thenReturn(List.of(venta));

        //when
        ResponseEntity<List<VentaFacturaDTO>> respuesta = ventaFacturaController.listarVentasFacturas();

        //then
        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals(1, respuesta.getBody().size());
        assertEquals(1L, respuesta.getBody().get(0).getIdVenta());
        verify(ventaFacturaService).listarVentasFacturas();
    }

    @Test
    void buscarVentaFacturaPorId_cuandoExiste_deberiaRetornarVenta() {
        //given
        VentaFacturaDTO venta = crearVentaFacturaDTO(1L, "PAGADA", true);

        when(ventaFacturaService.buscarVentaFacturaPorId(1L)).thenReturn(Optional.of(venta));

        //when
        ResponseEntity<VentaFacturaDTO> respuesta = ventaFacturaController.buscarVentaFacturaPorId(1L);

        //then
        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals(1L, respuesta.getBody().getIdVenta());
        verify(ventaFacturaService).buscarVentaFacturaPorId(1L);
    }

    @Test
    void buscarVentaFacturaPorId_cuandoNoExiste_deberiaRetornarNotFound() {
        //given
        when(ventaFacturaService.buscarVentaFacturaPorId(99L)).thenReturn(Optional.empty());

        //when
        ResponseEntity<VentaFacturaDTO> respuesta = ventaFacturaController.buscarVentaFacturaPorId(99L);

        //then
        assertEquals(404, respuesta.getStatusCode().value());
        assertNull(respuesta.getBody());
        verify(ventaFacturaService).buscarVentaFacturaPorId(99L);
    }

    @Test
    void crearVentaFactura_deberiaRetornarCreated() {
        //given
        VentaFacturaDTO ventaEntrada = crearVentaFacturaDTO(null, "PAGADA", true);
        VentaFacturaDTO ventaCreada = crearVentaFacturaDTO(1L, "PAGADA", true);

        when(ventaFacturaService.guardarVentaFactura(any(VentaFacturaDTO.class))).thenReturn(ventaCreada);

        //when
        ResponseEntity<VentaFacturaDTO> respuesta = ventaFacturaController.crearVentaFactura(ventaEntrada);

        //then
        assertEquals(201, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals(1L, respuesta.getBody().getIdVenta());
        assertEquals("PAGADA", respuesta.getBody().getEstadoVenta());
        verify(ventaFacturaService).guardarVentaFactura(any(VentaFacturaDTO.class));
    }

    @Test
    void actualizarVentaFactura_cuandoExiste_deberiaRetornarVentaActualizada() {
        //given
        VentaFacturaDTO ventaEntrada = crearVentaFacturaDTO(null, "PENDIENTE", false);
        VentaFacturaDTO ventaActualizada = crearVentaFacturaDTO(1L, "PENDIENTE", false);

        when(ventaFacturaService.existeVentaFacturaPorId(1L)).thenReturn(true);
        when(ventaFacturaService.actualizarVentaFactura(any(VentaFacturaDTO.class))).thenReturn(ventaActualizada);

        //when
        ResponseEntity<VentaFacturaDTO> respuesta = ventaFacturaController.actualizarVentaFactura(1L, ventaEntrada);

        //then
        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals(1L, ventaEntrada.getIdVenta());
        assertEquals("PENDIENTE", respuesta.getBody().getEstadoVenta());
        assertFalse(respuesta.getBody().getFacturada());
        verify(ventaFacturaService).existeVentaFacturaPorId(1L);
        verify(ventaFacturaService).actualizarVentaFactura(any(VentaFacturaDTO.class));
    }

    @Test
    void actualizarVentaFactura_cuandoNoExiste_deberiaRetornarNotFound() {
        //given
        VentaFacturaDTO ventaEntrada = crearVentaFacturaDTO(null, "PAGADA", true);

        when(ventaFacturaService.existeVentaFacturaPorId(99L)).thenReturn(false);

        //when
        ResponseEntity<VentaFacturaDTO> respuesta = ventaFacturaController.actualizarVentaFactura(99L, ventaEntrada);

        //then
        assertEquals(404, respuesta.getStatusCode().value());
        assertNull(respuesta.getBody());
        verify(ventaFacturaService).existeVentaFacturaPorId(99L);
        verify(ventaFacturaService, never()).actualizarVentaFactura(any(VentaFacturaDTO.class));
    }

    @Test
    void eliminarVentaFactura_cuandoExiste_deberiaRetornarNoContent() {
        //given
        when(ventaFacturaService.existeVentaFacturaPorId(1L)).thenReturn(true);
        doNothing().when(ventaFacturaService).eliminarVentaFactura(1L);

        //when
        ResponseEntity<Void> respuesta = ventaFacturaController.eliminarVentaFactura(1L);

        //then
        assertEquals(204, respuesta.getStatusCode().value());
        assertNull(respuesta.getBody());
        verify(ventaFacturaService).existeVentaFacturaPorId(1L);
        verify(ventaFacturaService).eliminarVentaFactura(1L);
    }

    @Test
    void eliminarVentaFactura_cuandoNoExiste_deberiaRetornarNotFound() {
        //given
        when(ventaFacturaService.existeVentaFacturaPorId(99L)).thenReturn(false);

        //when
        ResponseEntity<Void> respuesta = ventaFacturaController.eliminarVentaFactura(99L);

        //then
        assertEquals(404, respuesta.getStatusCode().value());
        assertNull(respuesta.getBody());
        verify(ventaFacturaService).existeVentaFacturaPorId(99L);
        verify(ventaFacturaService, never()).eliminarVentaFactura(99L);
    }

    @Test
    void listarVentasPorCliente_deberiaRetornarLista() {
        //given
        VentaFacturaDTO venta = crearVentaFacturaDTO(1L, "PAGADA", true);

        when(ventaFacturaService.listarVentasPorCliente(1L)).thenReturn(List.of(venta));

        //when
        ResponseEntity<List<VentaFacturaDTO>> respuesta = ventaFacturaController.listarVentasPorCliente(1L);

        //then
        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals(1, respuesta.getBody().size());
        assertEquals(1L, respuesta.getBody().get(0).getIdCliente());
        verify(ventaFacturaService).listarVentasPorCliente(1L);
    }

    @Test
    void listarVentasPorEstado_deberiaRetornarLista() {
        //given
        VentaFacturaDTO venta = crearVentaFacturaDTO(1L, "PAGADA", true);

        when(ventaFacturaService.listarVentasPorEstado("PAGADA")).thenReturn(List.of(venta));

        //when
        ResponseEntity<List<VentaFacturaDTO>> respuesta = ventaFacturaController.listarVentasPorEstado("PAGADA");

        //then
        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals(1, respuesta.getBody().size());
        assertEquals("PAGADA", respuesta.getBody().get(0).getEstadoVenta());
        verify(ventaFacturaService).listarVentasPorEstado("PAGADA");
    }

    @Test
    void listarVentasFacturadas_deberiaRetornarLista() {
        //given
        VentaFacturaDTO venta = crearVentaFacturaDTO(1L, "PAGADA", true);

        when(ventaFacturaService.listarVentasFacturadas()).thenReturn(List.of(venta));

        //when
        ResponseEntity<List<VentaFacturaDTO>> respuesta = ventaFacturaController.listarVentasFacturadas();

        //then
        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals(1, respuesta.getBody().size());
        assertTrue(respuesta.getBody().get(0).getFacturada());
        verify(ventaFacturaService).listarVentasFacturadas();
    }

    @Test
    void actualizarEstadoVenta_deberiaRetornarVentaActualizada() {
        //given
        EstadoVentaDTO estadoVentaDTO = new EstadoVentaDTO("ANULADA");
        VentaFacturaDTO ventaActualizada = crearVentaFacturaDTO(1L, "ANULADA", true);

        when(ventaFacturaService.actualizarEstadoVenta(1L, estadoVentaDTO)).thenReturn(ventaActualizada);

        //when
        ResponseEntity<VentaFacturaDTO> respuesta = ventaFacturaController.actualizarEstadoVenta(1L, estadoVentaDTO);

        //then
        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals(1L, respuesta.getBody().getIdVenta());
        assertEquals("ANULADA", respuesta.getBody().getEstadoVenta());
        verify(ventaFacturaService).actualizarEstadoVenta(1L, estadoVentaDTO);
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