package com.example.ventas_facturacion_service;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
class VentasFacturacionServiceApplicationTest {

    @Test
    void contextLoads() {
        //given

        //when //then
    }

    @Test
    void main_deberiaEjecutarSpringApplicationRun() {
        //given
        String[] args = {};

        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {

            //when
            VentasFacturacionServiceApplication.main(args);

            //then
            springApplicationMock.verify(() ->
                    SpringApplication.run(VentasFacturacionServiceApplication.class, args)
            );
        }
    }

    @Test
    void main_noDeberiaLanzarExcepcion() {
        //given
        String[] args = {};

        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {

            //when //then
            assertDoesNotThrow(() -> VentasFacturacionServiceApplication.main(args));
        }
    }
    
}