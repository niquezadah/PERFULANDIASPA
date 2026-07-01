package com.perfulandia.pedido.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "PerfuLandia - Pedido Service",
                version = "1.0.0",
                description = "Documentación OpenAPI del microservicio de pedidos. Este servicio forma parte de la API general de PerfuLandia y permite crear, consultar, actualizar, cancelar y filtrar pedidos.",
                contact = @Contact(
                        name = "Equipo PerfuLandia",
                        email = "soporte@perfulandia.cl"
                ),
                license = @License(
                        name = "Uso académico",
                        url = "https://perfulandia.cl"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8071",
                        description = "Servidor local de pedido-service"
                )
        }
)
public class OpenApiConfig {
}