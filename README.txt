PERFULANDIA SPA

Proyecto desarrollado para la asignatura de Desarrollo FullStack I.

Integrantes:
- Javier Ancaten
- Claudio Córdova
- Nicolás Quezada

Descripción del proyecto:
Perfulandia SPA es una empresa dedicada a la venta de productos de perfumería y cuidado personal. El sistema fue desarrollado utilizando una arquitectura de microservicios, donde cada servicio cumple una responsabilidad específica dentro del negocio.

El objetivo del proyecto es separar las funciones principales del sistema para mejorar la organización, facilitar el mantenimiento y permitir que cada microservicio tenga su propia lógica, base de datos y endpoints REST.

Tecnologías utilizadas:
- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- MySQL
- Maven
- Swagger / OpenAPI
- JUnit
- Mockito
- Postman
- GitHub

Microservicios del proyecto:

1. autenticacion-service
Puerto: 8081
Función: permite iniciar sesión, validar token y manejar la seguridad básica del sistema.
Swagger: http://localhost:8081/swagger-ui/index.html

2. usuario-service
Puerto: 8082
Función: gestiona usuarios, roles, permisos, estados de usuario y validación de credenciales.
Swagger: http://localhost:8082/swagger-ui/index.html

3. soporte-service
Puerto: 8070
Función: gestiona tickets de soporte, mensajes, estados, responsables y cierre de solicitudes.
Swagger: http://localhost:8070/swagger-ui/index.html

4. pedido-service
Puerto: 8071
Función: permite crear pedidos, consultar pedidos, buscar por usuario, tienda o estado, actualizar estados y cancelar pedidos.
Swagger: http://localhost:8071/swagger-ui/index.html

5. tiendas-service
Puerto: 8091
Función: gestiona las tiendas o sucursales de Perfulandia, incluyendo dirección, horarios, estado y políticas locales.
Swagger: http://localhost:8091/swagger-ui/index.html

6. inventario-catalogo-service
Puerto: 8092
Función: gestiona productos, stock, precios, disponibilidad, categorías y asociación con tiendas.
Swagger: http://localhost:8092/swagger-ui/index.html

7. soporte-resena-service
Puerto: 8093
Función: permite registrar, consultar, actualizar y eliminar reseñas de productos.
Swagger: http://localhost:8093/swagger-ui/index.html

8. carrito-service
Puerto: 8094
Función: permite agregar productos al carrito, actualizar cantidades, eliminar productos, consultar carritos activos y calcular el total.
Swagger: http://localhost:8094/swagger-ui/index.html

9. ventas-facturacion-service
Puerto: 8095
Función: registra ventas, genera facturas, consulta ventas por cliente o estado y actualiza el estado de una venta.
Swagger: http://localhost:8095/swagger-ui/index.html

Flujo principal del sistema:
El cliente puede consultar tiendas y productos, agregar productos al carrito, calcular el total y registrar una venta con factura. Además, puede crear pedidos, dejar reseñas de productos y generar tickets de soporte si necesita ayuda.

Relaciones principales entre servicios:
- autenticacion-service se comunica con usuario-service para validar credenciales.
- inventario-catalogo-service valida la tienda asociada consultando tiendas-service.
- soporte-resena-service valida productos consultando inventario-catalogo-service.
- carrito-service valida productos y stock consultando inventario-catalogo-service.
- ventas-facturacion-service obtiene el total del carrito consultando carrito-service.
- pedido-service registra pedidos con datos de usuario, tienda y productos.
- soporte-service permite registrar y gestionar solicitudes de ayuda de los usuarios.

Pruebas unitarias:
Los microservicios fueron probados con JUnit y Mockito, aplicando pruebas sobre servicios, controladores, configuración y manejo de excepciones cuando corresponde.

Documentación:
Cada microservicio cuenta con documentación Swagger/OpenAPI, lo que permite revisar y probar sus endpoints desde el navegador.

Estado del proyecto:
El sistema representa una solución de microservicios para Perfulandia SPA, permitiendo organizar las principales áreas del negocio: usuarios, autenticación, tiendas, productos, carrito, ventas, pedidos, reseñas y soporte.