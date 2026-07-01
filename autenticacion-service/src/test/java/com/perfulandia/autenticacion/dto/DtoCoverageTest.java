package com.perfulandia.autenticacion.dto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

class DtoCoverageTest {

    @Test
    void loginRequest_deberiaCubrirConstructoresGettersYSetters() {
        LoginRequest request = new LoginRequest();

        request.setCorreo("admin@perfulandia.cl");
        request.setPassword("admin123");

        assertEquals("admin@perfulandia.cl", request.getCorreo());
        assertEquals("admin123", request.getPassword());

        LoginRequest requestConConstructor = new LoginRequest(
                "cliente@perfulandia.cl",
                "cliente123"
        );

        assertEquals("cliente@perfulandia.cl", requestConConstructor.getCorreo());
        assertEquals("cliente123", requestConConstructor.getPassword());
    }

    @Test
    void loginResponse_deberiaCubrirConstructoresGettersYSetters() {
        LoginResponse response = new LoginResponse();

        response.setToken("token");
        response.setTipo("Bearer");
        response.setIdUsuario(1L);
        response.setCorreo("admin@perfulandia.cl");
        response.setRol("ADMINISTRADOR");
        response.setPermisos(List.of("GESTIONAR_USUARIOS"));

        assertEquals("token", response.getToken());
        assertEquals("Bearer", response.getTipo());
        assertEquals(1L, response.getIdUsuario());
        assertEquals("admin@perfulandia.cl", response.getCorreo());
        assertEquals("ADMINISTRADOR", response.getRol());
        assertEquals(List.of("GESTIONAR_USUARIOS"), response.getPermisos());

        LoginResponse responseConConstructor = new LoginResponse(
                "token2",
                "Bearer",
                2L,
                "cliente@perfulandia.cl",
                "CLIENTE",
                List.of("VER_PRODUCTOS")
        );

        assertEquals("token2", responseConConstructor.getToken());
        assertEquals("Bearer", responseConConstructor.getTipo());
        assertEquals(2L, responseConConstructor.getIdUsuario());
        assertEquals("cliente@perfulandia.cl", responseConConstructor.getCorreo());
        assertEquals("CLIENTE", responseConConstructor.getRol());
        assertEquals(List.of("VER_PRODUCTOS"), responseConConstructor.getPermisos());
    }

    @Test
    void validarTokenRequest_deberiaCubrirConstructoresGettersYSetters() {
        ValidarTokenRequest request = new ValidarTokenRequest();

        request.setToken("token");

        assertEquals("token", request.getToken());

        ValidarTokenRequest requestConConstructor = new ValidarTokenRequest("token2");

        assertEquals("token2", requestConConstructor.getToken());
    }

    @Test
    void validarTokenResponse_deberiaCubrirConstructoresGettersYSetters() {
        ValidarTokenResponse response = new ValidarTokenResponse();

        response.setValido(true);
        response.setMensaje("Token válido");
        response.setIdUsuario(1L);
        response.setCorreo("admin@perfulandia.cl");
        response.setRol("ADMINISTRADOR");
        response.setPermisos(List.of("GESTIONAR_USUARIOS"));

        assertEquals(true, response.isValido());
        assertEquals("Token válido", response.getMensaje());
        assertEquals(1L, response.getIdUsuario());
        assertEquals("admin@perfulandia.cl", response.getCorreo());
        assertEquals("ADMINISTRADOR", response.getRol());
        assertEquals(List.of("GESTIONAR_USUARIOS"), response.getPermisos());

        ValidarTokenResponse responseConConstructor = new ValidarTokenResponse(
                true,
                "Token válido",
                2L,
                "cliente@perfulandia.cl",
                "CLIENTE",
                List.of("VER_PRODUCTOS")
        );

        assertEquals(true, responseConConstructor.isValido());
        assertEquals("Token válido", responseConConstructor.getMensaje());
        assertEquals(2L, responseConConstructor.getIdUsuario());
        assertEquals("cliente@perfulandia.cl", responseConConstructor.getCorreo());
        assertEquals("CLIENTE", responseConConstructor.getRol());
        assertEquals(List.of("VER_PRODUCTOS"), responseConConstructor.getPermisos());

        ValidarTokenResponse invalido = ValidarTokenResponse.invalido("Token inválido");

        assertFalse(invalido.isValido());
        assertEquals("Token inválido", invalido.getMensaje());
    }

    @Test
    void usuarioAuthResponse_deberiaCubrirConstructoresGettersYSetters() {
        UsuarioAuthResponse response = new UsuarioAuthResponse();

        response.setIdUsuario(1L);
        response.setCorreo("admin@perfulandia.cl");
        response.setRol("ADMINISTRADOR");
        response.setPermisos(List.of("GESTIONAR_USUARIOS"));

        assertEquals(1L, response.getIdUsuario());
        assertEquals("admin@perfulandia.cl", response.getCorreo());
        assertEquals("ADMINISTRADOR", response.getRol());
        assertEquals(List.of("GESTIONAR_USUARIOS"), response.getPermisos());

        UsuarioAuthResponse responseConConstructor = new UsuarioAuthResponse(
                2L,
                "cliente@perfulandia.cl",
                "CLIENTE",
                List.of("VER_PRODUCTOS")
        );

        assertEquals(2L, responseConConstructor.getIdUsuario());
        assertEquals("cliente@perfulandia.cl", responseConConstructor.getCorreo());
        assertEquals("CLIENTE", responseConConstructor.getRol());
        assertEquals(List.of("VER_PRODUCTOS"), responseConConstructor.getPermisos());
    }
}