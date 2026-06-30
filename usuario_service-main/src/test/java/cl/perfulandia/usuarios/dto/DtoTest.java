package cl.perfulandia.usuarios.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

class DtoTest {

    @Test
    void cambiarEstadoRequest_deberiaCubrirConstructorVacioSetterYConstructorConParametro() {
        CambiarEstadoRequest request = new CambiarEstadoRequest();
        request.setEstado(true);

        assertEquals(true, request.getEstado());

        CambiarEstadoRequest requestConConstructor = new CambiarEstadoRequest(false);

        assertEquals(false, requestConConstructor.getEstado());
    }

    @Test
    void cambiarPasswordRequest_deberiaCubrirConstructorVacioSetterYConstructorConParametros() {
        CambiarPasswordRequest request = new CambiarPasswordRequest();
        request.setPasswordActual("actual123");
        request.setPasswordNueva("nueva123");

        assertEquals("actual123", request.getPasswordActual());
        assertEquals("nueva123", request.getPasswordNueva());

        CambiarPasswordRequest requestConConstructor =
                new CambiarPasswordRequest("admin123", "admin456");

        assertEquals("admin123", requestConConstructor.getPasswordActual());
        assertEquals("admin456", requestConConstructor.getPasswordNueva());
    }

    @Test
    void cambiarRolRequest_deberiaCubrirConstructorVacioSetterYConstructorConParametro() {
        CambiarRolRequest request = new CambiarRolRequest();
        request.setIdRol(2L);

        assertEquals(2L, request.getIdRol());

        CambiarRolRequest requestConConstructor = new CambiarRolRequest(3L);

        assertEquals(3L, requestConConstructor.getIdRol());
    }

    @Test
    void loginRequest_deberiaCubrirGettersSettersYMetodosDeLombok() {
        LoginRequest request = new LoginRequest();
        request.setCorreo("admin@perfulandia.cl");
        request.setPassword("admin123");

        assertEquals("admin@perfulandia.cl", request.getCorreo());
        assertEquals("admin123", request.getPassword());
        assertNotNull(request.toString());
        assertNotNull(request.hashCode());

        LoginRequest otroRequest = new LoginRequest();
        otroRequest.setCorreo("admin@perfulandia.cl");
        otroRequest.setPassword("admin123");

        assertEquals(request, otroRequest);
    }

    @Test
    void loginResponse_deberiaCubrirConstructorVacioSetterYConstructorConParametros() {
        LoginResponse response = new LoginResponse();
        response.setIdUsuario(1L);
        response.setNombre("Admin");
        response.setCorreo("admin@perfulandia.cl");
        response.setRol("ADMINISTRADOR");
        response.setMensaje("Login correcto");

        assertEquals(1L, response.getIdUsuario());
        assertEquals("Admin", response.getNombre());
        assertEquals("admin@perfulandia.cl", response.getCorreo());
        assertEquals("ADMINISTRADOR", response.getRol());
        assertEquals("Login correcto", response.getMensaje());

        LoginResponse responseConConstructor = new LoginResponse(
                2L,
                "Cliente",
                "cliente@perfulandia.cl",
                "CLIENTE",
                "Login exitoso"
        );

        assertEquals(2L, responseConConstructor.getIdUsuario());
        assertEquals("Cliente", responseConConstructor.getNombre());
        assertEquals("cliente@perfulandia.cl", responseConConstructor.getCorreo());
        assertEquals("CLIENTE", responseConConstructor.getRol());
        assertEquals("Login exitoso", responseConConstructor.getMensaje());
    }

    @Test
    void usuarioResponse_deberiaCubrirConstructorVacioSetterYConstructorConParametros() {
        UsuarioResponse response = new UsuarioResponse();
        response.setIdUsuario(1L);
        response.setNombre("Admin");
        response.setApellido("Sistema");
        response.setCorreo("admin@perfulandia.cl");
        response.setDireccionEnvio("Casa matriz");
        response.setEstado(true);
        response.setIdRol(1L);
        response.setNombreRol("ADMINISTRADOR");

        assertEquals(1L, response.getIdUsuario());
        assertEquals("Admin", response.getNombre());
        assertEquals("Sistema", response.getApellido());
        assertEquals("admin@perfulandia.cl", response.getCorreo());
        assertEquals("Casa matriz", response.getDireccionEnvio());
        assertEquals(true, response.getEstado());
        assertEquals(1L, response.getIdRol());
        assertEquals("ADMINISTRADOR", response.getNombreRol());

        UsuarioResponse responseConConstructor = new UsuarioResponse(
                2L,
                "Cliente",
                "Final",
                "cliente@perfulandia.cl",
                "Av. Siempre Viva 123",
                false,
                2L,
                "CLIENTE"
        );

        assertEquals(2L, responseConConstructor.getIdUsuario());
        assertEquals("Cliente", responseConConstructor.getNombre());
        assertEquals("Final", responseConConstructor.getApellido());
        assertEquals("cliente@perfulandia.cl", responseConConstructor.getCorreo());
        assertEquals("Av. Siempre Viva 123", responseConConstructor.getDireccionEnvio());
        assertEquals(false, responseConConstructor.getEstado());
        assertEquals(2L, responseConConstructor.getIdRol());
        assertEquals("CLIENTE", responseConConstructor.getNombreRol());
    }
}