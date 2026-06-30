package cl.perfulandia.usuarios.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isNull;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.perfulandia.usuarios.model.Permiso;
import cl.perfulandia.usuarios.repository.PermisoRepository;

@ExtendWith(MockitoExtension.class)
class PermisoServiceTest {

    @Mock
    private PermisoRepository permisoRepository;

    @InjectMocks
    private PermisoService permisoService;

    @Test
    void listarPermisos_deberiaRetornarLista() {
        Permiso permiso = new Permiso(1L, "GESTIONAR_USUARIOS", "Permite gestionar usuarios");
        when(permisoRepository.findAll()).thenReturn(List.of(permiso));

        List<Permiso> resultado = permisoService.listarPermisos();

        assertEquals(1, resultado.size());
        assertEquals("GESTIONAR_USUARIOS", resultado.get(0).getNombrePermiso());
        verify(permisoRepository).findAll();
    }

    @Test
    void buscarPermisoPorId_deberiaRetornarPermisoCuandoExiste() {
        Permiso permiso = new Permiso(1L, "CONFIGURAR_PERMISOS", "Permite configurar permisos");
        when(permisoRepository.findById(1L)).thenReturn(Optional.of(permiso));

        Optional<Permiso> resultado = permisoService.buscarPermisoPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("CONFIGURAR_PERMISOS", resultado.get().getNombrePermiso());
        verify(permisoRepository).findById(1L);
    }

