package com.perfulandia.autenticacion.exception;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void manejarRuntimeException_deberiaRetornarUnauthorized() {
        RuntimeException ex = new RuntimeException("Credenciales inválidas");

        ResponseEntity<Map<String, Object>> response = handler.manejarRuntimeException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(401, response.getBody().get("status"));
        assertEquals("Unauthorized", response.getBody().get("error"));
        assertEquals("Credenciales inválidas", response.getBody().get("mensaje"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void manejarValidaciones_deberiaRetornarBadRequest() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError correoError = new FieldError(
                "loginRequest",
                "correo",
                "El correo es obligatorio"
        );

        FieldError passwordError = new FieldError(
                "loginRequest",
                "password",
                "La contraseña es obligatoria"
        );

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(correoError, passwordError));

        ResponseEntity<Map<String, Object>> response = handler.manejarValidaciones(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Bad Request", response.getBody().get("error"));
        assertEquals("Error de validación", response.getBody().get("mensaje"));
        assertNotNull(response.getBody().get("timestamp"));

        @SuppressWarnings("unchecked")
        List<String> errores = (List<String>) response.getBody().get("errores");

        assertEquals(2, errores.size());
        assertEquals("correo: El correo es obligatorio", errores.get(0));
        assertEquals("password: La contraseña es obligatoria", errores.get(1));
    }
}