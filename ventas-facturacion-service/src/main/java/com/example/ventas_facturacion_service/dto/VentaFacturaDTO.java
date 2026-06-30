package com.example.ventas_facturacion_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "VentaFacturaDTO",
        description = "Datos de una venta y factura generada para Perfulandia."
)
public class VentaFacturaDTO {

    @Schema(
            description = "Identificador único de la venta. Se genera automáticamente al registrar la venta.",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long idVenta;

    @Schema(
            description = "ID del cliente que realiza la compra.",
            example = "1"
    )
    @NotNull(message = "El ID del CLIENTE es OBLIGATORIO")
    private Long idCliente;

    @Schema(
            description = "Fecha y hora en que se registra la venta. Se asigna automáticamente.",
            example = "2026-06-23T12:00:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime fechaVenta;

    @Schema(
            description = "Total de la venta calculado desde el carrito activo del cliente.",
            example = "69970.0",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double totalVenta;

    @Schema(
            description = "Método de pago utilizado por el cliente.",
            example = "TARJETA"
    )
    @NotBlank(message = "El MÉTODO de PAGO es OBLIGATORIO")
    @Size(max = 50, message = "El MÉTODO de PAGO no puede superar los 50 caracteres")
    private String metodoPago;

    @Schema(
            description = "Estado actual de la venta. Si no se informa, se registra como PAGADA.",
            example = "PAGADA"
    )
    @Size(max = 50, message = "El ESTADO de la VENTA no puede superar los 50 caracteres")
    private String estadoVenta;

    @Schema(
            description = "Número de factura generado automáticamente.",
            example = "PF-1-20260623",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String numeroFactura;

    @Schema(
            description = "Correo del cliente donde se enviaría la factura.",
            example = "cliente@correo.cl"
    )
    @NotBlank(message = "El CORREO del CLIENTE es OBLIGATORIO")
    @Email(message = "El CORREO del CLIENTE no tiene un formato válido")
    @Size(max = 120, message = "El CORREO del CLIENTE no puede superar los 120 caracteres")
    private String correoCliente;

    @Schema(
            description = "Observación opcional de la venta.",
            example = "Compra realizada desde carrito web."
    )
    @Size(max = 300, message = "La OBSERVACIÓN no puede superar los 300 caracteres")
    private String observacion;

    @Schema(
            description = "Indica si la venta tiene factura emitida. Si no se informa, queda en true.",
            example = "true"
    )
    private Boolean facturada;
}
