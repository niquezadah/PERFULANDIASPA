package com.example.inventario_catalogo_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "ProductoDTO",
        description = "Datos de un producto registrado en el catálogo de Perfulandia."
)
public class ProductoDTO {

    @Schema(
            description = "Identificador único del producto. Se genera automáticamente al registrar un producto.",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long idProducto;

    @Schema(
            description = "Nombre del producto. Campo obligatorio y con máximo de 100 caracteres.",
            example = "Eau de Parfum Rosas del Sur"
    )
    @NotBlank(message = "El NOMBRE del PRODUCTO es OBLIGATORIO")
    @Size(max = 100, message = "El NOMBRE no puede superar los 100 caracteres")
    private String nombre;

    @Schema(
            description = "Descripción general del producto.",
            example = "Perfume floral de larga duración para uso diario."
    )
    @Size(max = 300, message = "La DESCRIPCIÓN no puede superar los 300 caracteres")
    private String descripcion;

    @Schema(
            description = "Categoría a la que pertenece el producto.",
            example = "PERFUMERIA"
    )
    @NotBlank(message = "La CATEGORÍA del PRODUCTO es OBLIGATORIA")
    @Size(max = 100, message = "La CATEGORÍA no puede superar los 100 caracteres")
    private String categoria;

    @Schema(
            description = "Cantidad disponible del producto en inventario.",
            example = "35"
    )
    @NotNull(message = "El STOCK del PRODUCTO es OBLIGATORIO")
    @Min(value = 0, message = "El STOCK no puede ser negativo")
    private Integer stock;

    @Schema(
            description = "Precio unitario del producto.",
            example = "24990.0"
    )
    @NotNull(message = "El PRECIO del PRODUCTO es OBLIGATORIO")
    @PositiveOrZero(message = "El PRECIO no puede ser negativo")
    private Double precio;

    @Schema(
            description = "Indica si el producto está disponible para venta.",
            example = "true"
    )
    @NotNull(message = "La DISPONIBILIDAD del PRODUCTO es OBLIGATORIA")
    private Boolean disponible;

    @Schema(
            description = "ID de la tienda a la que pertenece el producto. Debe corresponder a una tienda existente.",
            example = "1"
    )
    @NotNull(message = "El ID de la TIENDA es OBLIGATORIO")
    private Long idTienda;
}