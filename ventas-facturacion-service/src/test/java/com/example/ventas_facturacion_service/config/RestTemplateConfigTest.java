package com.example.ventas_facturacion_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RestTemplateConfigTest {

    @Test
    void restTemplate_deberiaRetornarInstanciaDeRestTemplate() {
        //given
        RestTemplateConfig config = new RestTemplateConfig();

        //when
        RestTemplate resultado = config.restTemplate();

        //then
        assertNotNull(resultado);
        assertInstanceOf(RestTemplate.class, resultado);
    }
}
