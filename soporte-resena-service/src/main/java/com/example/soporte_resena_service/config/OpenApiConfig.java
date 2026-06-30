package com.example.soporte_resena_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Perfulandia - API de Soporte y Reseñas",
                version = "v1",
                description = "Microservicio responsable de registrar, consultar, actualizar, eliminar y filtrar reseñas de productos de Perfulandia."
        ),
        servers = {
                @Server(
                        url = "http://localhost:8093",
                        description = "Servidor local de soporte-resena-service"
                )
        }
)
public class OpenApiConfig {
}