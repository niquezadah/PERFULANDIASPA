package com.perfulandia.autenticacion.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.perfulandia.autenticacion.client.UsuarioClient;

class AuthServiceBranchTest {

    private AuthService authService;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "secret",
                "perfulandia-clave-secreta-para-firmar-tokens-jwt-2026"
        );

        ReflectionTestUtils.setField(jwtService, "expirationMinutes", 60L);

        authService = new AuthService(jwtService, mock(UsuarioClient.class));
    }

    @Test
    void tienePermiso_deberiaRetornarFalseCuandoTokenValidoNoTienePermisos() {
        String token = jwtService.generarToken(
                1L,
                "admin@perfulandia.cl",
                "ADMINISTRADOR",
                null
        );

        boolean permitido = authService.tienePermiso(token, "GESTIONAR_USUARIOS");

        assertFalse(permitido);
    }

    @Test
    void tienePermiso_deberiaRetornarTrueConPermisoRealDesdeUsuarioService() {
        String token = jwtService.generarToken(
                1L,
                "admin@perfulandia.cl",
                "ADMINISTRADOR",
                List.of("GESTIONAR_USUARIOS")
        );

        boolean permitido = authService.tienePermiso(token, "GESTIONAR_USUARIOS");

        assertTrue(permitido);
    }
}