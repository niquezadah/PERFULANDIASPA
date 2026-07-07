package com.perfulandia.logistica.dto;

import com.perfulandia.logistica.model.TipoEntrega;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CrearEnvioRequest {

    @NotNull(message = "El id del pedido es obligatorio")
    private Long idPedido;

    @NotNull(message = "El id del cliente es obligatorio")
    private Long idCliente;

    @NotNull(message = "El id de la tienda origen es obligatorio")
    private Long idTiendaOrigen;

    @NotBlank(message = "La dirección de destino es obligatoria")
    private String direccionDestino;

    @NotNull(message = "El tipo de entrega es obligatorio")
    private TipoEntrega tipoEntrega;

    private LocalDateTime fechaEntregaEstimada;

    private String transportista;

    private String numeroSeguimiento;

    private String observacion;
}