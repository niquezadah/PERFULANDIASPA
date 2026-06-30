package cl.perfulandia.usuarios.controller;

import java.util.HashSet;
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
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import cl.perfulandia.usuarios.dto.LoginRequest;
import cl.perfulandia.usuarios.dto.LoginResponse;
import cl.perfulandia.usuarios.dto.UsuarioAuthResponse;
import cl.perfulandia.usuarios.model.Rol;
import cl.perfulandia.usuarios.model.Usuario;
import cl.perfulandia.usuarios.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_deberiaRetornarOkCuandoCredencialesSonCorrectas() {
        LoginRequest request = crearLoginRequest("admin@perfulandia.cl", "admin123");
        Usuario usuario = crearUsuarioConRol();

        when(usuarioService.validarLogin("admin@perfulandia.cl", "admin123"))
                .thenReturn(Optional.of(usuario));

        ResponseEntity<LoginResponse> respuesta = authController.login(request);

        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals(1L, respuesta.getBody().getIdUsuario());
        assertEquals("Admin", respuesta.getBody().getNombre());
        assertEquals("admin@perfulandia.cl", respuesta.getBody().getCorreo());
        assertEquals("ADMINISTRADOR", respuesta.getBody().getRol());
        assertEquals("Inicio de sesión correcto", respuesta.getBody().getMensaje());

        verify(usuarioService).validarLogin("admin@perfulandia.cl", "admin123");
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    void login_deberiaRetornarOkConSinRolCuandoUsuarioNoTieneRol() {
        LoginRequest request = crearLoginRequest("cliente@perfulandia.cl", "cliente123");
        Usuario usuario = crearUsuarioSinRol();

        when(usuarioService.validarLogin("cliente@perfulandia.cl", "cliente123"))
                .thenReturn(Optional.of(usuario));

        ResponseEntity<LoginResponse> respuesta = authController.login(request);

        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals(2L, respuesta.getBody().getIdUsuario());
        assertEquals("Cliente", respuesta.getBody().getNombre());
        assertEquals("cliente@perfulandia.cl", respuesta.getBody().getCorreo());
        assertEquals("SIN_ROL", respuesta.getBody().getRol());
        assertEquals("Inicio de sesión correcto", respuesta.getBody().getMensaje());

        verify(usuarioService).validarLogin("cliente@perfulandia.cl", "cliente123");
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    void login_deberiaRetornarUnauthorizedCuandoCredencialesSonIncorrectas() {
        LoginRequest request = crearLoginRequest("admin@perfulandia.cl", "mala");

        when(usuarioService.validarLogin("admin@perfulandia.cl", "mala"))
                .thenReturn(Optional.empty());

        ResponseEntity<LoginResponse> respuesta = authController.login(request);

        assertEquals(401, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertNull(respuesta.getBody().getIdUsuario());
        assertNull(respuesta.getBody().getNombre());
        assertNull(respuesta.getBody().getCorreo());
        assertNull(respuesta.getBody().getRol());
        assertEquals("Credenciales incorrectas o usuario inactivo", respuesta.getBody().getMensaje());

        verify(usuarioService).validarLogin("admin@perfulandia.cl", "mala");
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    void validarCredenciales_deberiaRetornarOkCuandoCredencialesSonCorrectas() {
        LoginRequest request = crearLoginRequest("admin@perfulandia.cl", "admin123");

        UsuarioAuthResponse response = new UsuarioAuthResponse(
                1L,
                "admin@perfulandia.cl",
                "ADMINISTRADOR",
                List.of("CREAR_USUARIO", "VER_USUARIOS")
        );

        when(usuarioService.validarCredencialesParaAuth("admin@perfulandia.cl", "admin123"))
                .thenReturn(Optional.of(response));

        ResponseEntity<UsuarioAuthResponse> respuesta = authController.validarCredenciales(request);

        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals(1L, respuesta.getBody().getIdUsuario());
        assertEquals("admin@perfulandia.cl", respuesta.getBody().getCorreo());
        assertEquals("ADMINISTRADOR", respuesta.getBody().getRol());
        assertEquals(List.of("CREAR_USUARIO", "VER_USUARIOS"), respuesta.getBody().getPermisos());

        verify(usuarioService).validarCredencialesParaAuth("admin@perfulandia.cl", "admin123");
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    void validarCredenciales_deberiaRetornarUnauthorizedCuandoCredencialesSonIncorrectas() {
        LoginRequest request = crearLoginRequest("admin@perfulandia.cl", "mala");

        when(usuarioService.validarCredencialesParaAuth("admin@perfulandia.cl", "mala"))
                .thenReturn(Optional.empty());

        ResponseEntity<UsuarioAuthResponse> respuesta = authController.validarCredenciales(request);

        assertEquals(401, respuesta.getStatusCode().value());
        assertNull(respuesta.getBody());

        verify(usuarioService).validarCredencialesParaAuth("admin@perfulandia.cl", "mala");
        verifyNoMoreInteractions(usuarioService);
    }

    private LoginRequest crearLoginRequest(String correo, String password) {
        LoginRequest request = new LoginRequest();
        request.setCorreo(correo);
        request.setPassword(password);
        return request;
    }

    private Usuario crearUsuarioConRol() {
        Rol rol = new Rol();
        rol.setIdRol(1L);
        rol.setNombreRol("ADMINISTRADOR");
        rol.setDescripcion("Rol administrador");
        rol.setPermisos(new HashSet<>());

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNombre("Admin");
        usuario.setApellido("Sistema");
        usuario.setCorreo("admin@perfulandia.cl");
        usuario.setPasswordHash("HASH");
        usuario.setEstado(true);
        usuario.setRol(rol);

        return usuario;
    }

    private Usuario crearUsuarioSinRol() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(2L);
        usuario.setNombre("Cliente");
        usuario.setApellido("Web");
        usuario.setCorreo("cliente@perfulandia.cl");
        usuario.setPasswordHash("HASH");
        usuario.setEstado(true);
        usuario.setRol(null);

        return usuario;
    }
}