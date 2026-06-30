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
                // SOPORTE SERVICE
               .route("soporte-service", r -> r
                    .path("/api/soporte/**")
                    .filters(f -> f.rewritePath("/api/soporte/(?<segment>.*)", "/api/${segment}"))
                    .uri("http://localhost:8070"))



                // PEDIDO SERVICE
                .route("pedido-service", r -> r
                    .path("/api/pedidos", "/api/pedidos/**")
                    .uri("http://localhost:8071"))

                // TIENDAS SERVICE
                .route("tiendas-service", r -> r
                        .path("/api/v1/tiendas", "/api/v1/tiendas/**")
                        .uri("http://localhost:8091"))



                // INVENTARIO / CATALOGO SERVICE
                .route("inventario-catalogo-service", r -> r
                        .path(
                                "/api/v1/productos", "/api/v1/productos/**",
                                "/api/v1/categorias", "/api/v1/categorias/**",
                                "/api/v1/inventarios", "/api/v1/inventarios/**"
                        )
                        .uri("http://localhost:8092"))


                // RESEÑA SERVICE
                .route("resena-service", r -> r
                        .path("/api/v1/resenas", "/api/v1/resenas/**")
                        .uri("http://localhost:8093"))


                // AUTENTICACION SERVICE
                .route("autenticacion-service", r -> r
                        .path("/api/auth", "/api/auth/**")
                        .uri("http://localhost:8081"))


                // CARRITO SERVICE
                .route("carrito-service", r -> r
                        .path("/api/v1/carrito", "/api/v1/carrito/**")
                        .uri("http://localhost:8094"))

                // VENTAS Y FACTURACION SERVICE
                .route("ventas-facturacion-service", r -> r
                        .path("/api/v1/ventas-facturacion", "/api/v1/ventas-facturacion/**")
                        .uri("http://localhost:8095"))

                // USUARIO SERVICE
                .route("usuario-service", r -> r
                        .path(
                                "/api/usuarios", "/api/usuarios/**",
                                "/api/roles", "/api/roles/**",
                                "/api/permisos", "/api/permisos/**"
                        )
                        .uri("http://localhost:8082"))


                .build();
    }
}