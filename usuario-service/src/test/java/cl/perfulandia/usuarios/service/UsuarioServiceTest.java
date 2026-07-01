package cl.perfulandia.usuarios.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.perfulandia.usuarios.dto.UsuarioAuthResponse;
import cl.perfulandia.usuarios.dto.UsuarioResponse;
import cl.perfulandia.usuarios.model.Permiso;
import cl.perfulandia.usuarios.model.Rol;
import cl.perfulandia.usuarios.model.Usuario;
import cl.perfulandia.usuarios.repository.RolRepository;
import cl.perfulandia.usuarios.repository.UsuarioRepository;
import cl.perfulandia.usuarios.security.PasswordUtil;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordUtil passwordUtil;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void listarUsuarios_deberiaRetornarLista() {
        Usuario usuario = usuarioEjemplo(1L, "admin@perfulandia.cl", true, rolEjemplo(1L));

        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<Usuario> resultado = usuarioService.listarUsuarios();

        assertEquals(1, resultado.size());
        assertEquals("admin@perfulandia.cl", resultado.get(0).getCorreo());
        verify(usuarioRepository).findAll();
    }

    @Test
    void listarUsuariosPorEstado_deberiaRetornarUsuariosActivos() {
        Usuario usuario = usuarioEjemplo(1L, "admin@perfulandia.cl", true, rolEjemplo(1L));

        when(usuarioRepository.findByEstado(true)).thenReturn(List.of(usuario));

        List<Usuario> resultado = usuarioService.listarUsuariosPorEstado(true);

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getEstado());
        verify(usuarioRepository).findByEstado(true);
    }

    @Test
    void buscarUsuarioPorId_deberiaRetornarUsuarioCuandoExiste() {
        Usuario usuario = usuarioEjemplo(1L, "admin@perfulandia.cl", true, rolEjemplo(1L));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.buscarUsuarioPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("admin@perfulandia.cl", resultado.get().getCorreo());
        verify(usuarioRepository).findById(1L);
    }

    @Test
    void guardarUsuario_deberiaGuardarUsuarioNuevo() {
        Rol rol = rolEjemplo(1L);
        Usuario usuario = usuarioRequest(" Admin ", " Sistema ", " ADMIN@PERFULANDIA.CL ", "123456", " Santiago ", null, rol);

        when(usuarioRepository.existsByCorreo("admin@perfulandia.cl")).thenReturn(false);
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));
        when(passwordUtil.generarHash("123456")).thenReturn("HASH_123456");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario guardado = invocation.getArgument(0);
            guardado.setIdUsuario(1L);
            return guardado;
        });

        Usuario resultado = usuarioService.guardarUsuario(usuario);

        assertEquals(1L, resultado.getIdUsuario());
        assertEquals("Admin", resultado.getNombre());
        assertEquals("Sistema", resultado.getApellido());
        assertEquals("admin@perfulandia.cl", resultado.getCorreo());
        assertEquals("Santiago", resultado.getDireccionEnvio());
        assertEquals("HASH_123456", resultado.getPasswordHash());
        assertTrue(resultado.getEstado());
        assertEquals(1L, resultado.getRol().getIdRol());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void guardarUsuario_deberiaMantenerEstadoFalseSiVieneInformado() {
        Rol rol = rolEjemplo(2L);
        Usuario usuario = usuarioRequest("Camila", "Torres", "camila@gmail.com", "123456", null, false, rol);

        when(usuarioRepository.existsByCorreo("camila@gmail.com")).thenReturn(false);
        when(rolRepository.findById(2L)).thenReturn(Optional.of(rol));
        when(passwordUtil.generarHash("123456")).thenReturn("HASH");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.guardarUsuario(usuario);

        assertFalse(resultado.getEstado());
        assertNull(resultado.getDireccionEnvio());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void guardarUsuario_deberiaPermitirNombreApellidoYCorreoNullParaCubrirLimpiezaNull() {
        Rol rol = rolEjemplo(1L);
        Usuario usuario = usuarioRequest(null, null, null, "123456", " Valparaiso ", null, rol);

        when(usuarioRepository.existsByCorreo(isNull())).thenReturn(false);
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));
        when(passwordUtil.generarHash("123456")).thenReturn("HASH_123456");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.guardarUsuario(usuario);

        assertNull(resultado.getNombre());
        assertNull(resultado.getApellido());
        assertNull(resultado.getCorreo());
        assertEquals("Valparaiso", resultado.getDireccionEnvio());
        assertTrue(resultado.getEstado());
        assertEquals("HASH_123456", resultado.getPasswordHash());
        verify(usuarioRepository).existsByCorreo(isNull());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void guardarUsuario_deberiaLanzarExcepcionCuandoCorreoExiste() {
        Usuario usuario = usuarioRequest("Admin", "Sistema", "admin@perfulandia.cl", "123456", null, true, rolEjemplo(1L));

        when(usuarioRepository.existsByCorreo("admin@perfulandia.cl")).thenReturn(true);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> usuarioService.guardarUsuario(usuario)
        );

        assertTrue(exception.getMessage().contains("Ya existe"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void guardarUsuario_deberiaLanzarExcepcionCuandoRolEsNull() {
        Usuario usuario = usuarioRequest("Admin", "Sistema", "admin@perfulandia.cl", "123456", null, true, null);

        when(usuarioRepository.existsByCorreo("admin@perfulandia.cl")).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> usuarioService.guardarUsuario(usuario)
        );

        assertTrue(exception.getMessage().contains("rol"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void guardarUsuario_deberiaLanzarExcepcionCuandoIdRolEsNull() {
        Rol rolSinId = new Rol(null, "CLIENTE", "Cliente", new HashSet<>());
        Usuario usuario = usuarioRequest("Admin", "Sistema", "admin@perfulandia.cl", "123456", null, true, rolSinId);

        when(usuarioRepository.existsByCorreo("admin@perfulandia.cl")).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> usuarioService.guardarUsuario(usuario)
        );

        assertTrue(exception.getMessage().contains("rol"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void guardarUsuario_deberiaLanzarExcepcionCuandoRolNoExiste() {
        Usuario usuario = usuarioRequest("Admin", "Sistema", "admin@perfulandia.cl", "123456", null, true, rolEjemplo(99L));

        when(usuarioRepository.existsByCorreo("admin@perfulandia.cl")).thenReturn(false);
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> usuarioService.guardarUsuario(usuario)
        );

        assertTrue(exception.getMessage().contains("Rol no encontrado"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void actualizarUsuario_deberiaActualizarSinCambiarPasswordCuandoPasswordVieneNull() {
        Rol rol = rolEjemplo(1L);
        Usuario existente = usuarioEjemplo(1L, "admin@perfulandia.cl", true, rol);
        Usuario request = usuarioRequest("Admin", "Actualizado", "admin@perfulandia.cl", null, " Nueva direccion ", null, rol);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.actualizarUsuario(1L, request);

        assertEquals("Admin", resultado.getNombre());
        assertEquals("Actualizado", resultado.getApellido());
        assertEquals("Nueva direccion", resultado.getDireccionEnvio());
        assertEquals("HASH_GUARDADO", resultado.getPasswordHash());
        assertTrue(resultado.getEstado());
        verify(passwordUtil, never()).generarHash(anyString());
    }

    @Test
    void actualizarUsuario_noDeberiaCambiarPasswordCuandoPasswordVieneEnBlanco() {
        Rol rol = rolEjemplo(1L);
        Usuario existente = usuarioEjemplo(1L, "admin@perfulandia.cl", true, rol);
        Usuario request = usuarioRequest(" Admin ", " Sistema ", " ADMIN@PERFULANDIA.CL ", "   ", null, true, rol);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.actualizarUsuario(1L, request);

        assertEquals("Admin", resultado.getNombre());
        assertEquals("Sistema", resultado.getApellido());
        assertEquals("admin@perfulandia.cl", resultado.getCorreo());
        assertEquals("HASH_GUARDADO", resultado.getPasswordHash());
        assertNull(resultado.getDireccionEnvio());
        verify(passwordUtil, never()).generarHash(anyString());
        verify(usuarioRepository).save(existente);
    }

    @Test
    void actualizarUsuario_deberiaActualizarCorreoYPassword() {
        Rol rol = rolEjemplo(1L);
        Usuario existente = usuarioEjemplo(1L, "admin@perfulandia.cl", true, rol);
        Usuario request = usuarioRequest("Admin", "Sistema", "NUEVO@PERFULANDIA.CL", "nueva123", "   ", false, rol);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.existsByCorreo("nuevo@perfulandia.cl")).thenReturn(false);
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));
        when(passwordUtil.generarHash("nueva123")).thenReturn("HASH_NUEVO");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.actualizarUsuario(1L, request);

        assertEquals("nuevo@perfulandia.cl", resultado.getCorreo());
        assertEquals("HASH_NUEVO", resultado.getPasswordHash());
        assertFalse(resultado.getEstado());
        assertNull(resultado.getDireccionEnvio());
        verify(usuarioRepository).save(existente);
    }

    @Test
    void actualizarUsuario_deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> usuarioService.actualizarUsuario(99L, usuarioRequestBasico())
        );

        assertTrue(exception.getMessage().contains("Usuario no encontrado"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void actualizarUsuario_deberiaLanzarExcepcionCuandoCorreoYaExiste() {
        Usuario existente = usuarioEjemplo(1L, "admin@perfulandia.cl", true, rolEjemplo(1L));
        Usuario request = usuarioRequest("Admin", "Sistema", "otro@perfulandia.cl", null, null, true, rolEjemplo(1L));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.existsByCorreo("otro@perfulandia.cl")).thenReturn(true);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> usuarioService.actualizarUsuario(1L, request)
        );

        assertTrue(exception.getMessage().contains("Ya existe"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void cambiarEstadoUsuario_deberiaCambiarEstado() {
        Usuario usuario = usuarioEjemplo(1L, "admin@perfulandia.cl", true, rolEjemplo(1L));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.cambiarEstadoUsuario(1L, false);

        assertFalse(resultado.getEstado());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void cambiarEstadoUsuario_deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> usuarioService.cambiarEstadoUsuario(99L, false)
        );

        assertTrue(exception.getMessage().contains("Usuario no encontrado"));
    }

    @Test
    void cambiarEstadoUsuario_deberiaLanzarExcepcionCuandoEstadoEsNull() {
        Usuario usuario = usuarioEjemplo(1L, "admin@perfulandia.cl", true, rolEjemplo(1L));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> usuarioService.cambiarEstadoUsuario(1L, null)
        );

        assertTrue(exception.getMessage().contains("estado"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void cambiarRolUsuario_deberiaCambiarRol() {
        Usuario usuario = usuarioEjemplo(1L, "admin@perfulandia.cl", true, rolEjemplo(1L));
        Rol nuevoRol = rolEjemplo(2L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(rolRepository.findById(2L)).thenReturn(Optional.of(nuevoRol));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.cambiarRolUsuario(1L, 2L);

        assertEquals(2L, resultado.getRol().getIdRol());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void cambiarRolUsuario_deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> usuarioService.cambiarRolUsuario(99L, 1L)
        );

        assertTrue(exception.getMessage().contains("Usuario no encontrado"));
        verify(rolRepository, never()).findById(anyLong());
    }

    @Test
    void cambiarRolUsuario_deberiaLanzarExcepcionCuandoRolNoExiste() {
        Usuario usuario = usuarioEjemplo(1L, "admin@perfulandia.cl", true, rolEjemplo(1L));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> usuarioService.cambiarRolUsuario(1L, 99L)
        );

        assertTrue(exception.getMessage().contains("Rol no encontrado"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void cambiarPassword_deberiaCambiarPasswordCuandoActualEsCorrecta() {
        Usuario usuario = usuarioEjemplo(1L, "admin@perfulandia.cl", true, rolEjemplo(1L));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordUtil.verificarPassword("admin123", "HASH_GUARDADO")).thenReturn(true);
        when(passwordUtil.generarHash("admin456")).thenReturn("HASH_NUEVO");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.cambiarPassword(1L, "admin123", "admin456");

        assertEquals("HASH_NUEVO", resultado.getPasswordHash());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void cambiarPassword_deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> usuarioService.cambiarPassword(99L, "admin123", "admin456")
        );

        assertTrue(exception.getMessage().contains("Usuario no encontrado"));
    }

    @Test
    void cambiarPassword_deberiaLanzarExcepcionCuandoUsuarioEstaInactivo() {
        Usuario usuario = usuarioEjemplo(1L, "admin@perfulandia.cl", false, rolEjemplo(1L));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> usuarioService.cambiarPassword(1L, "admin123", "admin456")
        );

        assertTrue(exception.getMessage().contains("usuario inactivo"));
        verify(passwordUtil, never()).verificarPassword(anyString(), anyString());
    }

    @Test
    void cambiarPassword_deberiaLanzarExcepcionCuandoPasswordActualEsIncorrecta() {
        Usuario usuario = usuarioEjemplo(1L, "admin@perfulandia.cl", true, rolEjemplo(1L));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordUtil.verificarPassword("incorrecta", "HASH_GUARDADO")).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> usuarioService.cambiarPassword(1L, "incorrecta", "admin456")
        );

        assertTrue(exception.getMessage().toLowerCase().contains("actual"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void eliminarUsuario_deberiaDesactivarUsuario() {
        Usuario usuario = usuarioEjemplo(1L, "admin@perfulandia.cl", true, rolEjemplo(1L));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.eliminarUsuario(1L);

        assertFalse(usuario.getEstado());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void eliminarUsuario_deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> usuarioService.eliminarUsuario(99L)
        );

        assertTrue(exception.getMessage().contains("Usuario no encontrado"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void validarLogin_deberiaRetornarVacioCuandoCorreoEsNull() {
        Optional<Usuario> resultado = usuarioService.validarLogin(null, "admin123");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void validarLogin_deberiaRetornarVacioCuandoCorreoEstaVacio() {
        Optional<Usuario> resultado = usuarioService.validarLogin(" ", "admin123");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void validarLogin_deberiaRetornarVacioCuandoPasswordEsNull() {
        Optional<Usuario> resultado = usuarioService.validarLogin("admin@perfulandia.cl", null);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void validarLogin_deberiaRetornarVacioCuandoPasswordEstaVacio() {
        Optional<Usuario> resultado = usuarioService.validarLogin("admin@perfulandia.cl", " ");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void validarLogin_deberiaRetornarVacioCuandoUsuarioNoExiste() {
        when(usuarioRepository.findByCorreo("admin@perfulandia.cl")).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.validarLogin(" ADMIN@PERFULANDIA.CL ", "admin123");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void validarLogin_deberiaRetornarVacioCuandoPasswordNoCoincide() {
        Usuario usuario = usuarioEjemplo(1L, "admin@perfulandia.cl", true, rolEjemplo(1L));

        when(usuarioRepository.findByCorreo("admin@perfulandia.cl")).thenReturn(Optional.of(usuario));
        when(passwordUtil.verificarPassword("incorrecta", "HASH_GUARDADO")).thenReturn(false);

        Optional<Usuario> resultado = usuarioService.validarLogin("admin@perfulandia.cl", "incorrecta");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void validarLogin_deberiaRetornarVacioCuandoUsuarioEstaInactivo() {
        Usuario usuario = usuarioEjemplo(1L, "admin@perfulandia.cl", false, rolEjemplo(1L));

        when(usuarioRepository.findByCorreo("admin@perfulandia.cl")).thenReturn(Optional.of(usuario));
        when(passwordUtil.verificarPassword("admin123", "HASH_GUARDADO")).thenReturn(true);

        Optional<Usuario> resultado = usuarioService.validarLogin("admin@perfulandia.cl", "admin123");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void validarLogin_deberiaRetornarUsuarioCuandoCredencialesSonCorrectas() {
        Usuario usuario = usuarioEjemplo(1L, "admin@perfulandia.cl", true, rolEjemplo(1L));

        when(usuarioRepository.findByCorreo("admin@perfulandia.cl")).thenReturn(Optional.of(usuario));
        when(passwordUtil.verificarPassword("admin123", "HASH_GUARDADO")).thenReturn(true);

        Optional<Usuario> resultado = usuarioService.validarLogin("admin@perfulandia.cl", "admin123");

        assertTrue(resultado.isPresent());
        assertEquals("admin@perfulandia.cl", resultado.get().getCorreo());
    }

    @Test
    void validarCredencialesParaAuth_deberiaRetornarDatosConRolYPermisos() {
        Permiso permisoCrear = new Permiso(1L, "CREAR_USUARIO", "Crear usuarios");
        Permiso permisoVer = new Permiso(2L, "VER_USUARIOS", "Ver usuarios");

        HashSet<Permiso> permisos = new HashSet<>();
        permisos.add(permisoVer);
        permisos.add(permisoCrear);

        Rol rol = new Rol(1L, "ADMINISTRADOR", "Rol administrador", permisos);
        Usuario usuario = usuarioEjemplo(1L, "admin@perfulandia.cl", true, rol);

        when(usuarioRepository.findByCorreo("admin@perfulandia.cl")).thenReturn(Optional.of(usuario));
        when(passwordUtil.verificarPassword("admin123", "HASH_GUARDADO")).thenReturn(true);

        Optional<UsuarioAuthResponse> resultado =
                usuarioService.validarCredencialesParaAuth(" ADMIN@PERFULANDIA.CL ", "admin123");

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdUsuario());
        assertEquals("admin@perfulandia.cl", resultado.get().getCorreo());
        assertEquals("ADMINISTRADOR", resultado.get().getRol());
        assertEquals(List.of("CREAR_USUARIO", "VER_USUARIOS"), resultado.get().getPermisos());

        verify(usuarioRepository).findByCorreo("admin@perfulandia.cl");
        verify(passwordUtil).verificarPassword("admin123", "HASH_GUARDADO");
    }

    @Test
    void validarCredencialesParaAuth_deberiaRetornarSinRolYSinPermisosCuandoUsuarioNoTieneRol() {
        Usuario usuario = usuarioEjemplo(2L, "cliente@perfulandia.cl", true, null);

        when(usuarioRepository.findByCorreo("cliente@perfulandia.cl")).thenReturn(Optional.of(usuario));
        when(passwordUtil.verificarPassword("cliente123", "HASH_GUARDADO")).thenReturn(true);

        Optional<UsuarioAuthResponse> resultado =
                usuarioService.validarCredencialesParaAuth("cliente@perfulandia.cl", "cliente123");

        assertTrue(resultado.isPresent());
        assertEquals(2L, resultado.get().getIdUsuario());
        assertEquals("cliente@perfulandia.cl", resultado.get().getCorreo());
        assertEquals("SIN_ROL", resultado.get().getRol());
        assertEquals(List.of(), resultado.get().getPermisos());

        verify(usuarioRepository).findByCorreo("cliente@perfulandia.cl");
        verify(passwordUtil).verificarPassword("cliente123", "HASH_GUARDADO");
    }

    @Test
    void validarCredencialesParaAuth_deberiaRetornarPermisosVaciosCuandoRolNoTienePermisos() {
        Rol rol = mock(Rol.class);
        when(rol.getNombreRol()).thenReturn("CLIENTE");
        when(rol.getPermisos()).thenReturn(null);

        Usuario usuario = usuarioEjemplo(3L, "cliente2@perfulandia.cl", true, rol);

        when(usuarioRepository.findByCorreo("cliente2@perfulandia.cl")).thenReturn(Optional.of(usuario));
        when(passwordUtil.verificarPassword("cliente123", "HASH_GUARDADO")).thenReturn(true);

        Optional<UsuarioAuthResponse> resultado =
                usuarioService.validarCredencialesParaAuth("cliente2@perfulandia.cl", "cliente123");

        assertTrue(resultado.isPresent());
        assertEquals(3L, resultado.get().getIdUsuario());
        assertEquals("cliente2@perfulandia.cl", resultado.get().getCorreo());
        assertEquals("CLIENTE", resultado.get().getRol());
        assertEquals(List.of(), resultado.get().getPermisos());
    }

    @Test
    void validarCredencialesParaAuth_deberiaRetornarVacioCuandoCredencialesNoSonValidas() {
        when(usuarioRepository.findByCorreo("admin@perfulandia.cl")).thenReturn(Optional.empty());

        Optional<UsuarioAuthResponse> resultado =
                usuarioService.validarCredencialesParaAuth("admin@perfulandia.cl", "mala");

        assertTrue(resultado.isEmpty());
        verify(usuarioRepository).findByCorreo("admin@perfulandia.cl");
    }

    @Test
    void convertirAUsuarioResponse_deberiaConvertirUsuarioConRol() {
        Usuario usuario = usuarioEjemplo(1L, "admin@perfulandia.cl", true, rolEjemplo(1L));

        UsuarioResponse resultado = usuarioService.convertirAUsuarioResponse(usuario);

        assertEquals(1L, resultado.getIdUsuario());
        assertEquals("admin@perfulandia.cl", resultado.getCorreo());
        assertEquals(1L, resultado.getIdRol());
        assertEquals("ADMINISTRADOR", resultado.getNombreRol());
    }

    @Test
    void convertirAUsuarioResponse_deberiaConvertirUsuarioSinRol() {
        Usuario usuario = usuarioEjemplo(1L, "admin@perfulandia.cl", true, null);

        UsuarioResponse resultado = usuarioService.convertirAUsuarioResponse(usuario);

        assertEquals(1L, resultado.getIdUsuario());
        assertNull(resultado.getIdRol());
        assertNull(resultado.getNombreRol());
    }

    private Usuario usuarioRequestBasico() {
        return usuarioRequest(
                "Admin",
                "Sistema",
                "admin@perfulandia.cl",
                "123456",
                "Santiago",
                true,
                rolEjemplo(1L)
        );
    }

    private Usuario usuarioRequest(String nombre, String apellido, String correo, String password,
                                   String direccion, Boolean estado, Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setCorreo(correo);
        usuario.setPasswordHash(password);
        usuario.setDireccionEnvio(direccion);
        usuario.setEstado(estado);
        usuario.setRol(rol);
        return usuario;
    }

    private Usuario usuarioEjemplo(Long id, String correo, Boolean estado, Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        usuario.setNombre("Admin");
        usuario.setApellido("Sistema");
        usuario.setCorreo(correo);
        usuario.setPasswordHash("HASH_GUARDADO");
        usuario.setDireccionEnvio("Santiago");
        usuario.setEstado(estado);
        usuario.setRol(rol);
        return usuario;
    }

    private Rol rolEjemplo(Long idRol) {
        return new Rol(idRol, "ADMINISTRADOR", "Rol administrador", new HashSet<>());
    }
}