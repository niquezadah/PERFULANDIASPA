package com.perfulandia.autenticacion.dto;

import java.util.List;

public class UsuarioAuthResponse {

    private Long idUsuario;
    private String correo;
    private String rol;
    private List<String> permisos;

    public UsuarioAuthResponse() {
    }

    public UsuarioAuthResponse(Long idUsuario, String correo, String rol, List<String> permisos) {
        this.idUsuario = idUsuario;
        this.correo = correo;
        this.rol = rol;
        this.permisos = permisos;
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