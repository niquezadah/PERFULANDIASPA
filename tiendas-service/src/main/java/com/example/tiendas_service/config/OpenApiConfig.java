package com.example.tiendas_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Perfulandia - API de Tiendas",
                version = "v1",
                description = "Microservicio responsable de registrar, consultar, actualizar y eliminar las tiendas físicas de Perfulandia."
        ),
        servers = {
                @Server(
                        url = "http://localhost:8091",
                        description = "Servidor local de tiendas-service"
                )
        }
)
public class OpenApiConfig {
}