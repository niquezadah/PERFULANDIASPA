package com.perfulandia.autenticacion.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.perfulandia.autenticacion.client.UsuarioClient;
import com.perfulandia.autenticacion.dto.LoginRequest;
import com.perfulandia.autenticacion.dto.LoginResponse;
import com.perfulandia.autenticacion.dto.UsuarioAuthResponse;
import com.perfulandia.autenticacion.dto.ValidarTokenResponse;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioClient usuarioClient;

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

        authService = new AuthService(jwtService, usuarioClient);
    }

    @Test
    void login_deberiaRetornarTokenCuandoCredencialesSonCorrectas() {
        LoginRequest request = new LoginRequest("admin@perfulandia.cl", "admin123");

        UsuarioAuthResponse usuario = new UsuarioAuthResponse(
                1L,
                "admin@perfulandia.cl",
                "ADMINISTRADOR",
                List.of("GESTIONAR_USUARIOS")
        );

        when(usuarioClient.validarCredenciales(request)).thenReturn(usuario);

        LoginResponse response = authService.login(request);

        assertNotNull(response.getToken());
        assertEquals("Bearer", response.getTipo());
        assertEquals(1L, response.getIdUsuario());
        assertEquals("admin@perfulandia.cl", response.getCorreo());
        assertEquals("ADMINISTRADOR", response.getRol());
        assertEquals(List.of("GESTIONAR_USUARIOS"), response.getPermisos());

        verify(usuarioClient).validarCredenciales(request);
    }

    @Test
    void login_deberiaUsarDatosDevueltosPorUsuarioService() {
        LoginRequest request = new LoginRequest("cliente@perfulandia.cl", "cliente123");

        UsuarioAuthResponse usuario = new UsuarioAuthResponse(
                2L,
                "cliente@perfulandia.cl",
                "CLIENTE",
                List.of("VER_PRODUCTOS")
        );

        when(usuarioClient.validarCredenciales(request)).thenReturn(usuario);

        LoginResponse response = authService.login(request);

        assertNotNull(response.getToken());
        assertEquals(2L, response.getIdUsuario());
        assertEquals("cliente@perfulandia.cl", response.getCorreo());
        assertEquals("CLIENTE", response.getRol());
        assertEquals(List.of("VER_PRODUCTOS"), response.getPermisos());

        verify(usuarioClient).validarCredenciales(request);
    }

    @Test
    void login_deberiaUsarListaVaciaCuandoUsuarioServiceNoEnviaPermisos() {
        LoginRequest request = new LoginRequest("admin@perfulandia.cl", "admin123");

        UsuarioAuthResponse usuario = new UsuarioAuthResponse(
                1L,
                "admin@perfulandia.cl",
                "ADMINISTRADOR",
                null
        );

        when(usuarioClient.validarCredenciales(request)).thenReturn(usuario);

        LoginResponse response = authService.login(request);

        assertNotNull(response.getToken());
        assertEquals(List.of(), response.getPermisos());

        verify(usuarioClient).validarCredenciales(request);
    }

    @Test
    void login_deberiaLanzarErrorCuandoCredencialesSonInvalidas() {
        LoginRequest request = new LoginRequest("admin@perfulandia.cl", "mala123");

        when(usuarioClient.validarCredenciales(request))
                .thenThrow(new RuntimeException("Credenciales inválidas"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(request));

        assertEquals("Credenciales inválidas", ex.getMessage());
        verify(usuarioClient).validarCredenciales(request);
    }

    @Test
    void validarToken_deberiaRetornarInvalidoCuandoTokenEsNulo() {
        ValidarTokenResponse response = authService.validarToken(null);

        assertFalse(response.isValido());
        assertEquals("Token no informado", response.getMensaje());
    }

    @Test
    void validarToken_deberiaRetornarInvalidoCuandoTokenEstaVacio() {
        ValidarTokenResponse response = authService.validarToken("   ");

        assertFalse(response.isValido());
        assertEquals("Token no informado", response.getMensaje());
    }

    @Test
    void validarToken_deberiaRetornarInvalidoCuandoTokenNoSirve() {
        ValidarTokenResponse response = authService.validarToken("token-invalido");

        assertFalse(response.isValido());
        assertEquals("Token inválido o expirado", response.getMensaje());
    }

    @Test
    void validarToken_deberiaRetornarDatosCuandoTokenEsValido() {
        String token = jwtService.generarToken(
                1L,
                "admin@perfulandia.cl",
                "ADMINISTRADOR",
                List.of("GESTIONAR_USUARIOS")
        );

        ValidarTokenResponse response = authService.validarToken(token);

        assertTrue(response.isValido());
        assertEquals("Token válido", response.getMensaje());
        assertEquals(1L, response.getIdUsuario());
        assertEquals("admin@perfulandia.cl", response.getCorreo());
        assertEquals("ADMINISTRADOR", response.getRol());
        assertTrue(response.getPermisos().contains("GESTIONAR_USUARIOS"));
    }

    @Test
    void tienePermiso_deberiaRetornarTrueCuandoPermisoExiste() {
        String token = jwtService.generarToken(
                1L,
                "admin@perfulandia.cl",
                "ADMINISTRADOR",
                List.of("GESTIONAR_USUARIOS")
        );

        boolean permitido = authService.tienePermiso("Bearer " + token, "GESTIONAR_USUARIOS");

        assertTrue(permitido);
    }

    @Test
    void tienePermiso_deberiaRetornarFalseCuandoPermisoNoExiste() {
        String token = jwtService.generarToken(
                1L,
                "admin@perfulandia.cl",
                "ADMINISTRADOR",
                List.of("GESTIONAR_USUARIOS")
        );

        boolean permitido = authService.tienePermiso("Bearer " + token, "ELIMINAR_PRODUCTO");

        assertFalse(permitido);
    }

    @Test
    void tienePermiso_deberiaRetornarFalseCuandoTokenEsInvalido() {
        boolean permitido = authService.tienePermiso("token-invalido", "GESTIONAR_USUARIOS");

        assertFalse(permitido);
    }
}