package com.example.tiendas_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(
        name = "ActualizarEstadoTiendaDTO",
        description = "Datos requeridos para activar o desactivar una tienda de Perfulandia."
)
public class ActualizarEstadoTiendaDTO {

    @Schema(
            description = "Nuevo estado de disponibilidad de la tienda.",
            example = "false"
    )
    @NotNull(message = "El ESTADO ACTIVO es OBLIGATORIO")
    private Boolean activa;
}