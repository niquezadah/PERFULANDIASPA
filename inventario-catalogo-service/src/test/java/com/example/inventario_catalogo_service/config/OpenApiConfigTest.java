package com.example.inventario_catalogo_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigTest {

    @Test
    void deberiaCargarConfiguracionOpenApi() {
        //given
        OpenApiConfig config = new OpenApiConfig();

        //when
        OpenAPIDefinition openApiDefinition = OpenApiConfig.class.getAnnotation(OpenAPIDefinition.class);

        //then
        assertNotNull(config);
        assertNotNull(openApiDefinition);
        assertEquals("Perfulandia - API de Inventario y Catálogo", openApiDefinition.info().title());
        assertEquals("v1", openApiDefinition.info().version());
        assertEquals("http://localhost:8092", openApiDefinition.servers()[0].url());
    }
}