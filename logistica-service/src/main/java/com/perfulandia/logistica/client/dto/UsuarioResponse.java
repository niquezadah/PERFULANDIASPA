package com.perfulandia.logistica.client.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsuarioResponse {

    @JsonAlias({"idUsuario", "id_usuario", "id"})
    private Long idUsuario;

    @JsonAlias({"nombre", "nombreUsuario"})
    private String nombre;

    @JsonAlias({"correo", "email"})
    private String correo;
}