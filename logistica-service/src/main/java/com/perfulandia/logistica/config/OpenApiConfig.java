package com.perfulandia.logistica.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Perfulandia - API de Logística",
                version = "v1",
                description = """
                        Microservicio encargado de gestionar los envíos,
                        seguimiento de pedidos, transportistas y entregas
                        de Perfulandia SPA.
                        """,
                contact = @Contact(
                        name = "Equipo Perfulandia",
                        email = "soporte@perfulandia.cl"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8090",
                        description = "API Gateway central de Perfulandia"
                )
        }
)
public class OpenApiConfig {
}