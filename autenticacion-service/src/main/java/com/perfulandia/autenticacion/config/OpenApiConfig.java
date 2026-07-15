package com.perfulandia.autenticacion.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI autenticacionOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PerfuLandia - Microservicio de Autenticación")
                        .description("""
                                API encargada del inicio de sesión,
                                generación de JWT, validación de tokens
                                y verificación de permisos.
                                """)
                        .version("1.0.0"))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8090")
                                .description("API Gateway central de Perfulandia")
                ))
                .externalDocs(new ExternalDocumentation()
                        .description("Proyecto PerfuLandia")
                        .url("https://github.com/Clau3333/autenticacion_service"));
    }
}