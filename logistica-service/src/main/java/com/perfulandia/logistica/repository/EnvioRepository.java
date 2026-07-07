package com.perfulandia.logistica.repository;

import com.perfulandia.logistica.model.Envio;
import com.perfulandia.logistica.model.EstadoEnvio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnvioRepository extends JpaRepository<Envio, Long> {

    Optional<Envio> findByIdPedido(Long idPedido);

    List<Envio> findByIdCliente(Long idCliente);

    List<Envio> findByEstadoEnvio(EstadoEnvio estadoEnvio);

    List<Envio> findByIdTiendaOrigen(Long idTiendaOrigen);
}