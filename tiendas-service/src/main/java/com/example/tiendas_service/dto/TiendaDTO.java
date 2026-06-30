package com.example.tiendas_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
        name = "TiendaDTO",
        description = "Datos de una tienda física registrada en Perfulandia."
)
public class TiendaDTO {

    @Schema(
            description = "Identificador único de la tienda. Se genera automáticamente al registrar una tienda.",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long idTienda;

    @Schema(
            description = "Nombre comercial de la tienda. Campo obligatorio y con máximo de 100 caracteres.",
            example = "Perfulandia Concepción"
    )
    @NotBlank(message = "El NOMBRE de la TIENDA es OBLIGATORIO")
    @Size(max = 100, message = "El NOMBRE no puede superar los 100 caracteres")
    private String nombre;

    @Schema(
            description = "Dirección física de la tienda. Campo obligatorio y con máximo de 150 caracteres.",
            example = "Avenida O'Higgins 1250"
    )
    @NotBlank(message = "La DIRECCIÓN es OBLIGATORIA")
    @Size(max = 150, message = "La DIRECCIÓN no puede superar los 150 caracteres")
    private String direccion;

    @Schema(
            description = "Comuna donde está ubicada la tienda. Campo obligatorio.",
            example = "Concepción"
    )
    @NotBlank(message = "La COMUNA es OBLIGATORIA")
    @Size(max = 50, message = "La COMUNA no puede superar los 50 caracteres")
    private String comuna;

    @Schema(
            description = "Ciudad donde está ubicada la tienda. Campo obligatorio.",
            example = "Concepción"
    )
    @NotBlank(message = "La CIUDAD es OBLIGATORIA")
    @Size(max = 50, message = "La CIUDAD no puede superar los 50 caracteres")
    private String ciudad;

    @Schema(
            description = "Región donde está ubicada la tienda. Campo obligatorio.",
            example = "Biobío"
    )
    @NotBlank(message = "La REGIÓN es OBLIGATORIA")
    @Size(max = 50, message = "La REGIÓN no puede superar los 50 caracteres")
    private String region;

    @Schema(
            description = "Teléfono de contacto de la tienda. Máximo 12 caracteres.",
            example = "+56912345678"
    )
    @Size(max = 12, message = "El TELÉFONO no puede superar los 12 caracteres")
    private String telefono;

    @Schema(
            description = "Nombre del personal responsable o asignado a la tienda.",
            example = "Camila Torres"
    )
    @Size(max = 300, message = "El PERSONAL ASIGNADO no puede superar los 300 caracteres")
    private String personalAsignado;

    @Schema(
            description = "Hora de apertura de la tienda. Campo obligatorio.",
            example = "09:00"
    )
    @NotBlank(message = "El HORARIO DE APERTURA es OBLIGATORIO")
    @Size(max = 10, message = "El HORARIO DE APERTURA no puede superar los 10 caracteres")
    private String horarioApertura;

    @Schema(
            description = "Hora de cierre de la tienda. Campo obligatorio.",
            example = "19:00"
    )
    @NotBlank(message = "El HORARIO DE CIERRE es OBLIGATORIO")
    @Size(max = 10, message = "El HORARIO DE CIERRE no puede superar los 10 caracteres")
    private String horarioCierre;

    @Schema(
            description = "Indica si la tienda está disponible para operar.",
            example = "true"
    )
    @NotNull(message = "El ESTADO ACTIVO es OBLIGATORIO")
    private Boolean activa;

    @Schema(
            description = "Políticas o condiciones locales aplicables a la tienda.",
            example = "Atención presencial, retiro en tienda y despacho según disponibilidad local."
    )
    @Size(max = 500, message = "Las POLITICAS LOCALES no pueden superar los 500 caracteres")
    private String politicasLocales;
}