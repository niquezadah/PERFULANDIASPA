package com.perfulandia.logistica;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:logistica_test;DB_CLOSE_DELAY=-1;MODE=MySQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "pedido.service.url=http://localhost:8071/api/pedidos",
        "usuario.service.url=http://localhost:8082/api/usuarios"
})
public class LogisticaServiceApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void main_deberiaEjecutarseSinLanzarExcepcion() {
        assertDoesNotThrow(() ->
                LogisticaServiceApplication.main(new String[] {
                        "--spring.main.web-application-type=none",
                        "--spring.datasource.url=jdbc:h2:mem:logistica_main_test;DB_CLOSE_DELAY=-1;MODE=MySQL",
                        "--spring.datasource.driver-class-name=org.h2.Driver",
                        "--spring.datasource.username=sa",
                        "--spring.datasource.password=",
                        "--spring.jpa.hibernate.ddl-auto=create-drop",
                        "--pedido.service.url=http://localhost:8071/api/pedidos",
                        "--usuario.service.url=http://localhost:8082/api/usuarios"
                })
        );
    }
}