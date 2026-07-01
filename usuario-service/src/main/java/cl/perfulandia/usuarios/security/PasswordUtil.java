package cl.perfulandia.usuarios.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {

    public String generarHash(String password) {
        if (password == null || password.isBlank()) {
            throw new RuntimeException("La contraseña es obligatoria");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar hash de contraseña", e);
        }
    }

    public boolean verificarPassword(String passwordIngresada, String passwordHashGuardada) {
        if (passwordIngresada == null || passwordIngresada.isBlank()) {
            return false;
        }

        if (passwordHashGuardada == null || passwordHashGuardada.isBlank()) {
            return false;
        }

        String hashIngresado = generarHash(passwordIngresada);
        return hashIngresado.equals(passwordHashGuardada);
    }
}