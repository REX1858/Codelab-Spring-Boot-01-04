package com.denkitronik.clienteservice.domain.entities;

import com.denkitronik.clienteservice.ClienteServiceApplication;
import com.denkitronik.clienteservice.delivery.exception.ApiErrorResponse;
import com.denkitronik.clienteservice.domain.exception.ClienteNotFoundException;
import com.denkitronik.clienteservice.domain.exception.ClienteServiceException;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class EntitiesAndExceptionsTest {

    @Test
    void testClienteGettersAndSetters() {
        Cliente cliente = new Cliente();
        Region region = new Region();
        region.setId(1L);
        region.setNombre("Caribe");

        Date fecha = new Date();

        cliente.setId(10L);
        cliente.setNombre("Juan");
        cliente.setApellido("Gomez");
        cliente.setEmail("juan.gomez@test.com");
        cliente.setFoto("foto.jpg");
        cliente.setRegion(region);
        cliente.setCreateAt(fecha);

        assertEquals(10L, cliente.getId());
        assertEquals("Juan", cliente.getNombre());
        assertEquals("Gomez", cliente.getApellido());
        assertEquals("juan.gomez@test.com", cliente.getEmail());
        assertEquals("foto.jpg", cliente.getFoto());
        assertEquals(region, cliente.getRegion());
        assertEquals(fecha, cliente.getCreateAt());

        cliente.prePersist();
        assertNotNull(cliente.getCreateAt());
    }

    @Test
    void testRegionGettersAndSetters() {
        Region region = new Region();
        region.setId(2L);
        region.setNombre("Pacífico");

        assertEquals(2L, region.getId());
        assertEquals("Pacífico", region.getNombre());
    }

    @Test
    void testApiErrorResponse() {
        ApiErrorResponse response = new ApiErrorResponse(404, "Not Found", "Mensaje de prueba");

        assertEquals(404, response.getStatus());
        assertEquals("Not Found", response.getError());
        assertEquals("Mensaje de prueba", response.getMensaje());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void testClienteNotFoundException() {
        ClienteNotFoundException ex = new ClienteNotFoundException(7L);

        assertEquals(7L, ex.getId());
        assertEquals("Cliente con id 7 no encontrado", ex.getMessage());
    }

    @Test
    void testClienteServiceException() {
        ClienteServiceException exSimple = new ClienteServiceException("Error simple");
        assertEquals("Error simple", exSimple.getMessage());

        Throwable cause = new RuntimeException("Causa raíz");
        ClienteServiceException exConCausa = new ClienteServiceException("Error compuesto", cause);
        assertEquals("Error compuesto", exConCausa.getMessage());
        assertEquals(cause, exConCausa.getCause());
    }

    @Test
    void testClienteServiceApplicationConstructor() {
        ClienteServiceApplication app = new ClienteServiceApplication();
        assertNotNull(app);
    }
}
