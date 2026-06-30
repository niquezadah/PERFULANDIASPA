package com.example.soporte_resena_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "ResenaDTO",
        description = "Datos de una reseña realizada por un cliente sobre un producto de Perfulandia."
)
public class ResenaDTO {

    @Schema(
            description = "Identificador único de la reseña. Se genera automáticamente al registrar una reseña.",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long idResena;

    @Schema(
            description = "ID del producto asociado a la reseña. Debe corresponder a un producto existente.",
            example = "1"
    )
    @NotNull(message = "El ID del PRODUCTO es OBLIGATORIO")
    private Long idProducto;

    @Schema(
            description = "Nombre del cliente que realiza la reseña.",
            example = "Valentina Rojas"
    )
    @NotBlank(message = "El NOMBRE del CLIENTE es OBLIGATORIO")
    @Size(max = 100, message = "El NOMBRE del CLIENTE no puede superar los 100 caracteres")
    private String nombreCliente;

    @Schema(
            description = "Calificación entregada al producto. Debe estar entre 1 y 5.",
            example = "5"
    )
    @NotNull(message = "La CALIFICACIÓN es OBLIGATORIA")
    @Min(value = 1, message = "La CALIFICACIÓN mínima es 1")
    @Max(value = 5, message = "La CALIFICACIÓN máxima es 5")
    private Integer calificacion;

    @Schema(
            description = "Comentario escrito por el cliente sobre el producto.",
            example = "Aroma elegante, buena fijación y presentación muy cuidada."
    )
    @NotBlank(message = "El COMENTARIO es OBLIGATORIO")
    @Size(max = 500, message = "El COMENTARIO no puede superar los 500 caracteres")
    private String comentario;

    @Schema(
            description = "Indica si la reseña está activa y visible.",
            example = "true"
    )
    @NotNull(message = "El estado ACTIVA es OBLIGATORIO")
    private Boolean activa;
}