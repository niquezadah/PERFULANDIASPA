package com.example.ventas_facturacion_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVenta;

    private Long idCliente;
    private LocalDateTime fechaVenta;
    private Double totalVenta;
    private String metodoPago;
    private String estadoVenta;
    private String numeroFactura;
    private String correoCliente;
    private String observacion;
    private Boolean facturada;
}
