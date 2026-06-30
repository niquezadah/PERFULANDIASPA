package com.perfulandia.pedido.service;

import com.perfulandia.pedido.dto.ActualizarEstadoPedidoRequest;
import com.perfulandia.pedido.dto.CrearPedidoRequest;
import com.perfulandia.pedido.dto.DetallePedidoRequest;
import com.perfulandia.pedido.exception.RecursoNoEncontradoException;
import com.perfulandia.pedido.exception.ReglaNegocioException;
import com.perfulandia.pedido.model.DetallePedido;
import com.perfulandia.pedido.model.EstadoPedido;
import com.perfulandia.pedido.model.Pedido;
import com.perfulandia.pedido.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public Pedido crearPedido(CrearPedidoRequest request) {

        if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
            throw new ReglaNegocioException("El pedido debe tener al menos un producto");
        }

        Pedido pedido = Pedido.builder()
                .idUsuario(request.getIdUsuario())
                .idTienda(request.getIdTienda())
                .estado(EstadoPedido.PENDIENTE)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .total(0)
                .build();

        int total = 0;

        for (DetallePedidoRequest detalleRequest : request.getDetalles()) {
            int subtotal = detalleRequest.getCantidad() * detalleRequest.getPrecioUnitario();

            DetallePedido detalle = DetallePedido.builder()
                    .pedido(pedido)
                    .idProducto(detalleRequest.getIdProducto())
                    .cantidad(detalleRequest.getCantidad())
                    .precioUnitario(detalleRequest.getPrecioUnitario())
                    .subtotal(subtotal)
                    .build();

            pedido.getDetalles().add(detalle);
            total += subtotal;
        }

        pedido.setTotal(total);

        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public Pedido buscarPorId(Long idPedido) {
        return pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con ID: " + idPedido));
    }

    public List<Pedido> buscarPorUsuario(Long idUsuario) {
        return pedidoRepository.findByIdUsuario(idUsuario);
    }

    public List<Pedido> buscarPorTienda(Long idTienda) {
        return pedidoRepository.findByIdTienda(idTienda);
    }

    public List<Pedido> buscarPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado);
    }

    public Pedido actualizarEstado(Long idPedido, ActualizarEstadoPedidoRequest request) {
        Pedido pedido = buscarPorId(idPedido);

        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new ReglaNegocioException("No se puede modificar un pedido cancelado");
        }

        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new ReglaNegocioException("No se puede modificar un pedido entregado");
        }

        pedido.setEstado(request.getEstado());
        pedido.setFechaActualizacion(LocalDateTime.now());

        return pedidoRepository.save(pedido);
    }

    public Pedido cancelarPedido(Long idPedido) {
        Pedido pedido = buscarPorId(idPedido);

        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new ReglaNegocioException("No se puede cancelar un pedido entregado");
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        pedido.setFechaActualizacion(LocalDateTime.now());

        return pedidoRepository.save(pedido);
    }
}