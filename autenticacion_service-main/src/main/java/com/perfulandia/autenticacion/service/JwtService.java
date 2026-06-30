package com.perfulandia.autenticacion.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-minutes}")
    private Long expirationMinutes;

    public String generarToken(Long idUsuario, String correo, String rol, List<String> permisos) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expirationMinutes * 60 * 1000);

        return Jwts.builder()
                .subject(correo)
                .claim("idUsuario", idUsuario)
                .claim("rol", rol)
                .claim("permisos", permisos)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(obtenerClave())
                .compact();
    }

    public boolean tokenEsValido(String token) {
        try {
            obtenerClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public Claims obtenerClaims(String token) {
        return Jwts.parser()
                .verifyWith(obtenerClave())
                .build()
                .parseSignedClaims(limpiarBearer(token))
                .getPayload();
    }

    public String limpiarBearer(String token) {
        if (token == null) {
            return null;
        }

        String tokenLimpio = token.trim();

        if (tokenLimpio.toLowerCase().startsWith("bearer ")) {
            return tokenLimpio.substring(7).trim();
        }

        return tokenLimpio;
    }

    private SecretKey obtenerClave() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}