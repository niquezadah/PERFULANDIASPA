package com.perfulandia.pedido.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void manejarRecursoNoEncontrado_deberiaRetornarStatus404() {
        RecursoNoEncontradoException exception =
                new RecursoNoEncontradoException("Pedido no encontrado");

        ResponseEntity<Map<String, Object>> response =
                handler.manejarRecursoNoEncontrado(exception);

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().get("estado"));
        assertEquals("Recurso no encontrado", response.getBody().get("error"));
        assertEquals("Pedido no encontrado", response.getBody().get("mensaje"));
    }

    @Test
    void manejarReglaNegocio_deberiaRetornarStatus400() {
        ReglaNegocioException exception =
                new ReglaNegocioException("No se puede cancelar un pedido entregado");

        ResponseEntity<Map<String, Object>> response =
                handler.manejarReglaNegocio(exception);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("estado"));
        assertEquals("Regla de negocio", response.getBody().get("error"));
        assertEquals("No se puede cancelar un pedido entregado", response.getBody().get("mensaje"));
    }
}