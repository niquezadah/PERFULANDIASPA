package com.example.ventas_facturacion_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Perfulandia - API de Ventas y Facturación",
                version = "v1",
                description = "Microservicio responsable de registrar ventas, generar facturas, consultar ventas y actualizar estados. Antes de registrar una venta, valida que el cliente exista y esté activo en usuario-service, y obtiene el total del carrito desde carrito-service."
        ),
        servers = {
                @Server(
                        url = "http://localhost:8090",
                        description = "Servidor local de ventas-facturacion-service"
                )
        }
)
public class OpenApiConfig {
}