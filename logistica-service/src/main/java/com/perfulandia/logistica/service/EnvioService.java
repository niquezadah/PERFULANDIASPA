package com.perfulandia.logistica.service;

import com.perfulandia.logistica.dto.ActualizarEstadoEnvioRequest;
import com.perfulandia.logistica.dto.CrearEnvioRequest;
import com.perfulandia.logistica.exception.RecursoNoEncontradoException;
import com.perfulandia.logistica.exception.ReglaNegocioException;
import com.perfulandia.logistica.model.Envio;
import com.perfulandia.logistica.model.EstadoEnvio;
import com.perfulandia.logistica.repository.EnvioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import com.perfulandia.logistica.client.PedidoClient;
import com.perfulandia.logistica.client.UsuarioClient;

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
        if (envioRepository.findByIdPedido(request.getIdPedido()).isPresent()) {
            throw new ReglaNegocioException("Ya existe un envío registrado para el pedido " + request.getIdPedido());
        }

        pedidoClient.validarPedidoExiste(request.getIdPedido());
        usuarioClient.validarUsuarioExiste(request.getIdCliente());

        Envio envio = new Envio();
        envio.setIdPedido(request.getIdPedido());
        envio.setIdCliente(request.getIdCliente());
        envio.setIdTiendaOrigen(request.getIdTiendaOrigen());
        envio.setDireccionDestino(request.getDireccionDestino());
        envio.setTipoEntrega(request.getTipoEntrega());
        envio.setFechaEntregaEstimada(request.getFechaEntregaEstimada());
        envio.setTransportista(request.getTransportista());
        envio.setNumeroSeguimiento(request.getNumeroSeguimiento());
        envio.setObservacion(request.getObservacion());
        envio.setEstadoEnvio(EstadoEnvio.PENDIENTE);

        return envioRepository.save(envio);
    }

    public List<Envio> listarEnvios() {
        return envioRepository.findAll();
    }

    public Envio buscarPorId(Long idEnvio) {
        return envioRepository.findById(idEnvio)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un envío con id " + idEnvio));
    }

    public Envio buscarPorPedido(Long idPedido) {
        return envioRepository.findByIdPedido(idPedido)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un envío para el pedido " + idPedido));
    }

    public List<Envio> listarPorCliente(Long idCliente) {
        return envioRepository.findByIdCliente(idCliente);
    }

    public List<Envio> listarPorEstado(EstadoEnvio estadoEnvio) {
        return envioRepository.findByEstadoEnvio(estadoEnvio);
    }

    public List<Envio> listarPorTiendaOrigen(Long idTiendaOrigen) {
        return envioRepository.findByIdTiendaOrigen(idTiendaOrigen);
    }

    public Envio actualizarEstado(Long idEnvio, ActualizarEstadoEnvioRequest request) {
        Envio envio = buscarPorId(idEnvio);

        if (envio.getEstadoEnvio() == EstadoEnvio.ENTREGADO) {
            throw new ReglaNegocioException("No se puede modificar un envío que ya fue entregado");
        }

        if (envio.getEstadoEnvio() == EstadoEnvio.CANCELADO) {
            throw new ReglaNegocioException("No se puede modificar un envío cancelado");
        }

        envio.setEstadoEnvio(request.getEstadoEnvio());

        if (request.getObservacion() != null && !request.getObservacion().isBlank()) {
            envio.setObservacion(request.getObservacion());
        }

        if (request.getEstadoEnvio() == EstadoEnvio.ENTREGADO) {
            envio.setFechaEntregaReal(LocalDateTime.now());
        }

        return envioRepository.save(envio);
    }

    public void eliminarEnvio(Long idEnvio) {
        Envio envio = buscarPorId(idEnvio);

        if (envio.getEstadoEnvio() == EstadoEnvio.EN_TRANSITO || envio.getEstadoEnvio() == EstadoEnvio.ENTREGADO) {
            throw new ReglaNegocioException("No se puede eliminar un envío en tránsito o entregado");
        }

        envioRepository.delete(envio);
    }
}