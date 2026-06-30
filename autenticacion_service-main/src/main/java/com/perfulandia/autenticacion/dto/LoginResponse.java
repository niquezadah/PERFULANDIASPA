package com.perfulandia.autenticacion.dto;

import java.util.List;

public class LoginResponse {

    private String token;
    private String tipo;
    private Long idUsuario;
    private String correo;
    private String rol;
    private List<String> permisos;

    public LoginResponse() {
    }

    public LoginResponse(String token, String tipo, Long idUsuario, String correo, String rol, List<String> permisos) {
        this.token = token;
        this.tipo = tipo;
        this.idUsuario = idUsuario;
        this.correo = correo;
        this.rol = rol;
        this.permisos = permisos;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public List<String> getPermisos() {
        return permisos;
    }

    public void setPermisos(List<String> permisos) {
        this.permisos = permisos;
    }
}