package com.perfulandia.pedido.repository;

import com.perfulandia.pedido.model.EstadoPedido;
import com.perfulandia.pedido.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByIdUsuario(Long idUsuario);

    List<Pedido> findByIdTienda(Long idTienda);

    List<Pedido> findByEstado(EstadoPedido estado);
}