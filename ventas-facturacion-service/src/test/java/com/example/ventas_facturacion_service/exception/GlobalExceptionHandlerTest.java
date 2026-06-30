package com.example.ventas_facturacion_service.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void manejarErroresValidacion_deberiaRetornarBadRequestConMensajes() {
        //given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError = new FieldError(
                "ventaFacturaDTO",
                "idCliente",
                "El ID del CLIENTE es OBLIGATORIO"
        );

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        //when
        ResponseEntity<Map<String, Object>> respuesta =
                globalExceptionHandler.manejarErroresValidacion(exception);

        //then
        assertEquals(400, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals(400, respuesta.getBody().get("status"));
        assertEquals("ERROR VALIDACIÓN", respuesta.getBody().get("error"));
        assertNotNull(respuesta.getBody().get("timestamp"));

        @SuppressWarnings("unchecked")
        Map<String, String> mensajes = (Map<String, String>) respuesta.getBody().get("mensajes");

        assertEquals("El ID del CLIENTE es OBLIGATORIO", mensajes.get("idCliente"));
    }

    @Test
    void manejarRuntimeException_deberiaRetornarBadRequestConMensaje() {
        //given
        RuntimeException exception = new RuntimeException("El carrito del cliente con ID 1 está vacío");

        //when
        ResponseEntity<Map<String, Object>> respuesta =
                globalExceptionHandler.manejarRuntimeException(exception);

        //then
        assertEquals(400, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals(400, respuesta.getBody().get("status"));
        assertEquals("ERROR DE SOLICITUD", respuesta.getBody().get("error"));
        assertEquals("El carrito del cliente con ID 1 está vacío", respuesta.getBody().get("mensaje"));
        assertNotNull(respuesta.getBody().get("timestamp"));
    }
}