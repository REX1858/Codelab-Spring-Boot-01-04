package com.denkitronik.clienteservice.delivery.exception;

import com.denkitronik.clienteservice.domain.exception.ClienteNotFoundException;
import com.denkitronik.clienteservice.domain.exception.ClienteServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testManejarNotFound() {
        ClienteNotFoundException ex = new ClienteNotFoundException(5L);

        ResponseEntity<ApiErrorResponse> response = handler.manejarNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals("Cliente con id 5 no encontrado", response.getBody().getMensaje());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testManejarServiceException() {
        ClienteServiceException ex = new ClienteServiceException("Error en base de datos");

        ResponseEntity<ApiErrorResponse> response = handler.manejarServiceException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("Error interno del servidor. Por favor intenta más tarde.", response.getBody().getMensaje());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testManejarValidacion() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("cliente", "email", "Debe ser una dirección de email válida");

        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, String>> response = handler.manejarValidacion(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Debe ser una dirección de email válida", response.getBody().get("email"));
    }

    @Test
    void testManejarExcepcionGenerica() {
        Exception ex = new RuntimeException("Error inesperado en el sistema");

        ResponseEntity<ApiErrorResponse> response = handler.manejarExcepcionGenerica(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("Error inesperado. Por favor contacta al administrador.", response.getBody().getMensaje());
        assertNotNull(response.getBody().getTimestamp());
    }
}
