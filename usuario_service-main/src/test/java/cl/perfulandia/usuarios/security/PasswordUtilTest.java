package cl.perfulandia.usuarios.security;

import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;

class PasswordUtilTest {

    private final PasswordUtil passwordUtil = new PasswordUtil();

    @Test
    void generarHash_deberiaRetornarHashCuandoPasswordEsValida() {
        String password = "admin123";

        String hash = passwordUtil.generarHash(password);

        assertNotNull(hash);
        assertFalse(hash.isBlank());
        assertNotEquals(password, hash);
    }

    @Test
    void generarHash_deberiaRetornarMismoHashParaMismaPassword() {
        String password = "cliente123";

        String hashUno = passwordUtil.generarHash(password);
        String hashDos = passwordUtil.generarHash(password);

        assertEquals(hashUno, hashDos);
    }

    @Test
    void generarHash_deberiaLanzarExcepcionCuandoPasswordEsNull() {
        String password = null;

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> passwordUtil.generarHash(password)
        );

        assertTrue(exception.getMessage().contains("contrase"));
    }

    @Test
    void generarHash_deberiaLanzarExcepcionCuandoPasswordEstaVacia() {
        String password = "   ";

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> passwordUtil.generarHash(password)
        );

        assertTrue(exception.getMessage().contains("contrase"));
    }

    @Test
    void generarHash_deberiaLanzarExcepcionCuandoFallaMessageDigest() {
        try (MockedStatic<MessageDigest> messageDigestMock = mockStatic(MessageDigest.class)) {
            messageDigestMock
                    .when(() -> MessageDigest.getInstance("SHA-256"))
                    .thenThrow(new RuntimeException("Algoritmo no disponible"));

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> passwordUtil.generarHash("admin123")
            );

            assertTrue(exception.getMessage().contains("Error al generar hash"));
            assertNotNull(exception.getCause());
        }
    }

    @Test
    void verificarPassword_deberiaRetornarTrueCuandoPasswordCoincide() {
        String password = "ventas123";
        String hashGuardado = passwordUtil.generarHash(password);

        boolean resultado = passwordUtil.verificarPassword(password, hashGuardado);

        assertTrue(resultado);
    }

    @Test
    void verificarPassword_deberiaRetornarFalseCuandoPasswordNoCoincide() {
        String passwordCorrecta = "ventas123";
        String passwordIncorrecta = "otraClave123";
        String hashGuardado = passwordUtil.generarHash(passwordCorrecta);

        boolean resultado = passwordUtil.verificarPassword(passwordIncorrecta, hashGuardado);

        assertFalse(resultado);
    }

    @Test
    void verificarPassword_deberiaRetornarFalseCuandoPasswordIngresadaEsNull() {
        String hashGuardado = passwordUtil.generarHash("admin123");

        boolean resultado = passwordUtil.verificarPassword(null, hashGuardado);

        assertFalse(resultado);
    }

    @Test
    void verificarPassword_deberiaRetornarFalseCuandoPasswordIngresadaEstaVacia() {
        String hashGuardado = passwordUtil.generarHash("admin123");

        boolean resultado = passwordUtil.verificarPassword(" ", hashGuardado);

        assertFalse(resultado);
    }

    @Test
    void verificarPassword_deberiaRetornarFalseCuandoHashGuardadoEsNull() {
        boolean resultado = passwordUtil.verificarPassword("admin123", null);

        assertFalse(resultado);
    }

    @Test
    void verificarPassword_deberiaRetornarFalseCuandoHashGuardadoEstaVacio() {
        boolean resultado = passwordUtil.verificarPassword("admin123", " ");

        assertFalse(resultado);
    }
}