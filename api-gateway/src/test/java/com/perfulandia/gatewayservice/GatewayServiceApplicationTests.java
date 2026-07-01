package com.perfulandia.gatewayservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(properties = "server.port=0")
class GatewayServiceApplicationTests {

    @Test
    void contextLoads() {
        // Contexto cargado correctamente
    }

    @Test
    void main_deberiaIniciarLaAplicacionSinLanzarExcepcion() {
        assertDoesNotThrow(() ->
                GatewayServiceApplication.main(new String[] {
                        "--server.port=0"
                })
        );
    }
}