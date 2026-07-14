# Carrito Service

Microservicio responsable de gestionar el carrito de compras de los clientes de Perfulandia SPA.

## Puerto

```txt
8094
```

## Base de datos

```txt
perfulandia_carrito_db
```

## Responsabilidades principales

- Agregar productos al carrito.
- Listar carritos.
- Buscar carrito por ID.
- Actualizar productos del carrito.
- Eliminar ítems del carrito.
- Listar carrito por cliente.
- Listar carrito por producto.
- Listar productos activos del carrito.
- Calcular total del carrito.
- Vaciar carrito de un cliente.

## Endpoints principales

```txt
GET    /api/v1/carrito
GET    /api/v1/carrito/{id}
POST   /api/v1/carrito
PUT    /api/v1/carrito/{id}
DELETE /api/v1/carrito/{id}
GET    /api/v1/carrito/cliente/{idCliente}
GET    /api/v1/carrito/producto/{idProducto}
GET    /api/v1/carrito/cliente/{idCliente}/activos
GET    /api/v1/carrito/cliente/{idCliente}/total
DELETE /api/v1/carrito/cliente/{idCliente}
```

## Integraciones

| Servicio destino | Uso |
|---|---|
| usuario-service | Validar que el cliente exista y esté activo |
| inventario-catalogo-service | Validar producto, disponibilidad, precio y stock |

## Reglas de negocio principales

- No se puede agregar un producto de un cliente inexistente.
- No se puede agregar producto si el cliente está inactivo.
- No se puede agregar un producto inexistente.
- No se puede agregar producto no disponible.
- No se puede agregar una cantidad mayor al stock disponible.
- El subtotal se calcula usando cantidad y precio unitario.

## Swagger

```txt
http://localhost:8094/swagger-ui/index.html
```

## Ejecución local

```bash
./mvnw spring-boot:run
```

En Windows:

```bash
mvnw.cmd spring-boot:run
```

## Pruebas

```bash
./mvnw test
```
