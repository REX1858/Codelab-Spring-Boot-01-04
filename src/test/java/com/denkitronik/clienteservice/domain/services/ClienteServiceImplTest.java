package com.denkitronik.clienteservice.domain.services;

import com.denkitronik.clienteservice.domain.entities.Cliente;
import com.denkitronik.clienteservice.domain.entities.Region;
import com.denkitronik.clienteservice.domain.exception.ClienteNotFoundException;
import com.denkitronik.clienteservice.domain.exception.ClienteServiceException;
import com.denkitronik.clienteservice.domain.repositories.IClienteDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

    @Mock
    private IClienteDao clienteDao;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    @Test
    void testFindAllPageable() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Cliente> page = new PageImpl<>(List.of(new Cliente()));
        when(clienteDao.findAll(pageable)).thenReturn(page);

        Page<Cliente> result = clienteService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(clienteDao).findAll(pageable);
    }

    @Test
    void testFindAll() {
        List<Cliente> clientes = List.of(new Cliente(), new Cliente());
        when(clienteDao.findAll()).thenReturn(clientes);

        List<Cliente> result = clienteService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(clienteDao).findAll();
    }

    @Test
    void testFindById_Success() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        when(clienteDao.findById(1L)).thenReturn(Optional.of(cliente));

        Cliente result = clienteService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(clienteDao).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(clienteDao.findById(99L)).thenReturn(Optional.empty());

        ClienteNotFoundException ex = assertThrows(ClienteNotFoundException.class, () -> {
            clienteService.findById(99L);
        });

        assertEquals(99L, ex.getId());
        verify(clienteDao).findById(99L);
    }

    @Test
    void testSave_Success() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Andres");
        when(clienteDao.save(cliente)).thenReturn(cliente);

        Cliente result = clienteService.save(cliente);

        assertNotNull(result);
        assertEquals("Andres", result.getNombre());
        verify(clienteDao).save(cliente);
    }

    @Test
    void testSave_DataIntegrityViolationException() {
        Cliente cliente = new Cliente();
        when(clienteDao.save(cliente)).thenThrow(new DataIntegrityViolationException("Constraint violation"));

        ClienteServiceException ex = assertThrows(ClienteServiceException.class, () -> {
            clienteService.save(cliente);
        });

        assertTrue(ex.getMessage().contains("datos duplicados o restricción violada"));
        assertNotNull(ex.getCause());
        verify(clienteDao).save(cliente);
    }

    @Test
    void testUpdate_Success() {
        Cliente existente = new Cliente();
        existente.setId(1L);
        existente.setNombre("NombreAntiguo");

        Region region = new Region();
        region.setId(10L);
        region.setNombre("Andina");

        Cliente nuevo = new Cliente();
        nuevo.setNombre("NombreNuevo");
        nuevo.setApellido("ApellidoNuevo");
        nuevo.setEmail("nuevo@correo.com");
        nuevo.setFoto("foto.png");
        nuevo.setRegion(region);

        when(clienteDao.findById(1L)).thenReturn(Optional.of(existente));
        when(clienteDao.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cliente result = clienteService.update(1L, nuevo);

        assertNotNull(result);
        assertEquals("NombreNuevo", result.getNombre());
        assertEquals("ApellidoNuevo", result.getApellido());
        assertEquals("nuevo@correo.com", result.getEmail());
        assertEquals("foto.png", result.getFoto());
        assertEquals(region, result.getRegion());
        verify(clienteDao).findById(1L);
        verify(clienteDao).save(existente);
    }

    @Test
    void testUpdate_NotFound() {
        Cliente nuevo = new Cliente();
        when(clienteDao.findById(99L)).thenReturn(Optional.empty());

        ClienteNotFoundException ex = assertThrows(ClienteNotFoundException.class, () -> {
            clienteService.update(99L, nuevo);
        });

        assertEquals(99L, ex.getId());
        verify(clienteDao).findById(99L);
        verify(clienteDao, never()).save(any(Cliente.class));
    }

    @Test
    void testDelete_Success() {
        when(clienteDao.existsById(1L)).thenReturn(true);
        doNothing().when(clienteDao).deleteById(1L);

        clienteService.delete(1L);

        verify(clienteDao).existsById(1L);
        verify(clienteDao).deleteById(1L);
    }

    @Test
    void testDelete_NotFound() {
        when(clienteDao.existsById(99L)).thenReturn(false);

        ClienteNotFoundException ex = assertThrows(ClienteNotFoundException.class, () -> {
            clienteService.delete(99L);
        });

        assertEquals(99L, ex.getId());
        verify(clienteDao).existsById(99L);
        verify(clienteDao, never()).deleteById(anyLong());
    }

    @Test
    void testFindAllRegiones() {
        Region region = new Region();
        region.setId(1L);
        region.setNombre("Antioquia");
        when(clienteDao.findAllRegiones()).thenReturn(List.of(region));

        List<Region> result = clienteService.findAllRegiones();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Antioquia", result.get(0).getNombre());
        verify(clienteDao).findAllRegiones();
    }
}
