package com.example.tiendas_service;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

class TiendasServiceApplicationTest {

    @Test
    void main_deberiaEjecutarSpringApplicationRun() {
        //given
        String[] args = {};

        try (MockedStatic<SpringApplication> springApplicationMock = Mockito.mockStatic(SpringApplication.class)) {

            //when
            TiendasServiceApplication.main(args);

            //then
            springApplicationMock.verify(
                    () -> SpringApplication.run(TiendasServiceApplication.class, args)
            );
        }
    }
}
