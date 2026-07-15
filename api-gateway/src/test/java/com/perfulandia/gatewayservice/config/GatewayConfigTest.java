package com.perfulandia.gatewayservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "server.port=0")
class GatewayConfigTest {

    @Autowired
    private RouteLocator routeLocator;

    @Test
    void deberiaCargarTodasLasRutasDelGateway() {
        StepVerifier.create(
                routeLocator.getRoutes()
                        .map(route -> route.getId())
                        .collectList()
        )
        .assertNext(rutas -> {
            assertTrue(rutas.contains("soporte-service"));
            assertTrue(rutas.contains("pedido-service"));
            assertTrue(rutas.contains("tiendas-service"));
            assertTrue(rutas.contains("inventario-catalogo-service"));
            assertTrue(rutas.contains("resena-service"));
            assertTrue(rutas.contains("autenticacion-service"));
            assertTrue(rutas.contains("carrito-service"));
            assertTrue(rutas.contains("ventas-facturacion-service"));
            assertTrue(rutas.contains("usuario-service"));
            assertTrue(rutas.contains("logistica-service"));

            assertTrue(rutas.contains("openapi-autenticacion"));
            assertTrue(rutas.contains("openapi-usuarios"));
            assertTrue(rutas.contains("openapi-soporte"));
            assertTrue(rutas.contains("openapi-pedidos"));
            assertTrue(rutas.contains("openapi-tiendas"));
            assertTrue(rutas.contains("openapi-inventario"));
            assertTrue(rutas.contains("openapi-resenas"));
            assertTrue(rutas.contains("openapi-carrito"));
            assertTrue(rutas.contains("openapi-ventas"));
            assertTrue(rutas.contains("openapi-logistica"));
        })
        .verifyComplete();
    }

    @Test
    void deberiaTenerCantidadCorrectaDeRutas() {
        StepVerifier.create(
                routeLocator.getRoutes()
                        .map(route -> route.getId())
                        .collectList()
        )
        .assertNext(rutas -> assertEquals(21, rutas.size()))
        .verifyComplete();
    }

    @Test
    void deberiaCargarRutaSoporteService() {
        verificarRutaExiste("soporte-service");
    }

    @Test
    void deberiaCargarRutaPedidoService() {
        verificarRutaExiste("pedido-service");
    }

    @Test
    void deberiaCargarRutaTiendasService() {
        verificarRutaExiste("tiendas-service");
    }

    @Test
    void deberiaCargarRutaInventarioCatalogoService() {
        verificarRutaExiste("inventario-catalogo-service");
    }

    @Test
    void deberiaCargarRutaResenaService() {
        verificarRutaExiste("resena-service");
    }

    @Test
    void deberiaCargarRutaAutenticacionService() {
        verificarRutaExiste("autenticacion-service");
    }

    @Test
    void deberiaCargarRutaCarritoService() {
        verificarRutaExiste("carrito-service");
    }

    @Test
    void deberiaCargarRutaVentasFacturacionService() {
        verificarRutaExiste("ventas-facturacion-service");
    }

    @Test
    void deberiaCargarRutaUsuarioService() {
        verificarRutaExiste("usuario-service");
    }

    @Test
    void deberiaCargarRutaLogisticaService() {
        verificarRutaExiste("logistica-service");
    }

    @Test
    void noDeberiaExistirRutaInvalida() {
        StepVerifier.create(
                routeLocator.getRoutes()
                        .map(route -> route.getId())
                        .collectList()
        )
        .assertNext(rutas -> assertFalse(rutas.contains("servicio-inexistente")))
        .verifyComplete();
    }

    private void verificarRutaExiste(String idRuta) {
        StepVerifier.create(
                routeLocator.getRoutes()
                        .filter(route -> route.getId().equals(idRuta))
                        .collectList()
        )
        .assertNext(rutas -> {
            assertEquals(1, rutas.size());
            assertEquals(idRuta, rutas.get(0).getId());
        })
        .verifyComplete();
    }
}