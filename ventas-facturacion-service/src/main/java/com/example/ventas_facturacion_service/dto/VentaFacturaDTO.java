package com.example.ventas_facturacion_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaFacturaDTO {

    @Schema(
            description = "ID de la venta. Se genera automáticamente.",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long idVenta;

    @Schema(
            description = "ID del cliente asociado a la venta. Debe existir y estar activo en usuario-service.",
            example = "1"
    )
    @NotNull(message = "El ID del CLIENTE es OBLIGATORIO")
    private Long idCliente;

    @Schema(
            description = "Fecha y hora de la venta. Se genera automáticamente.",
            example = "2026-06-23T12:00:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime fechaVenta;

    @Schema(
            description = "Total de la venta. Se obtiene desde el total del carrito en carrito-service.",
            example = "69970.0",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double totalVenta;

    @Schema(
            description = "Método de pago utilizado por el cliente.",
            example = "TARJETA"
    )
    @NotBlank(message = "El MÉTODO DE PAGO es OBLIGATORIO")
    private String metodoPago;

    @Schema(
            description = "Estado de la venta. Si no se envía, el sistema asigna PAGADA por defecto.",
            example = "PAGADA"
    )
    private String estadoVenta;

    @Schema(
            description = "Número de factura generado automáticamente por el sistema.",
            example = "PF-1-20260623",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String numeroFactura;

    @Schema(
            description = "Correo del cliente asociado a la venta.",
            example = "nicolas.quezada@perfulandia.cl"
    )
    @NotBlank(message = "El CORREO del CLIENTE es OBLIGATORIO")
    @Email(message = "El CORREO del CLIENTE debe tener un formato válido")
    private String correoCliente;

    @Schema(
            description = "Observación opcional de la venta.",
            example = "Compra realizada desde carrito web."
    )
    private String observacion;

    @Schema(
            description = "Indica si la venta fue facturada. Si no se envía, el sistema asigna true por defecto.",
            example = "true"
    )
    private Boolean facturada;
}