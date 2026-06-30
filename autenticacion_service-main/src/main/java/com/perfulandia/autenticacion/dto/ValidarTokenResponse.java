package com.perfulandia.autenticacion.dto;

import java.util.List;

public class ValidarTokenResponse {

    private boolean valido;
    private String mensaje;
    private Long idUsuario;
    private String correo;
    private String rol;
    private List<String> permisos;

    public ValidarTokenResponse() {
    }

    public ValidarTokenResponse(boolean valido, String mensaje, Long idUsuario, String correo, String rol, List<String> permisos) {
        this.valido = valido;
        this.mensaje = mensaje;
        this.idUsuario = idUsuario;
        this.correo = correo;
        this.rol = rol;
        this.permisos = permisos;
    }

    public static ValidarTokenResponse invalido(String mensaje) {
        return new ValidarTokenResponse(false, mensaje, null, null, null, List.of());
    }

    public boolean isValido() {
        return valido;
    }

    public void setValido(boolean valido) {
        this.valido = valido;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
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