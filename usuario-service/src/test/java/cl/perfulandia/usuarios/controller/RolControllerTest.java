package cl.perfulandia.usuarios.controller;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import cl.perfulandia.usuarios.model.Rol;
import cl.perfulandia.usuarios.service.RolService;

@ExtendWith(MockitoExtension.class)
class RolControllerTest {

    @Mock
    private RolService rolService;

    @InjectMocks
    private RolController rolController;

    @Test
    void listarRoles_deberiaRetornarOkConLista() {
        Rol rol = crearRol(1L, "ADMINISTRADOR", "Rol admin");
        when(rolService.listarRoles()).thenReturn(List.of(rol));

        ResponseEntity<List<Rol>> response = rolController.listarRoles();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("ADMINISTRADOR", response.getBody().get(0).getNombreRol());
        verify(rolService).listarRoles();
    }

    @Test
    void buscarRolPorId_deberiaRetornarOkCuandoExiste() {
        Rol rol = crearRol(1L, "CLIENTE", "Rol cliente");
        when(rolService.buscarRolPorId(1L)).thenReturn(Optional.of(rol));

        ResponseEntity<Rol> response = rolController.buscarRolPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CLIENTE", response.getBody().getNombreRol());
        verify(rolService).buscarRolPorId(1L);
    }

    @Test
    void buscarRolPorId_deberiaRetornarNotFoundCuandoNoExiste() {
        when(rolService.buscarRolPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<Rol> response = rolController.buscarRolPorId(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(rolService).buscarRolPorId(99L);
    }

    @Test
    void guardarRol_deberiaRetornarOkConRolGuardado() {
        Rol rolEntrada = crearRol(null, "LOGISTICA", "Rol logística");
        Rol rolGuardado = crearRol(5L, "LOGISTICA", "Rol logística");

        when(rolService.guardarRol(rolEntrada)).thenReturn(rolGuardado);

        ResponseEntity<Rol> response = rolController.guardarRol(rolEntrada);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5L, response.getBody().getIdRol());
        assertEquals("LOGISTICA", response.getBody().getNombreRol());
        verify(rolService).guardarRol(rolEntrada);
    }

    @Test
    void actualizarRol_deberiaRetornarOkConRolActualizado() {
        Rol rolEntrada = crearRol(null, "GERENTE_SUCURSAL", "Actualizado");
        Rol rolActualizado = crearRol(3L, "GERENTE_SUCURSAL", "Actualizado");

        when(rolService.actualizarRol(3L, rolEntrada)).thenReturn(rolActualizado);

        ResponseEntity<Rol> response = rolController.actualizarRol(3L, rolEntrada);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3L, response.getBody().getIdRol());
        assertEquals("GERENTE_SUCURSAL", response.getBody().getNombreRol());
        verify(rolService).actualizarRol(3L, rolEntrada);
    }

    @Test
    void agregarPermisoARol_deberiaRetornarOkConRolActualizado() {
        Rol rolActualizado = crearRol(1L, "ADMINISTRADOR", "Con permiso");

        when(rolService.agregarPermisoARol(1L, 10L)).thenReturn(rolActualizado);

        ResponseEntity<Rol> response = rolController.agregarPermisoARol(1L, 10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getIdRol());
        assertEquals("ADMINISTRADOR", response.getBody().getNombreRol());
        verify(rolService).agregarPermisoARol(1L, 10L);
    }

    @Test
    void eliminarRol_deberiaRetornarNoContent() {
        ResponseEntity<Void> response = rolController.eliminarRol(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(rolService).eliminarRol(1L);
    }

    private Rol crearRol(Long id, String nombre, String descripcion) {
        Rol rol = new Rol();
        rol.setIdRol(id);
        rol.setNombreRol(nombre);
        rol.setDescripcion(descripcion);
        return rol;
    }
}