package com.example.inventario_catalogo_service;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

class InventarioCatalogoServiceApplicationTest {

    @Test
    void main_deberiaEjecutarSpringApplicationRun() {
        //given
        String[] args = {};

        try (MockedStatic<SpringApplication> springApplicationMock = Mockito.mockStatic(SpringApplication.class)) {
            //when
            InventarioCatalogoServiceApplication.main(args);

            //then
            springApplicationMock.verify(
                    () -> SpringApplication.run(InventarioCatalogoServiceApplication.class, args)
            );
        }
    }
}
