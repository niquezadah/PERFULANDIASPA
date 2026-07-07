package com.perfulandia.logistica.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "envios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEnvio;

    @Column(nullable = false)
    private Long idPedido;

    @Column(nullable = false)
    private Long idCliente;

    @Column(nullable = false)
    private Long idTiendaOrigen;

    @Column(nullable = false, length = 200)
    private String direccionDestino;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEnvio estadoEnvio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEntrega tipoEntrega;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaEntregaEstimada;

    private LocalDateTime fechaEntregaReal;

    @Column(length = 100)
    private String transportista;

    @Column(length = 100)
    private String numeroSeguimiento;

    @Column(length = 300)
    private String observacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();

        if (this.estadoEnvio == null) {
            this.estadoEnvio = EstadoEnvio.PENDIENTE;
        }
    }
}