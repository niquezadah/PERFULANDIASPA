package cl.perfulandia.usuarios.controller;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import cl.perfulandia.usuarios.dto.CambiarEstadoRequest;
import cl.perfulandia.usuarios.dto.CambiarPasswordRequest;
import cl.perfulandia.usuarios.dto.CambiarRolRequest;
import cl.perfulandia.usuarios.dto.UsuarioResponse;
import cl.perfulandia.usuarios.model.Rol;
import cl.perfulandia.usuarios.model.Usuario;
import cl.perfulandia.usuarios.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @Test
    void listarUsuarios_deberiaRetornarCollectionModelConUsuariosYLinks() {
        Usuario usuario = crearUsuario(1L, true, 1L, "ADMINISTRADOR");
        UsuarioResponse response = crearUsuarioResponse(1L, true, 1L, "ADMINISTRADOR");

        when(usuarioService.listarUsuarios()).thenReturn(List.of(usuario));
        when(usuarioService.convertirAUsuarioResponse(usuario)).thenReturn(response);

        CollectionModel<EntityModel<UsuarioResponse>> resultado = usuarioController.listarUsuarios();

        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertEquals("self", resultado.getRequiredLink("self").getRel().value());

        verify(usuarioService).listarUsuarios();
        verify(usuarioService).convertirAUsuarioResponse(usuario);
    }

    @Test
    void listarUsuariosPorEstado_deberiaRetornarCollectionModelConUsuariosYLinks() {
        Usuario usuario = crearUsuario(1L, true, 1L, "ADMINISTRADOR");
        UsuarioResponse response = crearUsuarioResponse(1L, true, 1L, "ADMINISTRADOR");

        when(usuarioService.listarUsuariosPorEstado(true)).thenReturn(List.of(usuario));
        when(usuarioService.convertirAUsuarioResponse(usuario)).thenReturn(response);

        CollectionModel<EntityModel<UsuarioResponse>> resultado = usuarioController.listarUsuariosPorEstado(true);

        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertEquals("self", resultado.getRequiredLink("self").getRel().value());
        assertEquals("usuarios", resultado.getRequiredLink("usuarios").getRel().value());

        verify(usuarioService).listarUsuariosPorEstado(true);
        verify(usuarioService).convertirAUsuarioResponse(usuario);
    }

    @Test
    void buscarUsuarioPorId_deberiaRetornarOkCuandoExiste() {
        Usuario usuario = crearUsuario(1L, true, 1L, "ADMINISTRADOR");
        UsuarioResponse response = crearUsuarioResponse(1L, true, 1L, "ADMINISTRADOR");

        when(usuarioService.buscarUsuarioPorId(1L)).thenReturn(Optional.of(usuario));
        when(usuarioService.convertirAUsuarioResponse(usuario)).thenReturn(response);

        ResponseEntity<EntityModel<UsuarioResponse>> resultado = usuarioController.buscarUsuarioPorId(1L);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertNotNull(resultado.getBody());
        assertNotNull(resultado.getBody().getContent());
        assertEquals(1L, resultado.getBody().getContent().getIdUsuario());
        assertEquals("Admin", resultado.getBody().getContent().getNombre());
        assertTrue(resultado.getBody().hasLink("self"));
        assertTrue(resultado.getBody().hasLink("usuarios"));
        assertTrue(resultado.getBody().hasLink("cambiar-estado"));
        assertTrue(resultado.getBody().hasLink("cambiar-rol"));
        assertTrue(resultado.getBody().hasLink("cambiar-password"));

        verify(usuarioService).buscarUsuarioPorId(1L);
        verify(usuarioService).convertirAUsuarioResponse(usuario);
    }

    @Test
    void buscarUsuarioPorId_deberiaRetornarNotFoundCuandoNoExiste() {
        when(usuarioService.buscarUsuarioPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<EntityModel<UsuarioResponse>> resultado = usuarioController.buscarUsuarioPorId(99L);

        assertEquals(HttpStatus.NOT_FOUND, resultado.getStatusCode());

        verify(usuarioService).buscarUsuarioPorId(99L);
    }

    @Test
    void guardarUsuario_deberiaRetornarCreatedConUsuarioGuardado() {
        Usuario usuarioEntrada = crearUsuario(null, true, 1L, "ADMINISTRADOR");
        Usuario usuarioGuardado = crearUsuario(1L, true, 1L, "ADMINISTRADOR");
        UsuarioResponse response = crearUsuarioResponse(1L, true, 1L, "ADMINISTRADOR");

        when(usuarioService.guardarUsuario(usuarioEntrada)).thenReturn(usuarioGuardado);
        when(usuarioService.convertirAUsuarioResponse(usuarioGuardado)).thenReturn(response);

        ResponseEntity<EntityModel<UsuarioResponse>> resultado = usuarioController.guardarUsuario(usuarioEntrada);

        assertEquals(HttpStatus.CREATED, resultado.getStatusCode());
        assertNotNull(resultado.getBody());
        assertNotNull(resultado.getBody().getContent());
        assertEquals(1L, resultado.getBody().getContent().getIdUsuario());
        assertTrue(resultado.getBody().hasLink("self"));
        assertTrue(resultado.getBody().hasLink("usuarios"));

        verify(usuarioService).guardarUsuario(usuarioEntrada);
        verify(usuarioService).convertirAUsuarioResponse(usuarioGuardado);
    }

    @Test
    void actualizarUsuario_deberiaRetornarOkConUsuarioActualizado() {
        Usuario usuarioEntrada = crearUsuario(null, true, 1L, "ADMINISTRADOR");
        Usuario usuarioActualizado = crearUsuario(1L, true, 1L, "ADMINISTRADOR");
        UsuarioResponse response = crearUsuarioResponse(1L, true, 1L, "ADMINISTRADOR");

        when(usuarioService.actualizarUsuario(1L, usuarioEntrada)).thenReturn(usuarioActualizado);
        when(usuarioService.convertirAUsuarioResponse(usuarioActualizado)).thenReturn(response);

        ResponseEntity<EntityModel<UsuarioResponse>> resultado = usuarioController.actualizarUsuario(1L, usuarioEntrada);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertNotNull(resultado.getBody());
        assertNotNull(resultado.getBody().getContent());
        assertEquals(1L, resultado.getBody().getContent().getIdUsuario());
        assertTrue(resultado.getBody().hasLink("self"));

        verify(usuarioService).actualizarUsuario(1L, usuarioEntrada);
        verify(usuarioService).convertirAUsuarioResponse(usuarioActualizado);
    }

    @Test
    void cambiarEstadoUsuario_deberiaRetornarOkConUsuarioActualizado() {
        CambiarEstadoRequest request = new CambiarEstadoRequest();
        request.setEstado(false);

        Usuario usuarioActualizado = crearUsuario(1L, false, 1L, "ADMINISTRADOR");
        UsuarioResponse response = crearUsuarioResponse(1L, false, 1L, "ADMINISTRADOR");

        when(usuarioService.cambiarEstadoUsuario(1L, false)).thenReturn(usuarioActualizado);
        when(usuarioService.convertirAUsuarioResponse(usuarioActualizado)).thenReturn(response);

        ResponseEntity<EntityModel<UsuarioResponse>> resultado = usuarioController.cambiarEstadoUsuario(1L, request);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertNotNull(resultado.getBody());
        assertNotNull(resultado.getBody().getContent());
        assertEquals(false, resultado.getBody().getContent().getEstado());
        assertTrue(resultado.getBody().hasLink("cambiar-estado"));

        verify(usuarioService).cambiarEstadoUsuario(1L, false);
        verify(usuarioService).convertirAUsuarioResponse(usuarioActualizado);
    }

    @Test
    void cambiarRolUsuario_deberiaRetornarOkConUsuarioActualizado() {
        CambiarRolRequest request = new CambiarRolRequest();
        request.setIdRol(2L);

        Usuario usuarioActualizado = crearUsuario(1L, true, 2L, "CLIENTE");
        UsuarioResponse response = crearUsuarioResponse(1L, true, 2L, "CLIENTE");

        when(usuarioService.cambiarRolUsuario(1L, 2L)).thenReturn(usuarioActualizado);
        when(usuarioService.convertirAUsuarioResponse(usuarioActualizado)).thenReturn(response);

        ResponseEntity<EntityModel<UsuarioResponse>> resultado = usuarioController.cambiarRolUsuario(1L, request);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertNotNull(resultado.getBody());
        assertNotNull(resultado.getBody().getContent());
        assertEquals(2L, resultado.getBody().getContent().getIdRol());
        assertEquals("CLIENTE", resultado.getBody().getContent().getNombreRol());
        assertTrue(resultado.getBody().hasLink("cambiar-rol"));

        verify(usuarioService).cambiarRolUsuario(1L, 2L);
        verify(usuarioService).convertirAUsuarioResponse(usuarioActualizado);
    }

    @Test
    void cambiarPassword_deberiaRetornarOkConUsuarioActualizado() {
        CambiarPasswordRequest request = new CambiarPasswordRequest();
        request.setPasswordActual("admin123");
        request.setPasswordNueva("admin456");

        Usuario usuarioActualizado = crearUsuario(1L, true, 1L, "ADMINISTRADOR");
        UsuarioResponse response = crearUsuarioResponse(1L, true, 1L, "ADMINISTRADOR");

        when(usuarioService.cambiarPassword(1L, "admin123", "admin456")).thenReturn(usuarioActualizado);
        when(usuarioService.convertirAUsuarioResponse(usuarioActualizado)).thenReturn(response);

        ResponseEntity<EntityModel<UsuarioResponse>> resultado = usuarioController.cambiarPassword(1L, request);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertNotNull(resultado.getBody());
        assertNotNull(resultado.getBody().getContent());
        assertEquals(1L, resultado.getBody().getContent().getIdUsuario());
        assertTrue(resultado.getBody().hasLink("cambiar-password"));

        verify(usuarioService).cambiarPassword(1L, "admin123", "admin456");
        verify(usuarioService).convertirAUsuarioResponse(usuarioActualizado);
    }

    @Test
    void eliminarUsuario_deberiaRetornarNoContent() {
        ResponseEntity<Void> resultado = usuarioController.eliminarUsuario(1L);

        assertEquals(HttpStatus.NO_CONTENT, resultado.getStatusCode());

        verify(usuarioService).eliminarUsuario(1L);
    }

    private Usuario crearUsuario(Long id, Boolean estado, Long idRol, String nombreRol) {
        Rol rol = new Rol();
        rol.setIdRol(idRol);
        rol.setNombreRol(nombreRol);

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        usuario.setNombre("Admin");
        usuario.setApellido("Sistema");
        usuario.setCorreo("admin@perfulandia.cl");
        usuario.setDireccionEnvio("Casa matriz");
        usuario.setEstado(estado);
        usuario.setRol(rol);
        return usuario;
    }

    private UsuarioResponse crearUsuarioResponse(Long id, Boolean estado, Long idRol, String nombreRol) {
        return new UsuarioResponse(
                id,
                "Admin",
                "Sistema",
                "admin@perfulandia.cl",
                "Casa matriz",
                estado,
                idRol,
                nombreRol
        );
    }
}