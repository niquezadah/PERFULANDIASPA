package com.perfulandia.autenticacion.controller;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.perfulandia.autenticacion.dto.LoginRequest;
import com.perfulandia.autenticacion.dto.LoginResponse;
import com.perfulandia.autenticacion.dto.ValidarTokenRequest;
import com.perfulandia.autenticacion.dto.ValidarTokenResponse;
import com.perfulandia.autenticacion.service.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_deberiaRetornarOk() {
        LoginRequest request = new LoginRequest("admin@perfulandia.cl", "admin123");

        LoginResponse response = new LoginResponse(
                "token",
                "Bearer",
                1L,
                "admin@perfulandia.cl",
                "ADMINISTRADOR",
                List.of("CREAR_USUARIO")
        );

        when(authService.login(request)).thenReturn(response);

        ResponseEntity<LoginResponse> result = authController.login(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
        verify(authService).login(request);
    }

    @Test
    void validarToken_deberiaRetornarOk() {
        ValidarTokenRequest request = new ValidarTokenRequest("token");

        ValidarTokenResponse response = new ValidarTokenResponse(
                true,
                "Token válido",
                1L,
                "admin@perfulandia.cl",
                "ADMINISTRADOR",
                List.of("CREAR_USUARIO")
        );

        when(authService.validarToken("token")).thenReturn(response);

        ResponseEntity<ValidarTokenResponse> result = authController.validarToken(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
        verify(authService).validarToken("token");
    }

    @Test
    void tienePermiso_deberiaRetornarMapaConPermitidoTrue() {
        when(authService.tienePermiso("Bearer token", "CREAR_USUARIO")).thenReturn(true);

        ResponseEntity<Map<String, Object>> result =
                authController.tienePermiso("Bearer token", "CREAR_USUARIO");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(true, result.getBody().get("permitido"));
        assertEquals("CREAR_USUARIO", result.getBody().get("permiso"));
        verify(authService).tienePermiso("Bearer token", "CREAR_USUARIO");
    }

    @Test
    void tienePermiso_deberiaRetornarMapaConPermitidoFalse() {
        when(authService.tienePermiso(null, "ELIMINAR_PRODUCTO")).thenReturn(false);

        ResponseEntity<Map<String, Object>> result =
                authController.tienePermiso(null, "ELIMINAR_PRODUCTO");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(false, result.getBody().get("permitido"));
        assertEquals("ELIMINAR_PRODUCTO", result.getBody().get("permiso"));
        verify(authService).tienePermiso(null, "ELIMINAR_PRODUCTO");
    }
}