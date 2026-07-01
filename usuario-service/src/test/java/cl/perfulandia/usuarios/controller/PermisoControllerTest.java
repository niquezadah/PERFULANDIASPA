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

import cl.perfulandia.usuarios.model.Permiso;
import cl.perfulandia.usuarios.service.PermisoService;

@ExtendWith(MockitoExtension.class)
class PermisoControllerTest {

    @Mock
    private PermisoService permisoService;

    @InjectMocks
    private PermisoController permisoController;

    @Test
    void listarPermisos_deberiaRetornarOkConLista() {
        Permiso permiso = crearPermiso(1L, "GESTIONAR_USUARIOS", "Permite gestionar usuarios");
        when(permisoService.listarPermisos()).thenReturn(List.of(permiso));

        ResponseEntity<List<Permiso>> response = permisoController.listarPermisos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("GESTIONAR_USUARIOS", response.getBody().get(0).getNombrePermiso());
        verify(permisoService).listarPermisos();
    }

    @Test
    void buscarPermisoPorId_deberiaRetornarOkCuandoExiste() {
        Permiso permiso = crearPermiso(1L, "VER_REPORTES", "Permite ver reportes");
        when(permisoService.buscarPermisoPorId(1L)).thenReturn(Optional.of(permiso));

        ResponseEntity<Permiso> response = permisoController.buscarPermisoPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VER_REPORTES", response.getBody().getNombrePermiso());
        verify(permisoService).buscarPermisoPorId(1L);
    }

    @Test
    void buscarPermisoPorId_deberiaRetornarNotFoundCuandoNoExiste() {
        when(permisoService.buscarPermisoPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<Permiso> response = permisoController.buscarPermisoPorId(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(permisoService).buscarPermisoPorId(99L);
    }

    @Test
    void guardarPermiso_deberiaRetornarOkConPermisoGuardado() {
        Permiso permisoEntrada = crearPermiso(null, "CREAR_PEDIDOS", "Permite crear pedidos");
        Permiso permisoGuardado = crearPermiso(2L, "CREAR_PEDIDOS", "Permite crear pedidos");

        when(permisoService.guardarPermiso(permisoEntrada)).thenReturn(permisoGuardado);

        ResponseEntity<Permiso> response = permisoController.guardarPermiso(permisoEntrada);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2L, response.getBody().getIdPermiso());
        assertEquals("CREAR_PEDIDOS", response.getBody().getNombrePermiso());
        verify(permisoService).guardarPermiso(permisoEntrada);
    }

    @Test
    void actualizarPermiso_deberiaRetornarOkConPermisoActualizado() {
        Permiso permisoEntrada = crearPermiso(null, "EDITAR_USUARIOS", "Actualizado");
        Permiso permisoActualizado = crearPermiso(3L, "EDITAR_USUARIOS", "Actualizado");

        when(permisoService.actualizarPermiso(3L, permisoEntrada)).thenReturn(permisoActualizado);

        ResponseEntity<Permiso> response = permisoController.actualizarPermiso(3L, permisoEntrada);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3L, response.getBody().getIdPermiso());
        assertEquals("EDITAR_USUARIOS", response.getBody().getNombrePermiso());
        verify(permisoService).actualizarPermiso(3L, permisoEntrada);
    }

    @Test
    void eliminarPermiso_deberiaRetornarNoContent() {
        ResponseEntity<Void> response = permisoController.eliminarPermiso(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(permisoService).eliminarPermiso(1L);
    }

    private Permiso crearPermiso(Long id, String nombre, String descripcion) {
        Permiso permiso = new Permiso();
        permiso.setIdPermiso(id);
        permiso.setNombrePermiso(nombre);
        permiso.setDescripcion(descripcion);
        return permiso;
    }
}