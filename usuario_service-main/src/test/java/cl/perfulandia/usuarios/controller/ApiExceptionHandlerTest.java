package cl.perfulandia.usuarios.controller;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    void manejarRuntimeException_deberiaRetornarConflictCuandoMensajeDiceYaExiste() {
        ResponseEntity<Map<String, Object>> respuesta =
                handler.manejarRuntimeException(new RuntimeException("El rol ya existe"));

        assertEquals(HttpStatus.CONFLICT, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(409, respuesta.getBody().get("status"));
        assertEquals("Conflict", respuesta.getBody().get("error"));
        assertEquals("El rol ya existe", respuesta.getBody().get("mensaje"));
        assertNotNull(respuesta.getBody().get("timestamp"));
    }

    @Test
    void manejarRuntimeException_deberiaRetornarNotFoundCuandoMensajeDiceNoEncontrado() {
        ResponseEntity<Map<String, Object>> respuesta =
                handler.manejarRuntimeException(new RuntimeException("Usuario no encontrado"));

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(404, respuesta.getBody().get("status"));
        assertEquals("Not Found", respuesta.getBody().get("error"));
        assertEquals("Usuario no encontrado", respuesta.getBody().get("mensaje"));
    }

    @Test
    void manejarRuntimeException_deberiaRetornarNotFoundCuandoMensajeDiceNoEncontrada() {
        ResponseEntity<Map<String, Object>> respuesta =
                handler.manejarRuntimeException(new RuntimeException("Entidad no encontrada"));

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(404, respuesta.getBody().get("status"));
        assertEquals("Entidad no encontrada", respuesta.getBody().get("mensaje"));
    }

    @Test
    void manejarRuntimeException_deberiaRetornarNotFoundCuandoMensajeDiceNoExiste() {
        ResponseEntity<Map<String, Object>> respuesta =
                handler.manejarRuntimeException(new RuntimeException("El permiso no existe"));

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(404, respuesta.getBody().get("status"));
        assertEquals("El permiso no existe", respuesta.getBody().get("mensaje"));
    }

    @Test
    void manejarRuntimeException_deberiaRetornarBadRequestCuandoMensajeDiceObligatorio() {
        ResponseEntity<Map<String, Object>> respuesta =
                handler.manejarRuntimeException(new RuntimeException("El correo es obligatorio"));

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(400, respuesta.getBody().get("status"));
        assertEquals("Bad Request", respuesta.getBody().get("error"));
        assertEquals("El correo es obligatorio", respuesta.getBody().get("mensaje"));
    }

    @Test
    void manejarRuntimeException_deberiaRetornarBadRequestCuandoMensajeDiceInvalid() {
        ResponseEntity<Map<String, Object>> respuesta =
                handler.manejarRuntimeException(new RuntimeException("Formato invalid"));

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(400, respuesta.getBody().get("status"));
        assertEquals("Formato invalid", respuesta.getBody().get("mensaje"));
    }

    @Test
    void manejarRuntimeException_deberiaRetornarBadRequestCuandoMensajeDiceInvalidaConTilde() {
        ResponseEntity<Map<String, Object>> respuesta =
                handler.manejarRuntimeException(new RuntimeException("Solicitud inválida"));

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(400, respuesta.getBody().get("status"));
        assertEquals("Solicitud inválida", respuesta.getBody().get("mensaje"));
    }

    @Test
    void manejarRuntimeException_deberiaRetornarBadRequestCuandoMensajeDiceContrasena() {
        ResponseEntity<Map<String, Object>> respuesta =
                handler.manejarRuntimeException(new RuntimeException("contraseña incorrecta"));

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(400, respuesta.getBody().get("status"));
        assertEquals("contraseña incorrecta", respuesta.getBody().get("mensaje"));
    }

    @Test
    void manejarRuntimeException_deberiaRetornarInternalServerErrorCuandoMensajeEsNull() {
        ResponseEntity<Map<String, Object>> respuesta =
                handler.manejarRuntimeException(new RuntimeException());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(500, respuesta.getBody().get("status"));
        assertEquals("Internal Server Error", respuesta.getBody().get("error"));
        assertEquals("Error interno del servidor", respuesta.getBody().get("mensaje"));
    }

    @Test
    void manejarRuntimeException_deberiaRetornarInternalServerErrorCuandoMensajeNoClasifica() {
        ResponseEntity<Map<String, Object>> respuesta =
                handler.manejarRuntimeException(new RuntimeException("Error desconocido"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(500, respuesta.getBody().get("status"));
        assertEquals("Error desconocido", respuesta.getBody().get("mensaje"));
    }

    @Test
    void manejarValidaciones_deberiaRetornarBadRequestConListaDeErrores() {
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);

        FieldError errorNombre = new FieldError("usuario", "nombre", "no debe estar vacío");
        FieldError errorCorreo = new FieldError("usuario", "correo", "debe tener formato válido");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(errorNombre, errorCorreo));

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarValidaciones(exception);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(400, respuesta.getBody().get("status"));
        assertEquals("Bad Request", respuesta.getBody().get("error"));
        assertTrue(respuesta.getBody().get("mensaje").toString().toLowerCase().contains("validaci"));
        assertNotNull(respuesta.getBody().get("timestamp"));

        @SuppressWarnings("unchecked")
        List<String> errores = (List<String>) respuesta.getBody().get("errores");

        assertEquals(2, errores.size());
        assertTrue(errores.contains("nombre: no debe estar vacío"));
        assertTrue(errores.contains("correo: debe tener formato válido"));
    }
}