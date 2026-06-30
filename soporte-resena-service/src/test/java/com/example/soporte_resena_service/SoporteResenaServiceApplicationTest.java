package com.example.soporte_resena_service;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

class SoporteResenaServiceApplicationTest {

    @Test
    void main_deberiaEjecutarSpringApplicationRun() {
        //given
        String[] args = {};

        try (MockedStatic<SpringApplication> springApplicationMock = Mockito.mockStatic(SpringApplication.class)) {
            //when
            SoporteResenaServiceApplication.main(args);

            //then
            springApplicationMock.verify(
                    () -> SpringApplication.run(SoporteResenaServiceApplication.class, args)
            );
        }
    }
}