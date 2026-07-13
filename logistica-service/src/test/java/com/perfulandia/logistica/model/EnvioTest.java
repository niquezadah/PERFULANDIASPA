package com.perfulandia.logistica.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EnvioTest {

    @Test
    void envio_deberiaCrearObjetoConConstructorVacioYSetters() {
        LocalDateTime fechaEstimada = LocalDateTime.of(2026, 7, 10, 15, 30);

        Envio envio = new Envio();
        envio.setIdEnvio(1L);
        envio.setIdPedido(10L);
        envio.setIdCliente(5L);
        envio.setIdTiendaOrigen(2L);
        envio.setDireccionDestino("Av. Providencia 1234");
        envio.setEstadoEnvio(EstadoEnvio.PENDIENTE);
        envio.setTipoEntrega(TipoEntrega.DOMICILIO);
        envio.setFechaCreacion(LocalDateTime.now());
        envio.setFechaEntregaEstimada(fechaEstimada);
        envio.setFechaEntregaReal(null);
        envio.setTransportista("Chilexpress");
        envio.setNumeroSeguimiento("PERF-001");
        envio.setObservacion("Entrega de prueba");

        assertEquals(1L, envio.getIdEnvio());
        assertEquals(10L, envio.getIdPedido());
        assertEquals(5L, envio.getIdCliente());
        assertEquals(2L, envio.getIdTiendaOrigen());
        assertEquals("Av. Providencia 1234", envio.getDireccionDestino());
        assertEquals(EstadoEnvio.PENDIENTE, envio.getEstadoEnvio());
        assertEquals(TipoEntrega.DOMICILIO, envio.getTipoEntrega());
        assertEquals(fechaEstimada, envio.getFechaEntregaEstimada());
        assertNull(envio.getFechaEntregaReal());
        assertEquals("Chilexpress", envio.getTransportista());
        assertEquals("PERF-001", envio.getNumeroSeguimiento());
        assertEquals("Entrega de prueba", envio.getObservacion());
    }

    @Test
    void prePersist_deberiaAsignarFechaCreacionYEstadoPendienteSiNoTieneEstado() {
        Envio envio = new Envio();

        envio.prePersist();

        assertNotNull(envio.getFechaCreacion());
        assertEquals(EstadoEnvio.PENDIENTE, envio.getEstadoEnvio());
    }

    @Test
    void prePersist_noDeberiaSobrescribirEstadoSiYaExiste() {
        Envio envio = new Envio();
        envio.setEstadoEnvio(EstadoEnvio.EN_TRANSITO);

        envio.prePersist();

        assertNotNull(envio.getFechaCreacion());
        assertEquals(EstadoEnvio.EN_TRANSITO, envio.getEstadoEnvio());
    }

    @Test
    void envio_deberiaPermitirFechaEntregaRealCuandoSeEntrega() {
        LocalDateTime fechaReal = LocalDateTime.of(2026, 7, 11, 18, 0);

        Envio envio = new Envio();
        envio.setEstadoEnvio(EstadoEnvio.ENTREGADO);
        envio.setFechaEntregaReal(fechaReal);

        assertEquals(EstadoEnvio.ENTREGADO, envio.getEstadoEnvio());
        assertEquals(fechaReal, envio.getFechaEntregaReal());
    }
}