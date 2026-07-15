package com.perfulandia.gatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator rutas(RouteLocatorBuilder builder) {
        return builder.routes()

                // =====================================================
                // RUTAS DE NEGOCIO
                // =====================================================

                // SOPORTE SERVICE
                // Ruta utilizada por Postman:
                // /api/soporte/tickets -> /api/tickets
                .route("soporte-service", r -> r
                        .path("/api/soporte/**")
                        .filters(f -> f.rewritePath(
                                "/api/soporte/(?<segment>.*)",
                                "/api/${segment}"
                        ))
                        .uri("http://localhost:8070"))

                // Alias necesario para ejecutar soporte desde Swagger.
                // El controlador real de soporte utiliza /api/tickets.
                .route("soporte-service-swagger-alias", r -> r
                        .path("/api/tickets", "/api/tickets/**")
                        .uri("http://localhost:8070"))

                // PEDIDO SERVICE
                .route("pedido-service", r -> r
                        .path("/api/pedidos", "/api/pedidos/**")
                        .uri("http://localhost:8071"))

                // TIENDAS SERVICE
                .route("tiendas-service", r -> r
                        .path("/api/v1/tiendas", "/api/v1/tiendas/**")
                        .uri("http://localhost:8091"))

                // INVENTARIO / CATÁLOGO SERVICE
                .route("inventario-catalogo-service", r -> r
                        .path(
                                "/api/v1/productos",
                                "/api/v1/productos/**",
                                "/api/v1/categorias",
                                "/api/v1/categorias/**",
                                "/api/v1/inventarios",
                                "/api/v1/inventarios/**"
                        )
                        .uri("http://localhost:8092"))

                // RESEÑAS SERVICE
                .route("resena-service", r -> r
                        .path("/api/v1/resenas", "/api/v1/resenas/**")
                        .uri("http://localhost:8093"))

                // AUTENTICACIÓN SERVICE
                .route("autenticacion-service", r -> r
                        .path("/api/auth", "/api/auth/**")
                        .uri("http://localhost:8081"))

                // CARRITO SERVICE
                .route("carrito-service", r -> r
                        .path("/api/v1/carrito", "/api/v1/carrito/**")
                        .uri("http://localhost:8094"))

                // VENTAS Y FACTURACIÓN SERVICE
                .route("ventas-facturacion-service", r -> r
                        .path(
                                "/api/v1/ventas-facturacion",
                                "/api/v1/ventas-facturacion/**"
                        )
                        .uri("http://localhost:8095"))

                // USUARIO SERVICE
                .route("usuario-service", r -> r
                        .path(
                                "/api/usuarios",
                                "/api/usuarios/**",
                                "/api/roles",
                                "/api/roles/**",
                                "/api/permisos",
                                "/api/permisos/**"
                        )
                        .uri("http://localhost:8082"))

                // LOGÍSTICA SERVICE
                .route("logistica-service", r -> r
                        .path(
                                "/api/v1/logistica",
                                "/api/v1/logistica/**"
                        )
                        .uri("http://localhost:8096"))


                // =====================================================
                // DOCUMENTACIÓN OPENAPI DE LOS MICROSERVICIOS
                // =====================================================

                // AUTENTICACIÓN OPENAPI
                .route("openapi-autenticacion", r -> r
                        .path("/openapi/autenticacion")
                        .filters(f -> f.setPath("/v3/api-docs"))
                        .uri("http://localhost:8081"))

                // USUARIOS, ROLES Y PERMISOS OPENAPI
                .route("openapi-usuarios", r -> r
                        .path("/openapi/usuarios")
                        .filters(f -> f.setPath("/v3/api-docs"))
                        .uri("http://localhost:8082"))

                // SOPORTE OPENAPI
                .route("openapi-soporte", r -> r
                        .path("/openapi/soporte")
                        .filters(f -> f.setPath("/v3/api-docs"))
                        .uri("http://localhost:8070"))

                // PEDIDOS OPENAPI
                .route("openapi-pedidos", r -> r
                        .path("/openapi/pedidos")
                        .filters(f -> f.setPath("/v3/api-docs"))
                        .uri("http://localhost:8071"))

                // TIENDAS OPENAPI
                .route("openapi-tiendas", r -> r
                        .path("/openapi/tiendas")
                        .filters(f -> f.setPath("/v3/api-docs"))
                        .uri("http://localhost:8091"))

                // INVENTARIO OPENAPI
                .route("openapi-inventario", r -> r
                        .path("/openapi/inventario")
                        .filters(f -> f.setPath("/v3/api-docs"))
                        .uri("http://localhost:8092"))

                // RESEÑAS OPENAPI
                .route("openapi-resenas", r -> r
                        .path("/openapi/resenas")
                        .filters(f -> f.setPath("/v3/api-docs"))
                        .uri("http://localhost:8093"))

                // CARRITO OPENAPI
                .route("openapi-carrito", r -> r
                        .path("/openapi/carrito")
                        .filters(f -> f.setPath("/v3/api-docs"))
                        .uri("http://localhost:8094"))

                // VENTAS OPENAPI
                .route("openapi-ventas", r -> r
                        .path("/openapi/ventas")
                        .filters(f -> f.setPath("/v3/api-docs"))
                        .uri("http://localhost:8095"))

                // LOGÍSTICA OPENAPI
                // Este servicio configuró su documentación en /api-docs.
                .route("openapi-logistica", r -> r
                        .path("/openapi/logistica")
                        .filters(f -> f.setPath("/api-docs"))
                        .uri("http://localhost:8096"))

                .build();
    }
}