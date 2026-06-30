package com.example.carrito_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Perfulandia - API de Carrito",
                version = "v1",
                description = "Microservicio responsable de registrar, consultar, actualizar y eliminar productos del carrito de compra de Perfulandia."
        ),
        servers = {
                @Server(
                        url = "http://localhost:8094",
                        description = "Servidor local de carrito-service"
                )
        }
)
public class OpenApiConfig {
}
