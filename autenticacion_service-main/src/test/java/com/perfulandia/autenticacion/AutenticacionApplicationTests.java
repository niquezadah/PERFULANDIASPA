package com.perfulandia.autenticacion;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;
import org.springframework.boot.SpringApplication;

class AutenticacionApplicationTests {

    @Test
    void constructor_deberiaCrearInstancia() {
        assertNotNull(new AutenticacionApplication());
    }

    @Test
    void applicationClass_deberiaExistir() {
        assertNotNull(AutenticacionApplication.class);
    }

    @Test
    void main_deberiaEjecutarSpringApplicationRun() {
        String[] args = {};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            AutenticacionApplication.main(args);

            springApplication.verify(() ->
                    SpringApplication.run(AutenticacionApplication.class, args)
            );
        }
    }
}