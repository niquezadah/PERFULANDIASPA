package com.perfulandia.autenticacion.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.SecurityFilterChain;

import com.perfulandia.autenticacion.AutenticacionApplication;

import io.swagger.v3.oas.models.OpenAPI;

@SpringBootTest(classes = AutenticacionApplication.class)
class ConfigCoverageTest {

    @Autowired
    private OpenAPI openAPI;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Test
    void openApi_deberiaCargarInformacionDelProyecto() {
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());

        assertEquals(
                "PerfuLandia - Microservicio de Autenticación",
                openAPI.getInfo().getTitle()
        );

        assertEquals(
                "API para login, generación de JWT, validación de token y verificación de permisos.",
                openAPI.getInfo().getDescription()
        );

        assertEquals("1.0.0", openAPI.getInfo().getVersion());
        assertNotNull(openAPI.getExternalDocs());
        assertEquals("Proyecto PerfuLandia", openAPI.getExternalDocs().getDescription());
    }

    @Test
    void securityFilterChain_deberiaCargarCorrectamente() {
        assertNotNull(securityFilterChain);
    }
}