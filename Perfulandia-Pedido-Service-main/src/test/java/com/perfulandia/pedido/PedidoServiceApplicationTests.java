package com.perfulandia.pedido;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.mockStatic;

@SpringBootTest
@ActiveProfiles("test")
class PedidoServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void main_deberiaEjecutarSpringApplicationRun() {
        String[] args = {};

        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
            PedidoServiceApplication.main(args);

            springApplicationMock.verify(() ->
                    SpringApplication.run(PedidoServiceApplication.class, args)
            );
        }
    }
}