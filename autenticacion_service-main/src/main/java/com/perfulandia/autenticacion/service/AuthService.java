package com.perfulandia.autenticacion.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.perfulandia.autenticacion.client.UsuarioClient;
import com.perfulandia.autenticacion.dto.LoginRequest;
import com.perfulandia.autenticacion.dto.LoginResponse;
import com.perfulandia.autenticacion.dto.UsuarioAuthResponse;
import com.perfulandia.autenticacion.dto.ValidarTokenResponse;

import io.jsonwebtoken.Claims;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final UsuarioClient usuarioClient;

    public AuthService(JwtService jwtService, UsuarioClient usuarioClient) {
        this.jwtService = jwtService;
        this.usuarioClient = usuarioClient;
    }

    public LoginResponse login(LoginRequest request) {
        UsuarioAuthResponse usuario = usuarioClient.validarCredenciales(request);

        List<String> permisos = usuario.getPermisos() != null
                ? usuario.getPermisos()
                : List.of();

        String token = jwtService.generarToken(
                usuario.getIdUsuario(),
                usuario.getCorreo(),
                usuario.getRol(),
                permisos
        );

        return new LoginResponse(
                token,
                "Bearer",
                usuario.getIdUsuario(),
                usuario.getCorreo(),
                usuario.getRol(),
                permisos
        );
    }

    public ValidarTokenResponse validarToken(String token) {
        if (token == null || token.isBlank()) {
            return ValidarTokenResponse.invalido("Token no informado");
        }

        if (!jwtService.tokenEsValido(token)) {
            return ValidarTokenResponse.invalido("Token inválido o expirado");
        }

        Claims claims = jwtService.obtenerClaims(token);

        Long idUsuario = claims.get("idUsuario", Long.class);
        String correo = claims.getSubject();
        String rol = claims.get("rol", String.class);

        @SuppressWarnings("unchecked")
        List<String> permisos = claims.get("permisos", List.class);

        return new ValidarTokenResponse(
                true,
                "Token válido",
                idUsuario,
                correo,
                rol,
                permisos
        );
    }

    public boolean tienePermiso(String token, String permiso) {
        ValidarTokenResponse respuesta = validarToken(token);

        return respuesta.isValido()
                && respuesta.getPermisos() != null
                && respuesta.getPermisos().contains(permiso);
    }
}