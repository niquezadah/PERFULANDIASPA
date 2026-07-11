# Perfulandia SPA - Sistema de Microservicios

Proyecto desarrollado para la asignatura **Desarrollo FullStack I**.  
El sistema representa una solución basada en microservicios para la empresa **Perfulandia SPA**, permitiendo gestionar usuarios, autenticación, tiendas, productos, carrito, ventas, pedidos, soporte y reseñas.

## Integrantes

- Javier Ancaten
- Claudio Córdova
- Nicolás Quezada

## Tecnologías utilizadas

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Cloud Gateway
- MySQL
- Maven
- JUnit
- Mockito
- Swagger / OpenAPI
- GitHub

## Microservicios del sistema

| Microservicio | Puerto | Responsabilidad principal |
|---|---:|---|
| api-gateway | 8090 | Punto de entrada único hacia los microservicios |
| autenticacion-service | 8081 | Autenticación y validación de credenciales |
| usuario-service | 8082 | Gestión de usuarios, roles y permisos |
| soporte-service | 8070 | Gestión de tickets y mensajes de soporte |
| pedido-service | 8071 | Gestión de pedidos |
| tiendas-service | 8091 | Gestión de sucursales o tiendas |
| inventario-catalogo-service | 8092 | Gestión de productos e inventario |
| soporte-resena-service | 8093 | Gestión de reseñas de productos |
| carrito-service | 8094 | Gestión del carrito de compras |
| ventas-facturacion-service | 8095 | Registro de ventas y facturación |

## Comunicación entre microservicios

El sistema utiliza comunicación REST entre microservicios para validar datos y mantener la coherencia del flujo.

Integraciones principales:

| Servicio origen | Servicio destino | Uso |
|---|---|---|
| autenticacion-service | usuario-service | Validar credenciales de usuario |
| inventario-catalogo-service | tiendas-service | Validar existencia de tienda |
| carrito-service | usuario-service | Validar cliente existente y activo |
| carrito-service | inventario-catalogo-service | Validar producto, disponibilidad y stock |
| ventas-facturacion-service | usuario-service | Validar cliente existente y activo |
| ventas-facturacion-service | carrito-service | Obtener total del carrito |
| soporte-resena-service | usuario-service | Validar cliente existente y activo |
| soporte-resena-service | inventario-catalogo-service | Validar producto asociado a la reseña |
| pedido-service | usuario-service | Validar usuario |
| pedido-service | tiendas-service | Validar tienda |
| pedido-service | inventario-catalogo-service | Validar producto |

## Bases de datos

Cada microservicio utiliza su propia base de datos MySQL.

Bases de datos principales:

```sql
CREATE DATABASE IF NOT EXISTS perfulandia_usuarios_db;
CREATE DATABASE IF NOT EXISTS perfulandia_tiendas_db;
CREATE DATABASE IF NOT EXISTS perfulandia_inventario_catalogo_db;
CREATE DATABASE IF NOT EXISTS perfulandia_carrito_db;
CREATE DATABASE IF NOT EXISTS perfulandia_ventas_facturacion_db;
CREATE DATABASE IF NOT EXISTS perfulandia_soporte_resena_db;
CREATE DATABASE IF NOT EXISTS perfulandia_soporte_db;
CREATE DATABASE IF NOT EXISTS perfulandia_pedido_db;