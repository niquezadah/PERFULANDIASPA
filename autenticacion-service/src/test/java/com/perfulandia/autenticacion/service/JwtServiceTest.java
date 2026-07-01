package com.perfulandia.autenticacion.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Claims;

class JwtServiceTest {

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
    }

    @Test
    void generarToken_deberiaCrearTokenValido() {
        String token = jwtService.generarToken(
                1L,
                "admin@perfulandia.cl",
                "ADMINISTRADOR",
                List.of("CREAR_USUARIO", "EDITAR_USUARIO")
        );

        assertNotNull(token);
        assertTrue(jwtService.tokenEsValido(token));

        Claims claims = jwtService.obtenerClaims(token);

        assertEquals("admin@perfulandia.cl", claims.getSubject());
        assertEquals(1L, claims.get("idUsuario", Long.class));
        assertEquals("ADMINISTRADOR", claims.get("rol", String.class));
    }

    @Test
    void obtenerClaims_deberiaAceptarTokenConBearer() {
        String token = jwtService.generarToken(
                1L,
                "admin@perfulandia.cl",
                "ADMINISTRADOR",
                List.of("CREAR_USUARIO")
        );

        Claims claims = jwtService.obtenerClaims("Bearer " + token);

        assertEquals("admin@perfulandia.cl", claims.getSubject());
    }

    @Test
    void tokenEsValido_deberiaRetornarFalseCuandoTokenEsInvalido() {
        assertFalse(jwtService.tokenEsValido("token-invalido"));
    }

    @Test
    void tokenEsValido_deberiaRetornarFalseCuandoTokenEsNulo() {
        assertFalse(jwtService.tokenEsValido(null));
    }

    @Test
    void limpiarBearer_deberiaLimpiarTokenCorrectamente() {
        assertEquals("abc123", jwtService.limpiarBearer("Bearer abc123"));
        assertEquals("abc123", jwtService.limpiarBearer("bearer abc123"));
        assertEquals("abc123", jwtService.limpiarBearer("  Bearer abc123  "));
        assertEquals("abc123", jwtService.limpiarBearer("abc123"));
        assertEquals("", jwtService.limpiarBearer("   "));
        assertEquals(null, jwtService.limpiarBearer(null));
    }
}