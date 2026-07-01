package com.perfulandia.pedido.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.perfulandia.pedido.model.EstadoPedido;
import com.perfulandia.pedido.model.Pedido;
@DataJpaTest
@ActiveProfiles("test")
class PedidoRepositoryTest {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Test
    void findByIdUsuario_deberiaRetornarPedidosDelUsuario() {
        Pedido pedido = Pedido.builder()
                .idUsuario(1L)
                .idTienda(1L)
                .estado(EstadoPedido.PENDIENTE)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .total(15000)
                .build();

        pedidoRepository.save(pedido);

        List<Pedido> resultado = pedidoRepository.findByIdUsuario(1L);

        assertFalse(resultado.isEmpty());
        assertEquals(1L, resultado.get(0).getIdUsuario());
    }

    @Test
    void findByIdTienda_deberiaRetornarPedidosDeLaTienda() {
        Pedido pedido = Pedido.builder()
                .idUsuario(3L)
                .idTienda(5L)
                .estado(EstadoPedido.PENDIENTE)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .total(18000)
                .build();

        pedidoRepository.save(pedido);

        List<Pedido> resultado = pedidoRepository.findByIdTienda(5L);

        assertFalse(resultado.isEmpty());
        assertEquals(5L, resultado.get(0).getIdTienda());
    }

    @Test
    void findByEstado_deberiaRetornarPedidosPorEstado() {
        Pedido pedido = Pedido.builder()
                .idUsuario(2L)
                .idTienda(1L)
                .estado(EstadoPedido.CONFIRMADO)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .total(20000)
                .build();

        pedidoRepository.save(pedido);

        List<Pedido> resultado = pedidoRepository.findByEstado(EstadoPedido.CONFIRMADO);

        assertFalse(resultado.isEmpty());
        assertEquals(EstadoPedido.CONFIRMADO, resultado.get(0).getEstado());
    }
}