package com.example.carrito_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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