package cl.perfulandia.usuarios.controller;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> manejarRuntimeException(RuntimeException ex) {
        String mensaje = ex.getMessage() != null ? ex.getMessage() : "Error interno del servidor";
        HttpStatus estado = obtenerEstadoHttp(mensaje);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", estado.value());
        respuesta.put("error", estado.getReasonPhrase());
        respuesta.put("mensaje", mensaje);

        return ResponseEntity.status(estado).body(respuesta);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidaciones(MethodArgumentNotValidException ex) {
        List<String> errores = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", HttpStatus.BAD_REQUEST.value());
        respuesta.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        respuesta.put("mensaje", "Error de validación");
        respuesta.put("errores", errores);

        return ResponseEntity.badRequest().body(respuesta);
    }

    private HttpStatus obtenerEstadoHttp(String mensaje) {
        String mensajeMinuscula = mensaje.toLowerCase();

        if (mensajeMinuscula.contains("ya existe")) {
            return HttpStatus.CONFLICT;
        }

        if (mensajeMinuscula.contains("no encontrado")
                || mensajeMinuscula.contains("no encontrada")
                || mensajeMinuscula.contains("no existe")) {
            return HttpStatus.NOT_FOUND;
        }

        if (mensajeMinuscula.contains("obligatori")
                || mensajeMinuscula.contains("inválid")
                || mensajeMinuscula.contains("invalid")
                || mensajeMinuscula.contains("contraseña")) {
            return HttpStatus.BAD_REQUEST;
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}