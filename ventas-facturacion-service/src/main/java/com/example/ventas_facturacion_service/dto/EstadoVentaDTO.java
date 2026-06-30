package com.example.ventas_facturacion_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "EstadoVentaDTO",
        description = "Datos para actualizar el estado de una venta."
)
public class EstadoVentaDTO {

    @Schema(
            description = "Nuevo estado de la venta.",
            example = "ANULADA"
    )
    @NotBlank(message = "El ESTADO de la VENTA es OBLIGATORIO")
    @Size(max = 50, message = "El ESTADO de la VENTA no puede superar los 50 caracteres")
    private String estadoVenta;
}
