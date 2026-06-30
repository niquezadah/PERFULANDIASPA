package cl.perfulandia.usuarios.service;

import java.util.HashSet;
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
import cl.perfulandia.usuarios.model.Rol;
import cl.perfulandia.usuarios.repository.PermisoRepository;
import cl.perfulandia.usuarios.repository.RolRepository;

@ExtendWith(MockitoExtension.class)
class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PermisoRepository permisoRepository;

    @InjectMocks
    private RolService rolService;

    @Test
    void listarRoles_deberiaRetornarLista() {
        Rol rol = new Rol(1L, "ADMINISTRADOR", "Rol administrador", new HashSet<>());
        when(rolRepository.findAll()).thenReturn(List.of(rol));

        List<Rol> resultado = rolService.listarRoles();

        assertEquals(1, resultado.size());
        assertEquals("ADMINISTRADOR", resultado.get(0).getNombreRol());
        verify(rolRepository).findAll();
    }

    @Test
    void buscarRolPorId_deberiaRetornarRolCuandoExiste() {
        Rol rol = new Rol(1L, "ADMINISTRADOR", "Rol administrador", new HashSet<>());
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));

        Optional<Rol> resultado = rolService.buscarRolPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("ADMINISTRADOR", resultado.get().getNombreRol());
        verify(rolRepository).findById(1L);
    }

    @Test
    void buscarRolPorId_deberiaRetornarVacioCuandoNoExiste() {
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Rol> resultado = rolService.buscarRolPorId(99L);

        assertTrue(resultado.isEmpty());
        verify(rolRepository).findById(99L);
    }

    @Test
    void guardarRol_deberiaGuardarCuandoNoExisteNombre() {
        Rol rol = new Rol(null, " ADMINISTRADOR ", " Rol administrador ", new HashSet<>());
        when(rolRepository.existsByNombreRol("ADMINISTRADOR")).thenReturn(false);
        when(rolRepository.save(any(Rol.class))).thenAnswer(invocation -> {
            Rol rolGuardado = invocation.getArgument(0);
            rolGuardado.setIdRol(1L);
            return rolGuardado;
        });

        Rol resultado = rolService.guardarRol(rol);

        assertEquals(1L, resultado.getIdRol());
        assertEquals("ADMINISTRADOR", resultado.getNombreRol());
        assertEquals("Rol administrador", resultado.getDescripcion());
        verify(rolRepository).existsByNombreRol("ADMINISTRADOR");
        verify(rolRepository).save(rol);
    }

    @Test
    void guardarRol_deberiaPermitirNombreNullParaCubrirLimpiezaNull() {
        Rol rol = new Rol(null, null, " Rol sin nombre ", new HashSet<>());
        when(rolRepository.existsByNombreRol(isNull())).thenReturn(false);
        when(rolRepository.save(any(Rol.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Rol resultado = rolService.guardarRol(rol);

        assertNull(resultado.getNombreRol());
        assertEquals("Rol sin nombre", resultado.getDescripcion());
        verify(rolRepository).existsByNombreRol(isNull());
        verify(rolRepository).save(rol);
    }

    @Test
    void guardarRol_deberiaPermitirDescripcionNull() {
        Rol rol = new Rol(null, "CLIENTE", null, new HashSet<>());
        when(rolRepository.existsByNombreRol("CLIENTE")).thenReturn(false);
        when(rolRepository.save(any(Rol.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Rol resultado = rolService.guardarRol(rol);

        assertNull(resultado.getDescripcion());
        verify(rolRepository).save(rol);
    }

    @Test
    void guardarRol_deberiaConvertirDescripcionVaciaEnNull() {
        Rol rol = new Rol(null, "CLIENTE", "   ", new HashSet<>());
        when(rolRepository.existsByNombreRol("CLIENTE")).thenReturn(false);
        when(rolRepository.save(any(Rol.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Rol resultado = rolService.guardarRol(rol);

        assertNull(resultado.getDescripcion());
        verify(rolRepository).save(rol);
    }

    @Test
    void guardarRol_deberiaLanzarExcepcionCuandoNombreYaExiste() {
        Rol rol = new Rol(null, "ADMINISTRADOR", "Duplicado", new HashSet<>());
        when(rolRepository.existsByNombreRol("ADMINISTRADOR")).thenReturn(true);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rolService.guardarRol(rol)
        );

        assertTrue(exception.getMessage().contains("Ya existe"));
        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    void actualizarRol_deberiaActualizarCuandoExiste() {
        Rol existente = new Rol(1L, "ANTIGUO", "Descripcion antigua", new HashSet<>());
        Rol actualizado = new Rol(null, "NUEVO", "Descripcion nueva", new HashSet<>());

        when(rolRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(rolRepository.existsByNombreRol("NUEVO")).thenReturn(false);
        when(rolRepository.save(any(Rol.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Rol resultado = rolService.actualizarRol(1L, actualizado);

        assertEquals("NUEVO", resultado.getNombreRol());
        assertEquals("Descripcion nueva", resultado.getDescripcion());
        verify(rolRepository).save(existente);
    }

    @Test
    void actualizarRol_deberiaActualizarCuandoNombreEsIgual() {
        Rol existente = new Rol(1L, "CLIENTE", "Antes", new HashSet<>());
        Rol actualizado = new Rol(null, "CLIENTE", "Despues", new HashSet<>());

        when(rolRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(rolRepository.save(any(Rol.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Rol resultado = rolService.actualizarRol(1L, actualizado);

        assertEquals("CLIENTE", resultado.getNombreRol());
        assertEquals("Despues", resultado.getDescripcion());
        verify(rolRepository, never()).existsByNombreRol("CLIENTE");
        verify(rolRepository).save(existente);
    }

    @Test
    void actualizarRol_deberiaLanzarExcepcionCuandoNoExiste() {
        Rol actualizado = new Rol(null, "NUEVO", "Descripcion", new HashSet<>());
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rolService.actualizarRol(99L, actualizado)
        );

        assertTrue(exception.getMessage().contains("Rol no encontrado"));
        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    void actualizarRol_deberiaLanzarExcepcionCuandoNombreNuevoYaExiste() {
        Rol existente = new Rol(1L, "ANTIGUO", "Antes", new HashSet<>());
        Rol actualizado = new Rol(null, "NUEVO", "Despues", new HashSet<>());

        when(rolRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(rolRepository.existsByNombreRol("NUEVO")).thenReturn(true);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rolService.actualizarRol(1L, actualizado)
        );

        assertTrue(exception.getMessage().contains("Ya existe"));
        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    void agregarPermisoARol_deberiaAgregarPermisoCuandoRolYPermisoExisten() {
        Rol rol = new Rol(1L, "ADMINISTRADOR", "Rol administrador", new HashSet<>());
        Permiso permiso = new Permiso(1L, "VER_USUARIOS", "Permite ver usuarios");

        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));
        when(permisoRepository.findById(1L)).thenReturn(Optional.of(permiso));
        when(rolRepository.save(any(Rol.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Rol resultado = rolService.agregarPermisoARol(1L, 1L);

        assertEquals(1, resultado.getPermisos().size());
        assertTrue(resultado.getPermisos().contains(permiso));
        verify(rolRepository).save(rol);
    }

    @Test
    void agregarPermisoARol_deberiaLanzarExcepcionCuandoRolNoExiste() {
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rolService.agregarPermisoARol(99L, 1L)
        );

        assertTrue(exception.getMessage().contains("Rol no encontrado"));
        verify(permisoRepository, never()).findById(anyLong());
        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    void agregarPermisoARol_deberiaLanzarExcepcionCuandoPermisoNoExiste() {
        Rol rol = new Rol(1L, "ADMINISTRADOR", "Rol administrador", new HashSet<>());

        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));
        when(permisoRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rolService.agregarPermisoARol(1L, 99L)
        );

        assertTrue(exception.getMessage().contains("Permiso no encontrado"));
        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    void eliminarRol_deberiaEliminarCuandoExiste() {
        when(rolRepository.existsById(1L)).thenReturn(true);

        rolService.eliminarRol(1L);

        verify(rolRepository).deleteById(1L);
    }

    @Test
    void eliminarRol_deberiaLanzarExcepcionCuandoNoExiste() {
        when(rolRepository.existsById(99L)).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rolService.eliminarRol(99L)
        );

        assertTrue(exception.getMessage().contains("Rol no encontrado"));
        verify(rolRepository, never()).deleteById(anyLong());
    }
}