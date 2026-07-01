package com.perfulandia.autenticacion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI autenticacionOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PerfuLandia - Microservicio de Autenticación")
                        .description("API para login, generación de JWT, validación de token y verificación de permisos.")
                        .version("1.0.0"))
                .externalDocs(new ExternalDocumentation()
                        .description("Proyecto PerfuLandia")
                        .url("https://github.com/Clau3333/autenticacion_service"));
    }
}