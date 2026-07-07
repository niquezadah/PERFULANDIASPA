package com.perfulandia.logistica.dto;

import com.perfulandia.logistica.model.EstadoEnvio;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActualizarEstadoEnvioRequest {

    @NotNull(message = "El estado del envío es obligatorio")
    private EstadoEnvio estadoEnvio;

    private String observacion;
}