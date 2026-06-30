package cl.perfulandia.usuarios;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;
import org.springframework.boot.SpringApplication;

class UsuariosApplicationTests {

    @Test
    void main_deberiaEjecutarSpringApplicationRun() {
        String[] args = new String[] {};

        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
            UsuariosApplication.main(args);

            springApplicationMock.verify(
                    () -> SpringApplication.run(UsuariosApplication.class, args)
            );
        }
    }
}