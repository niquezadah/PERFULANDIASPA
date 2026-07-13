package com.perfulandia.logistica.repository;

import com.perfulandia.logistica.model.Envio;
import com.perfulandia.logistica.model.EstadoEnvio;
import com.perfulandia.logistica.model.TipoEntrega;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EnvioRepositoryTest {

    @Autowired
    private EnvioRepository envioRepository;

    @Test
    void save_deberiaGuardarEnvioCorrectamente() {
        Envio envio = crearEnvioBase(1L, 5L, 1L, EstadoEnvio.PENDIENTE);

        Envio resultado = envioRepository.save(envio);

        assertNotNull(resultado.getIdEnvio());
        assertEquals(1L, resultado.getIdPedido());
        assertEquals(5L, resultado.getIdCliente());
        assertEquals(1L, resultado.getIdTiendaOrigen());
        assertEquals(EstadoEnvio.PENDIENTE, resultado.getEstadoEnvio());
    }

    @Test
    void findByIdPedido_deberiaRetornarEnvioCuandoExistePedido() {
        Envio envio = crearEnvioBase(1L, 5L, 1L, EstadoEnvio.PENDIENTE);
        envioRepository.save(envio);

        Optional<Envio> resultado = envioRepository.findByIdPedido(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdPedido());
        assertEquals(5L, resultado.get().getIdCliente());
    }

    @Test
    void findByIdPedido_deberiaRetornarVacioCuandoNoExistePedido() {
        Optional<Envio> resultado = envioRepository.findByIdPedido(999L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void findByIdCliente_deberiaRetornarEnviosDelCliente() {
        envioRepository.save(crearEnvioBase(1L, 5L, 1L, EstadoEnvio.PENDIENTE));
        envioRepository.save(crearEnvioBase(2L, 5L, 2L, EstadoEnvio.EN_TRANSITO));
        envioRepository.save(crearEnvioBase(3L, 8L, 1L, EstadoEnvio.PENDIENTE));

        List<Envio> resultado = envioRepository.findByIdCliente(5L);

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(envio -> envio.getIdCliente().equals(5L)));
    }

    @Test
    void findByIdCliente_deberiaRetornarListaVaciaSiClienteNoTieneEnvios() {
        envioRepository.save(crearEnvioBase(1L, 5L, 1L, EstadoEnvio.PENDIENTE));

        List<Envio> resultado = envioRepository.findByIdCliente(999L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void findByEstadoEnvio_deberiaRetornarEnviosPorEstado() {
        envioRepository.save(crearEnvioBase(1L, 5L, 1L, EstadoEnvio.PENDIENTE));
        envioRepository.save(crearEnvioBase(2L, 6L, 1L, EstadoEnvio.PENDIENTE));
        envioRepository.save(crearEnvioBase(3L, 7L, 2L, EstadoEnvio.ENTREGADO));

        List<Envio> resultado = envioRepository.findByEstadoEnvio(EstadoEnvio.PENDIENTE);

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(envio -> envio.getEstadoEnvio() == EstadoEnvio.PENDIENTE));
    }

    @Test
    void findByEstadoEnvio_deberiaRetornarListaVaciaSiNoHayEnviosConEseEstado() {
        envioRepository.save(crearEnvioBase(1L, 5L, 1L, EstadoEnvio.PENDIENTE));

        List<Envio> resultado = envioRepository.findByEstadoEnvio(EstadoEnvio.CANCELADO);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void findByIdTiendaOrigen_deberiaRetornarEnviosPorTiendaOrigen() {
        envioRepository.save(crearEnvioBase(1L, 5L, 1L, EstadoEnvio.PENDIENTE));
        envioRepository.save(crearEnvioBase(2L, 6L, 1L, EstadoEnvio.EN_TRANSITO));
        envioRepository.save(crearEnvioBase(3L, 7L, 2L, EstadoEnvio.PENDIENTE));

        List<Envio> resultado = envioRepository.findByIdTiendaOrigen(1L);

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(envio -> envio.getIdTiendaOrigen().equals(1L)));
    }

    @Test
    void findByIdTiendaOrigen_deberiaRetornarListaVaciaSiNoHayEnviosDeEsaTienda() {
        envioRepository.save(crearEnvioBase(1L, 5L, 1L, EstadoEnvio.PENDIENTE));

        List<Envio> resultado = envioRepository.findByIdTiendaOrigen(999L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void delete_deberiaEliminarEnvioCorrectamente() {
        Envio envio = envioRepository.save(crearEnvioBase(1L, 5L, 1L, EstadoEnvio.PENDIENTE));

        envioRepository.delete(envio);

        Optional<Envio> resultado = envioRepository.findById(envio.getIdEnvio());

        assertTrue(resultado.isEmpty());
    }

    private Envio crearEnvioBase(
            Long idPedido,
            Long idCliente,
            Long idTiendaOrigen,
            EstadoEnvio estadoEnvio
    ) {
        Envio envio = new Envio();
        envio.setIdPedido(idPedido);
        envio.setIdCliente(idCliente);
        envio.setIdTiendaOrigen(idTiendaOrigen);
        envio.setDireccionDestino("Villa del Mar 123, Santiago");
        envio.setEstadoEnvio(estadoEnvio);
        envio.setTipoEntrega(TipoEntrega.DOMICILIO);
        envio.setFechaEntregaEstimada(LocalDateTime.of(2026, 7, 10, 15, 30));
        envio.setTransportista("Chilexpress");
        envio.setNumeroSeguimiento("PERF-" + idPedido);
        envio.setObservacion("Envío de prueba repository");
        return envio;
    }
}