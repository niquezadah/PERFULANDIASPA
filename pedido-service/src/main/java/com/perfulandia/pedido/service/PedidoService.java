package com.perfulandia.pedido.service;

import com.perfulandia.pedido.dto.ActualizarEstadoPedidoRequest;
import com.perfulandia.pedido.dto.CrearPedidoRequest;
import com.perfulandia.pedido.dto.DetallePedidoRequest;
import com.perfulandia.pedido.dto.UsuarioDTO;
import com.perfulandia.pedido.exception.RecursoNoEncontradoException;
import com.perfulandia.pedido.exception.ReglaNegocioException;
import com.perfulandia.pedido.model.DetallePedido;
import com.perfulandia.pedido.model.EstadoPedido;
import com.perfulandia.pedido.model.Pedido;
import com.perfulandia.pedido.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final RestTemplate restTemplate;
    private final PedidoRepository pedidoRepository;

    @Value("${usuario.service.url}")
    private String usuarioServiceUrl;

    @Value("${tienda.service.url}")
    private String tiendaServiceUrl;

    @Value("${producto.service.url}")
    private String productoServiceUrl;

    public Pedido crearPedido(CrearPedidoRequest request) {

        if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
            throw new ReglaNegocioException("El pedido debe tener al menos un producto");
        }

        validarUsuarioActivo(request.getIdUsuario());
        validarTiendaExiste(request.getIdTienda());

        for (DetallePedidoRequest detalleRequest : request.getDetalles()) {
            validarProductoExiste(detalleRequest.getIdProducto());
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

    private void validarUsuarioActivo(Long idUsuario) {
        UsuarioDTO usuarioDTO;

        try {
            usuarioDTO = restTemplate.getForObject(usuarioServiceUrl + "/" + idUsuario, UsuarioDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ReglaNegocioException("El usuario con ID " + idUsuario + " no existe");
        } catch (RestClientException ex) {
            throw new ReglaNegocioException("No se pudo validar el usuario con ID " + idUsuario);
        }

        if (usuarioDTO == null) {
            throw new ReglaNegocioException("El usuario con ID " + idUsuario + " no existe");
        }

        if (!Boolean.TRUE.equals(usuarioDTO.getEstado())) {
            throw new ReglaNegocioException("El usuario con ID " + idUsuario + " está inactivo");
        }
    }

    private void validarTiendaExiste(Long idTienda) {
        try {
            restTemplate.getForObject(tiendaServiceUrl + "/" + idTienda, Object.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ReglaNegocioException("La tienda con ID " + idTienda + " no existe");
        } catch (RestClientException ex) {
            throw new ReglaNegocioException("No se pudo validar la tienda con ID " + idTienda);
        }
    }

    private void validarProductoExiste(Long idProducto) {
        try {
            restTemplate.getForObject(productoServiceUrl + "/" + idProducto, Object.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ReglaNegocioException("El producto con ID " + idProducto + " no existe");
        } catch (RestClientException ex) {
            throw new ReglaNegocioException("No se pudo validar el producto con ID " + idProducto);
        }
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public Pedido buscarPorId(Long idPedido) {
        return pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con ID: " + idPedido));
    }

    public List<Pedido> buscarPorUsuario(Long idUsuario) {
        validarUsuarioActivo(idUsuario);
        return pedidoRepository.findByIdUsuario(idUsuario);
    }

    public List<Pedido> buscarPorTienda(Long idTienda) {
        validarTiendaExiste(idTienda);
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