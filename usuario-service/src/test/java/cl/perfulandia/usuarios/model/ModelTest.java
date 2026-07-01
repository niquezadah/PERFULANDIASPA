package cl.perfulandia.usuarios.model;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ModelTest {

    @Test
    void permiso_deberiaCubrirConstructorVacioSettersYConstructorConParametros() {
        Permiso permiso = new Permiso();
        permiso.setIdPermiso(1L);
        permiso.setNombrePermiso("VER_USUARIOS");
        permiso.setDescripcion("Permite ver usuarios");

        assertEquals(1L, permiso.getIdPermiso());
        assertEquals("VER_USUARIOS", permiso.getNombrePermiso());
        assertEquals("Permite ver usuarios", permiso.getDescripcion());

        Permiso permisoConConstructor = new Permiso(
                2L,
                "CREAR_USUARIOS",
                "Permite crear usuarios"
        );

        assertEquals(2L, permisoConConstructor.getIdPermiso());
        assertEquals("CREAR_USUARIOS", permisoConConstructor.getNombrePermiso());
        assertEquals("Permite crear usuarios", permisoConConstructor.getDescripcion());
    }

    @Test
    void rol_deberiaCubrirConstructorVacioSettersYConstructorConParametros() {
        Permiso permiso = new Permiso(1L, "VER_USUARIOS", "Permite ver usuarios");
        Set<Permiso> permisos = new HashSet<>();
        permisos.add(permiso);

        Rol rol = new Rol();
        rol.setIdRol(1L);
        rol.setNombreRol("ADMINISTRADOR");
        rol.setDescripcion("Rol administrador");
        rol.setPermisos(permisos);

        assertEquals(1L, rol.getIdRol());
        assertEquals("ADMINISTRADOR", rol.getNombreRol());
        assertEquals("Rol administrador", rol.getDescripcion());
        assertEquals(1, rol.getPermisos().size());
        assertTrue(rol.getPermisos().contains(permiso));

        Rol rolConConstructor = new Rol(
                2L,
                "CLIENTE",
                "Rol cliente",
                permisos
        );

        assertEquals(2L, rolConConstructor.getIdRol());
        assertEquals("CLIENTE", rolConConstructor.getNombreRol());
        assertEquals("Rol cliente", rolConConstructor.getDescripcion());
        assertEquals(1, rolConConstructor.getPermisos().size());
    }

    @Test
    void rol_deberiaInicializarPermisosVaciosCuandoRecibeNull() {
        Rol rol = new Rol(1L, "ADMINISTRADOR", "Rol administrador", null);

        assertNotNull(rol.getPermisos());
        assertTrue(rol.getPermisos().isEmpty());

        rol.setPermisos(null);

        assertNotNull(rol.getPermisos());
        assertTrue(rol.getPermisos().isEmpty());
    }

    @Test
    void usuario_deberiaCubrirConstructorVacioSettersYConstructorConParametros() {
        Rol rol = new Rol(1L, "ADMINISTRADOR", "Rol administrador", new HashSet<>());

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNombre("Admin");
        usuario.setApellido("Sistema");
        usuario.setCorreo("admin@perfulandia.cl");
        usuario.setPasswordHash("hash123");
        usuario.setDireccionEnvio("Casa matriz");
        usuario.setEstado(true);
        usuario.setRol(rol);

        assertEquals(1L, usuario.getIdUsuario());
        assertEquals("Admin", usuario.getNombre());
        assertEquals("Sistema", usuario.getApellido());
        assertEquals("admin@perfulandia.cl", usuario.getCorreo());
        assertEquals("hash123", usuario.getPasswordHash());
        assertEquals("Casa matriz", usuario.getDireccionEnvio());
        assertEquals(true, usuario.getEstado());
        assertEquals(rol, usuario.getRol());

        Usuario usuarioConConstructor = new Usuario(
                2L,
                "Cliente",
                "Final",
                "cliente@perfulandia.cl",
                "hash456",
                "Av. Siempre Viva 123",
                false,
                rol
        );

        assertEquals(2L, usuarioConConstructor.getIdUsuario());
        assertEquals("Cliente", usuarioConConstructor.getNombre());
        assertEquals("Final", usuarioConConstructor.getApellido());
        assertEquals("cliente@perfulandia.cl", usuarioConConstructor.getCorreo());
        assertEquals("hash456", usuarioConConstructor.getPasswordHash());
        assertEquals("Av. Siempre Viva 123", usuarioConConstructor.getDireccionEnvio());
        assertEquals(false, usuarioConConstructor.getEstado());
        assertEquals(rol, usuarioConConstructor.getRol());
    }

    @Test
    void usuario_deberiaDejarEstadoTrueCuandoConstructorRecibeEstadoNull() {
        Rol rol = new Rol(1L, "ADMINISTRADOR", "Rol administrador", new HashSet<>());

        Usuario usuario = new Usuario(
                1L,
                "Admin",
                "Sistema",
                "admin@perfulandia.cl",
                "hash123",
                "Casa matriz",
                null,
                rol
        );

        assertEquals(true, usuario.getEstado());
    }
}