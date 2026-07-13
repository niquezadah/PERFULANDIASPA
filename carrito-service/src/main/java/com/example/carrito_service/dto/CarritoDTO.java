package com.example.carrito_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoDTO {

    @Schema(
            description = "ID del registro del carrito. Se genera automáticamente al agregar un producto.",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long idCarrito;

    @Schema(
            description = "ID del cliente dueño del carrito. Debe existir y estar activo en usuario-service. Corresponde al idUsuario del cliente.",
            example = "1"
    )
    @NotNull(message = "El ID del CLIENTE es OBLIGATORIO")
    private Long idCliente;

    @Schema(
            description = "ID del producto que se desea agregar al carrito. Debe existir en inventario-catalogo-service, estar disponible y tener stock suficiente.",
            example = "1"
    )
    @NotNull(message = "El ID del PRODUCTO es OBLIGATORIO")
    private Long idProducto;

    @Schema(
            description = "Nombre del producto agregado al carrito. Se obtiene desde inventario-catalogo-service.",
            example = "Eau de Parfum Rosas del Sur",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String nombreProducto;

    @Schema(
            description = "Cantidad del producto que el cliente desea comprar. Debe ser mayor o igual a 1 y no superar el stock disponible.",
            example = "2"
    )
    @NotNull(message = "La CANTIDAD del PRODUCTO es OBLIGATORIA")
    @Min(value = 1, message = "La CANTIDAD mínima es 1")
    private Integer cantidad;

    @Schema(
            description = "Precio unitario del producto. Se obtiene desde inventario-catalogo-service.",
            example = "24990.0",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double precioUnitario;

    @Schema(
            description = "Subtotal calculado según precio unitario y cantidad.",
            example = "49980.0",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double subtotal;

    @Schema(
            description = "Indica si el registro del carrito se encuentra activo.",
            example = "true"
    )
    @NotNull(message = "El ESTADO ACTIVO del CARRITO es OBLIGATORIO")
    private Boolean activo;
}