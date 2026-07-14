# API Gateway

Microservicio encargado de centralizar el acceso hacia los demás microservicios de Perfulandia SPA.

## Puerto

```txt
8090
```

## Responsabilidad

- Actuar como punto de entrada único.
- Redirigir solicitudes hacia los microservicios correspondientes.
- Centralizar rutas principales del sistema.

## Rutas configuradas

| Ruta Gateway | Microservicio destino |
|---|---|
| `/api/soporte/**` | soporte-service |
| `/api/pedidos/**` | pedido-service |
| `/api/v1/tiendas/**` | tiendas-service |
| `/api/v1/productos/**` | inventario-catalogo-service |
| `/api/v1/resenas/**` | soporte-resena-service |
| `/api/auth/**` | autenticacion-service |
| `/api/v1/carrito/**` | carrito-service |
| `/api/v1/ventas-facturacion/**` | ventas-facturacion-service |
| `/api/usuarios/**` | usuario-service |
| `/api/roles/**` | usuario-service |
| `/api/permisos/**` | usuario-service |

## Tecnologías

- Spring Boot
- Spring Cloud Gateway
- Maven

## Ejecución local

```bash
./mvnw spring-boot:run
```

En Windows:

```bash
mvnw.cmd spring-boot:run
```

## Nota

El Gateway no maneja lógica de negocio ni persistencia. Su responsabilidad es enrutar las solicitudes hacia los microservicios.
