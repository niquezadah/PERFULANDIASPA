package com.example.inventario_catalogo_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Perfulandia - API de Inventario y Catálogo",
                version = "v1",
                description = "Microservicio responsable de registrar, consultar, actualizar, eliminar y filtrar productos del catálogo de Perfulandia."
        ),
        servers = {
                @Server(
                        url = "http://localhost:8092",
                        description = "Servidor local de inventario-catalogo-service"
                )
        }
)
public class OpenApiConfig {
}