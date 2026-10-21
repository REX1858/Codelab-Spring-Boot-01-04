package com.denkitronik.clienteservice.delivery.rest;

import com.denkitronik.clienteservice.domain.entities.Cliente;
import com.denkitronik.clienteservice.domain.entities.Region;
import com.denkitronik.clienteservice.domain.services.IClienteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteRestControllerTest {

    @Mock
    private IClienteService clienteService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private ClienteRestController clienteRestController;

    @Test
    void testListarClientes() {
        List<Cliente> clientes = List.of(new Cliente());
        when(clienteService.findAll()).thenReturn(clientes);

        ResponseEntity<List<Cliente>> response = clienteRestController.listarClientes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(clientes, response.getBody());
        verify(clienteService).findAll();
    }

    @Test
    void testListarClientesPaginado() {
        Page<Cliente> page = new PageImpl<>(List.of(new Cliente()));
        when(clienteService.findAll(PageRequest.of(0, 4))).thenReturn(page);

        ResponseEntity<Page<Cliente>> response = clienteRestController.listarClientesPaginado(0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(page, response.getBody());
        verify(clienteService).findAll(PageRequest.of(0, 4));
    }

    @Test
    void testBuscarCliente() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        when(clienteService.findById(1L)).thenReturn(cliente);

        ResponseEntity<Cliente> response = clienteRestController.buscarCliente(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cliente, response.getBody());
        verify(clienteService).findById(1L);
    }

    @Test
    void testCrearCliente_Success() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Andres");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(clienteService.save(cliente)).thenReturn(cliente);

        ResponseEntity<?> response = clienteRestController.crearCliente(cliente, bindingResult);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(cliente, response.getBody());
        verify(clienteService).save(cliente);
    }

    @Test
    void testCrearCliente_ValidationError() {
        Cliente cliente = new Cliente();
        FieldError fieldError = new FieldError("cliente", "nombre", "El nombre no puede estar vacío");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<?> response = clienteRestController.crearCliente(cliente, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue(body.containsKey("errors"));
        verify(clienteService, never()).save(any(Cliente.class));
    }

    @Test
    void testActualizarCliente_Success() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Carlos");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(clienteService.update(1L, cliente)).thenReturn(cliente);

        ResponseEntity<?> response = clienteRestController.actualizarCliente(cliente, bindingResult, 1L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(cliente, response.getBody());
        verify(clienteService).update(1L, cliente);
    }

    @Test
    void testActualizarCliente_ValidationError() {
        Cliente cliente = new Cliente();
        FieldError fieldError = new FieldError("cliente", "email", "Debe ser una dirección de email válida");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<?> response = clienteRestController.actualizarCliente(cliente, bindingResult, 1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue(body.containsKey("errors"));
        verify(clienteService, never()).update(anyLong(), any(Cliente.class));
    }

    @Test
    void testEliminarCliente() {
        doNothing().when(clienteService).delete(1L);

        ResponseEntity<Void> response = clienteRestController.eliminarCliente(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(clienteService).delete(1L);
    }

    @Test
    void testListarRegiones() {
        List<Region> regiones = List.of(new Region());
        when(clienteService.findAllRegiones()).thenReturn(regiones);

        ResponseEntity<List<Region>> response = clienteRestController.listarRegiones();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(regiones, response.getBody());
        verify(clienteService).findAllRegiones();
    }
}
