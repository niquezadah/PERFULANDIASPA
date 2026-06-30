package com.example.carrito_service.repository;

import com.example.carrito_service.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    List<Carrito> findByIdCliente(Long idCliente);

    List<Carrito> findByIdProducto(Long idProducto);

    List<Carrito> findByIdClienteAndActivo(Long idCliente, Boolean activo);

    void deleteByIdCliente(Long idCliente);
}