    @Test
    void buscarPermisoPorId_deberiaRetornarVacioCuandoNoExiste() {
        when(permisoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Permiso> resultado = permisoService.buscarPermisoPorId(99L);

        assertTrue(resultado.isEmpty());
        verify(permisoRepository).findById(99L);
    }

    @Test
    void guardarPermiso_deberiaGuardarCuandoNoExisteNombre() {
        Permiso permiso = new Permiso(null, " GESTIONAR_USUARIOS ", " Permite gestionar usuarios ");
        when(permisoRepository.existsByNombrePermiso("GESTIONAR_USUARIOS")).thenReturn(false);
        when(permisoRepository.save(any(Permiso.class))).thenAnswer(invocation -> {
            Permiso permisoGuardado = invocation.getArgument(0);
            permisoGuardado.setIdPermiso(1L);
            return permisoGuardado;
        });

        Permiso resultado = permisoService.guardarPermiso(permiso);

        assertEquals(1L, resultado.getIdPermiso());
        assertEquals("GESTIONAR_USUARIOS", resultado.getNombrePermiso());
        assertEquals("Permite gestionar usuarios", resultado.getDescripcion());
        verify(permisoRepository).save(permiso);
    }

    @Test
    void guardarPermiso_deberiaPermitirNombreNullParaCubrirLimpiezaNull() {
        Permiso permiso = new Permiso(null, null, " Descripcion ");
        when(permisoRepository.existsByNombrePermiso(isNull())).thenReturn(false);
        when(permisoRepository.save(any(Permiso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Permiso resultado = permisoService.guardarPermiso(permiso);

        assertNull(resultado.getNombrePermiso());
        assertEquals("Descripcion", resultado.getDescripcion());
        verify(permisoRepository).existsByNombrePermiso(isNull());
        verify(permisoRepository).save(permiso);
    }

    @Test
    void guardarPermiso_deberiaPermitirDescripcionNull() {
        Permiso permiso = new Permiso(null, "GESTIONAR_USUARIOS", null);
        when(permisoRepository.existsByNombrePermiso("GESTIONAR_USUARIOS")).thenReturn(false);
        when(permisoRepository.save(any(Permiso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Permiso resultado = permisoService.guardarPermiso(permiso);

        assertNull(resultado.getDescripcion());
        verify(permisoRepository).save(permiso);
    }

    @Test
    void guardarPermiso_deberiaConvertirDescripcionVaciaEnNull() {
        Permiso permiso = new Permiso(null, "GESTIONAR_USUARIOS", "   ");
        when(permisoRepository.existsByNombrePermiso("GESTIONAR_USUARIOS")).thenReturn(false);
        when(permisoRepository.save(any(Permiso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Permiso resultado = permisoService.guardarPermiso(permiso);

        assertNull(resultado.getDescripcion());
        verify(permisoRepository).save(permiso);
    }

    @Test
    void guardarPermiso_deberiaLanzarExcepcionCuandoNombreYaExiste() {
        Permiso permiso = new Permiso(null, "GESTIONAR_USUARIOS", "Duplicado");
        when(permisoRepository.existsByNombrePermiso("GESTIONAR_USUARIOS")).thenReturn(true);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> permisoService.guardarPermiso(permiso)
        );

        assertTrue(exception.getMessage().contains("Ya existe"));
        verify(permisoRepository, never()).save(any(Permiso.class));
    }

    @Test
    void actualizarPermiso_deberiaActualizarCuandoExiste() {
        Permiso existente = new Permiso(1L, "ANTIGUO", "Descripcion antigua");
        Permiso actualizado = new Permiso(null, "NUEVO", "Descripcion nueva");

        when(permisoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(permisoRepository.existsByNombrePermiso("NUEVO")).thenReturn(false);
        when(permisoRepository.save(any(Permiso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Permiso resultado = permisoService.actualizarPermiso(1L, actualizado);

        assertEquals("NUEVO", resultado.getNombrePermiso());
        assertEquals("Descripcion nueva", resultado.getDescripcion());
        verify(permisoRepository).save(existente);
    }

    @Test
    void actualizarPermiso_deberiaActualizarCuandoNombreEsIgual() {
        Permiso existente = new Permiso(1L, "GESTIONAR_USUARIOS", "Antes");
        Permiso actualizado = new Permiso(null, "GESTIONAR_USUARIOS", "Despues");

        when(permisoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(permisoRepository.save(any(Permiso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Permiso resultado = permisoService.actualizarPermiso(1L, actualizado);

        assertEquals("GESTIONAR_USUARIOS", resultado.getNombrePermiso());
        assertEquals("Despues", resultado.getDescripcion());
        verify(permisoRepository, never()).existsByNombrePermiso("GESTIONAR_USUARIOS");
    }

    @Test
    void actualizarPermiso_deberiaLanzarExcepcionCuandoNoExiste() {
        Permiso actualizado = new Permiso(null, "NUEVO", "Descripcion");
        when(permisoRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> permisoService.actualizarPermiso(99L, actualizado)
        );

        assertTrue(exception.getMessage().contains("Permiso no encontrado"));
        verify(permisoRepository, never()).save(any(Permiso.class));
    }

    @Test
    void actualizarPermiso_deberiaLanzarExcepcionCuandoNombreNuevoYaExiste() {
        Permiso existente = new Permiso(1L, "ANTIGUO", "Antes");
        Permiso actualizado = new Permiso(null, "NUEVO", "Despues");

        when(permisoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(permisoRepository.existsByNombrePermiso("NUEVO")).thenReturn(true);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> permisoService.actualizarPermiso(1L, actualizado)
        );

        assertTrue(exception.getMessage().contains("Ya existe"));
        verify(permisoRepository, never()).save(any(Permiso.class));
    }

    @Test
    void eliminarPermiso_deberiaEliminarCuandoExiste() {
        when(permisoRepository.existsById(1L)).thenReturn(true);

        permisoService.eliminarPermiso(1L);

        verify(permisoRepository).deleteById(1L);
    }

    @Test
    void eliminarPermiso_deberiaLanzarExcepcionCuandoNoExiste() {
        when(permisoRepository.existsById(99L)).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> permisoService.eliminarPermiso(99L)
        );

        assertTrue(exception.getMessage().contains("Permiso no encontrado"));
        verify(permisoRepository, never()).deleteById(anyLong());
    }
}