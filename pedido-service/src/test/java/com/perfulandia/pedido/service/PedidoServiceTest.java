package com.perfulandia.pedido.service;

import com.perfulandia.pedido.dto.ActualizarEstadoPedidoRequest;
import com.perfulandia.pedido.dto.CrearPedidoRequest;
import com.perfulandia.pedido.dto.DetallePedidoRequest;
import com.perfulandia.pedido.exception.RecursoNoEncontradoException;
import com.perfulandia.pedido.exception.ReglaNegocioException;
import com.perfulandia.pedido.model.EstadoPedido;
import com.perfulandia.pedido.model.Pedido;
import com.perfulandia.pedido.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void crearPedido_deberiaCrearPedidoConTotalCorrecto() {
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

        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = pedidoService.crearPedido(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdUsuario());
        assertEquals(1L, resultado.getIdTienda());
        assertEquals(EstadoPedido.PENDIENTE, resultado.getEstado());
        assertEquals(25000, resultado.getTotal());
        assertEquals(2, resultado.getDetalles().size());

        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void crearPedido_sinDetalles_deberiaLanzarReglaNegocioException() {
        CrearPedidoRequest request = new CrearPedidoRequest();
        request.setIdUsuario(1L);
        request.setIdTienda(1L);
        request.setDetalles(List.of());

        ReglaNegocioException exception = assertThrows(
                ReglaNegocioException.class,
                () -> pedidoService.crearPedido(request)
        );

        assertEquals("El pedido debe tener al menos un producto", exception.getMessage());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void listarPedidos_deberiaRetornarListaDePedidos() {
        Pedido pedido1 = Pedido.builder()
                .idPedido(1L)
                .idUsuario(1L)
                .idTienda(1L)
                .estado(EstadoPedido.PENDIENTE)
                .total(10000)
                .build();

        Pedido pedido2 = Pedido.builder()
                .idPedido(2L)
                .idUsuario(2L)
                .idTienda(1L)
                .estado(EstadoPedido.CONFIRMADO)
                .total(20000)
                .build();

        when(pedidoRepository.findAll()).thenReturn(List.of(pedido1, pedido2));

        List<Pedido> resultado = pedidoService.listarPedidos();

        assertEquals(2, resultado.size());
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_cuandoExiste_deberiaRetornarPedido() {
        Pedido pedido = Pedido.builder()
                .idPedido(1L)
                .idUsuario(1L)
                .idTienda(1L)
                .estado(EstadoPedido.PENDIENTE)
                .total(15000)
                .build();

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        Pedido resultado = pedidoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdPedido());
        assertEquals(15000, resultado.getTotal());

        verify(pedidoRepository, times(1)).findById(1L);
    }

    @Test
    void buscarPorId_cuandoNoExiste_deberiaLanzarRecursoNoEncontradoException() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(
                RecursoNoEncontradoException.class,
                () -> pedidoService.buscarPorId(99L)
        );

        assertEquals("Pedido no encontrado con ID: 99", exception.getMessage());
        verify(pedidoRepository, times(1)).findById(99L);
    }

    @Test
    void buscarPorUsuario_deberiaRetornarPedidosDelUsuario() {
        Pedido pedido = Pedido.builder()
                .idPedido(1L)
                .idUsuario(5L)
                .idTienda(1L)
                .estado(EstadoPedido.PENDIENTE)
                .total(12000)
                .build();

        when(pedidoRepository.findByIdUsuario(5L)).thenReturn(List.of(pedido));

        List<Pedido> resultado = pedidoService.buscarPorUsuario(5L);

        assertEquals(1, resultado.size());
        assertEquals(5L, resultado.get(0).getIdUsuario());

        verify(pedidoRepository, times(1)).findByIdUsuario(5L);
    }

    @Test
    void buscarPorTienda_deberiaRetornarPedidosDeLaTienda() {
        Pedido pedido = Pedido.builder()
                .idPedido(1L)
                .idUsuario(1L)
                .idTienda(3L)
                .estado(EstadoPedido.PENDIENTE)
                .total(18000)
                .build();

        when(pedidoRepository.findByIdTienda(3L)).thenReturn(List.of(pedido));

        List<Pedido> resultado = pedidoService.buscarPorTienda(3L);

        assertEquals(1, resultado.size());
        assertEquals(3L, resultado.get(0).getIdTienda());

        verify(pedidoRepository, times(1)).findByIdTienda(3L);
    }

    @Test
    void buscarPorEstado_deberiaRetornarPedidosPorEstado() {
        Pedido pedido = Pedido.builder()
                .idPedido(1L)
                .idUsuario(1L)
                .idTienda(1L)
                .estado(EstadoPedido.CONFIRMADO)
                .total(30000)
                .build();

        when(pedidoRepository.findByEstado(EstadoPedido.CONFIRMADO)).thenReturn(List.of(pedido));

        List<Pedido> resultado = pedidoService.buscarPorEstado(EstadoPedido.CONFIRMADO);

        assertEquals(1, resultado.size());
        assertEquals(EstadoPedido.CONFIRMADO, resultado.get(0).getEstado());

        verify(pedidoRepository, times(1)).findByEstado(EstadoPedido.CONFIRMADO);
    }

    @Test
    void actualizarEstado_deberiaActualizarEstadoCorrectamente() {
        Pedido pedido = Pedido.builder()
                .idPedido(1L)
                .idUsuario(1L)
                .idTienda(1L)
                .estado(EstadoPedido.PENDIENTE)
                .total(10000)
                .build();

        ActualizarEstadoPedidoRequest request = new ActualizarEstadoPedidoRequest();
        request.setEstado(EstadoPedido.CONFIRMADO);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = pedidoService.actualizarEstado(1L, request);

        assertEquals(EstadoPedido.CONFIRMADO, resultado.getEstado());
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    void actualizarEstado_pedidoCancelado_deberiaLanzarReglaNegocioException() {
        Pedido pedido = Pedido.builder()
                .idPedido(1L)
                .idUsuario(1L)
                .idTienda(1L)
                .estado(EstadoPedido.CANCELADO)
                .total(10000)
                .build();

        ActualizarEstadoPedidoRequest request = new ActualizarEstadoPedidoRequest();
        request.setEstado(EstadoPedido.CONFIRMADO);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        ReglaNegocioException exception = assertThrows(
                ReglaNegocioException.class,
                () -> pedidoService.actualizarEstado(1L, request)
        );

        assertEquals("No se puede modificar un pedido cancelado", exception.getMessage());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void cancelarPedido_deberiaCambiarEstadoACancelado() {
        Pedido pedido = Pedido.builder()
                .idPedido(1L)
                .idUsuario(1L)
                .idTienda(1L)
                .estado(EstadoPedido.PENDIENTE)
                .total(10000)
                .build();

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = pedidoService.cancelarPedido(1L);

        assertEquals(EstadoPedido.CANCELADO, resultado.getEstado());
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    void cancelarPedido_entregado_deberiaLanzarReglaNegocioException() {
        Pedido pedido = Pedido.builder()
                .idPedido(1L)
                .idUsuario(1L)
                .idTienda(1L)
                .estado(EstadoPedido.ENTREGADO)
                .total(10000)
                .build();

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        ReglaNegocioException exception = assertThrows(
                ReglaNegocioException.class,
                () -> pedidoService.cancelarPedido(1L)
        );

        assertEquals("No se puede cancelar un pedido entregado", exception.getMessage());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }




    @Test
        void actualizarEstado_pedidoEntregado_deberiaLanzarReglaNegocioException() {
         Pedido pedido = Pedido.builder()
            .idPedido(1L)
            .idUsuario(1L)
            .idTienda(1L)
            .estado(EstadoPedido.ENTREGADO)
            .total(10000)
            .build();

        ActualizarEstadoPedidoRequest request = new ActualizarEstadoPedidoRequest();
        request.setEstado(EstadoPedido.CONFIRMADO);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

                ReglaNegocioException exception = assertThrows(
                ReglaNegocioException.class,
            () -> pedidoService.actualizarEstado(1L, request)
        );

         assertEquals("No se puede modificar un pedido entregado", exception.getMessage());

         verify(pedidoRepository, never()).save(any(Pedido.class));
        }


        @Test
        void crearPedido_conDetallesNull_deberiaLanzarReglaNegocioException() {
        CrearPedidoRequest request = new CrearPedidoRequest();
        request.setIdUsuario(1L);
        request.setIdTienda(1L);
         request.setDetalles(null);

        ReglaNegocioException exception = assertThrows(
            ReglaNegocioException.class,
            () -> pedidoService.crearPedido(request)
        );

         assertEquals("El pedido debe tener al menos un producto", exception.getMessage());

         verify(pedidoRepository, never()).save(any(Pedido.class));
        }
}