package com.perfulandia.logistica.service;

import com.perfulandia.logistica.client.PedidoClient;
import com.perfulandia.logistica.client.UsuarioClient;
import com.perfulandia.logistica.client.dto.PedidoResponse;
import com.perfulandia.logistica.dto.ActualizarEstadoEnvioRequest;
import com.perfulandia.logistica.dto.CrearEnvioRequest;
import com.perfulandia.logistica.exception.RecursoNoEncontradoException;
import com.perfulandia.logistica.exception.ReglaNegocioException;
import com.perfulandia.logistica.model.Envio;
import com.perfulandia.logistica.model.EstadoEnvio;
import com.perfulandia.logistica.repository.EnvioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class EnvioService {

    private final EnvioRepository envioRepository;
    private final PedidoClient pedidoClient;
    private final UsuarioClient usuarioClient;

    public EnvioService(
            EnvioRepository envioRepository,
            PedidoClient pedidoClient,
            UsuarioClient usuarioClient
    ) {
        this.envioRepository = envioRepository;
        this.pedidoClient = pedidoClient;
        this.usuarioClient = usuarioClient;
    }

    public Envio crearEnvio(CrearEnvioRequest request) {
        log.info("Creando envío para pedido {} y cliente {}", request.getIdPedido(), request.getIdCliente());

        if (envioRepository.findByIdPedido(request.getIdPedido()).isPresent()) {
            log.warn("Intento de crear envío duplicado para pedido {}", request.getIdPedido());
            throw new ReglaNegocioException("Ya existe un envío asociado al pedido " + request.getIdPedido());
        }

        PedidoResponse pedido = pedidoClient.obtenerPedido(request.getIdPedido());
        usuarioClient.validarUsuarioExiste(request.getIdCliente());

        if (pedido.getIdUsuario() == null) {
            log.warn("El pedido {} no tiene usuario asociado", request.getIdPedido());
            throw new ReglaNegocioException("El pedido no tiene usuario asociado");
        }

        if (!pedido.getIdUsuario().equals(request.getIdCliente())) {
            log.warn(
                    "El pedido {} pertenece al usuario {}, pero se intentó crear envío para cliente {}",
                    request.getIdPedido(),
                    pedido.getIdUsuario(),
                    request.getIdCliente()
            );

            throw new ReglaNegocioException(
                    "El pedido " + request.getIdPedido() +
                    " no pertenece al cliente " + request.getIdCliente()
            );
        }

        Envio envio = new Envio();
        envio.setIdPedido(request.getIdPedido());
        envio.setIdCliente(request.getIdCliente());
        envio.setIdTiendaOrigen(request.getIdTiendaOrigen());
        envio.setDireccionDestino(request.getDireccionDestino());
        envio.setTipoEntrega(request.getTipoEntrega());
        envio.setEstadoEnvio(EstadoEnvio.PENDIENTE);
        envio.setFechaEntregaEstimada(request.getFechaEntregaEstimada());
        envio.setTransportista(request.getTransportista());
        envio.setNumeroSeguimiento(request.getNumeroSeguimiento());
        envio.setObservacion(request.getObservacion());

        Envio envioGuardado = envioRepository.save(envio);

        log.info(
                "Envío {} creado correctamente para pedido {}",
                envioGuardado.getIdEnvio(),
                envioGuardado.getIdPedido()
        );

        return envioGuardado;
    }

    public List<Envio> listarEnvios() {
        log.info("Listando todos los envíos");
        return envioRepository.findAll();
    }

    public Envio buscarPorId(Long idEnvio) {
        log.info("Buscando envío con id {}", idEnvio);

        return envioRepository.findById(idEnvio)
                .orElseThrow(() -> {
                    log.warn("No se encontró envío con id {}", idEnvio);
                    return new RecursoNoEncontradoException("No existe un envío con id " + idEnvio);
                });
    }

    public Envio buscarPorPedido(Long idPedido) {
        log.info("Buscando envío asociado al pedido {}", idPedido);

        return envioRepository.findByIdPedido(idPedido)
                .orElseThrow(() -> {
                    log.warn("No se encontró envío para el pedido {}", idPedido);
                    return new RecursoNoEncontradoException("No existe un envío para el pedido " + idPedido);
                });
    }

    public List<Envio> listarPorCliente(Long idCliente) {
        log.info("Listando envíos del cliente {}", idCliente);
        return envioRepository.findByIdCliente(idCliente);
    }

    public List<Envio> listarPorEstado(EstadoEnvio estadoEnvio) {
        log.info("Listando envíos con estado {}", estadoEnvio);
        return envioRepository.findByEstadoEnvio(estadoEnvio);
    }

    public List<Envio> listarPorTiendaOrigen(Long idTiendaOrigen) {
        log.info("Listando envíos de la tienda origen {}", idTiendaOrigen);
        return envioRepository.findByIdTiendaOrigen(idTiendaOrigen);
    }

    public Envio actualizarEstado(Long idEnvio, ActualizarEstadoEnvioRequest request) {
        log.info("Actualizando estado del envío {} a {}", idEnvio, request.getEstadoEnvio());

        Envio envio = buscarPorId(idEnvio);

        if (envio.getEstadoEnvio() == EstadoEnvio.ENTREGADO || envio.getEstadoEnvio() == EstadoEnvio.CANCELADO) {
            log.warn(
                    "No se puede modificar el envío {} porque está en estado {}",
                    idEnvio,
                    envio.getEstadoEnvio()
            );

            throw new ReglaNegocioException(
                    "No se puede modificar un envío en estado " + envio.getEstadoEnvio()
            );
        }

        envio.setEstadoEnvio(request.getEstadoEnvio());
        envio.setObservacion(request.getObservacion());

        if (request.getEstadoEnvio() == EstadoEnvio.ENTREGADO) {
            envio.setFechaEntregaReal(LocalDateTime.now());
            log.info("Se registró fecha de entrega real para el envío {}", idEnvio);
        }

        Envio envioActualizado = envioRepository.save(envio);

        log.info(
                "Estado del envío {} actualizado correctamente a {}",
                idEnvio,
                envioActualizado.getEstadoEnvio()
        );

        return envioActualizado;
    }

    public void eliminarEnvio(Long idEnvio) {
        log.info("Eliminando envío {}", idEnvio);

        Envio envio = buscarPorId(idEnvio);

        if (envio.getEstadoEnvio() == EstadoEnvio.EN_TRANSITO || envio.getEstadoEnvio() == EstadoEnvio.ENTREGADO) {
            log.warn(
                    "No se puede eliminar el envío {} porque está en estado {}",
                    idEnvio,
                    envio.getEstadoEnvio()
            );

            throw new ReglaNegocioException(
                    "No se puede eliminar un envío en estado " + envio.getEstadoEnvio()
            );
        }

        envioRepository.delete(envio);

        log.info("Envío {} eliminado correctamente", idEnvio);
    }
}