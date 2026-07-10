package com.example.soporte_resena_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsuarioDTO {

    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String correo;
    private String direccionEnvio;
    private Boolean estado;
    private Long idRol;
    private String nombreRol;
}