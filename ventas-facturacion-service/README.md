# Ventas Facturacion Service

Microservicio responsable de registrar ventas y generar información de facturación para Perfulandia SPA.

## Puerto

```txt
8095
```

## Base de datos

```txt
perfulandia_ventas_facturacion_db
```

## Responsabilidades principales

- Registrar ventas.
- Generar número de factura.
- Obtener total desde el carrito del cliente.
- Listar ventas.
- Buscar venta por ID.
- Listar ventas por cliente.
- Listar ventas por estado.
- Listar ventas facturadas.
- Actualizar estado de venta.
- Eliminar ventas.

## Endpoints principales

```txt
GET    /api/v1/ventas-facturacion
GET    /api/v1/ventas-facturacion/{id}
POST   /api/v1/ventas-facturacion
PUT    /api/v1/ventas-facturacion/{id}
DELETE /api/v1/ventas-facturacion/{id}
GET    /api/v1/ventas-facturacion/cliente/{idCliente}
GET    /api/v1/ventas-facturacion/estado/{estadoVenta}
GET    /api/v1/ventas-facturacion/facturadas
PATCH  /api/v1/ventas-facturacion/{id}/estado
```

## Integraciones

| Servicio destino | Uso |
|---|---|
| usuario-service | Validar que el cliente exista y esté activo |
| carrito-service | Obtener el total del carrito del cliente |

## Reglas de negocio principales

- No se registra venta si el cliente no existe.
- No se registra venta si el cliente está inactivo.
- No se registra venta si no se puede obtener el total del carrito.
- No se registra venta si el carrito está vacío.
- Si no se envía estado, se asigna `PAGADA`.
- Si no se envía facturada, se asigna `true`.

## Swagger

```txt
http://localhost:8095/swagger-ui/index.html
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
