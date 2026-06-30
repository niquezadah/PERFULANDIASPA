package com.example.ventas_facturacion_service.repository;

import com.example.ventas_facturacion_service.model.VentaFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentaFacturaRepository extends JpaRepository<VentaFactura, Long> {

    List<VentaFactura> findByIdCliente(Long idCliente);

    List<VentaFactura> findByEstadoVenta(String estadoVenta);

    List<VentaFactura> findByFacturada(Boolean facturada);
}
