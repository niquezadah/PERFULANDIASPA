package com.perfulandia.autenticacion.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.mock;

import com.perfulandia.autenticacion.dto.LoginRequest;
import com.perfulandia.autenticacion.dto.UsuarioAuthResponse;

class UsuarioClientTest {

    private RestTemplate restTemplate;
    private UsuarioClient usuarioClient;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        usuarioClient = new UsuarioClient(restTemplate, "http://localhost:8082");
    }

    @Test
    void validarCredenciales_deberiaRetornarUsuarioCuandoUsuarioServiceRespondeOk() {
        LoginRequest request = new LoginRequest("admin@perfulandia.cl", "admin123");

        UsuarioAuthResponse response = new UsuarioAuthResponse(
                1L,
                "admin@perfulandia.cl",
                "ADMINISTRADOR",
                List.of("GESTIONAR_USUARIOS")
        );

        when(restTemplate.postForEntity(
                "http://localhost:8082/api/auth/validar-credenciales",
                request,
                UsuarioAuthResponse.class
        )).thenReturn(ResponseEntity.ok(response));

        UsuarioAuthResponse resultado = usuarioClient.validarCredenciales(request);

        assertEquals(1L, resultado.getIdUsuario());
        assertEquals("admin@perfulandia.cl", resultado.getCorreo());
        assertEquals("ADMINISTRADOR", resultado.getRol());
        assertEquals(List.of("GESTIONAR_USUARIOS"), resultado.getPermisos());

        verify(restTemplate).postForEntity(
                "http://localhost:8082/api/auth/validar-credenciales",
                request,
                UsuarioAuthResponse.class
        );
    }

    @Test
    void validarCredenciales_deberiaLanzarErrorCuandoBodyEsNull() {
        LoginRequest request = new LoginRequest("admin@perfulandia.cl", "admin123");

        when(restTemplate.postForEntity(
                "http://localhost:8082/api/auth/validar-credenciales",
                request,
                UsuarioAuthResponse.class
        )).thenReturn(ResponseEntity.ok(null));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                usuarioClient.validarCredenciales(request)
        );

        assertEquals("Credenciales inválidas", ex.getMessage());
    }

    @Test
    void validarCredenciales_deberiaLanzarErrorCuandoUsuarioServiceRespondeUnauthorized() {
        LoginRequest request = new LoginRequest("admin@perfulandia.cl", "mala");

        when(restTemplate.postForEntity(
                "http://localhost:8082/api/auth/validar-credenciales",
                request,
                UsuarioAuthResponse.class
        )).thenThrow(HttpClientErrorException.Unauthorized.class);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                usuarioClient.validarCredenciales(request)
        );

        assertEquals("Credenciales inválidas", ex.getMessage());
    }

    @Test
    void validarCredenciales_deberiaLanzarErrorCuandoNoPuedeConectar() {
        LoginRequest request = new LoginRequest("admin@perfulandia.cl", "admin123");

        when(restTemplate.postForEntity(
                "http://localhost:8082/api/auth/validar-credenciales",
                request,
                UsuarioAuthResponse.class
        )).thenThrow(new ResourceAccessException("sin conexion"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                usuarioClient.validarCredenciales(request)
        );

        assertEquals("No se pudo conectar con usuario_service", ex.getMessage());
    }

    @Test
    void validarCredenciales_deberiaLanzarErrorCuandoOcurreOtroErrorRest() {
        LoginRequest request = new LoginRequest("admin@perfulandia.cl", "admin123");

        when(restTemplate.postForEntity(
                "http://localhost:8082/api/auth/validar-credenciales",
                request,
                UsuarioAuthResponse.class
        )).thenThrow(new RestClientException("error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                usuarioClient.validarCredenciales(request)
        );

        assertEquals("Error al consultar usuario_service", ex.getMessage());
    }
}