# Pedido Service

Microservicio responsable de gestionar pedidos realizados por clientes de Perfulandia SPA.

## Puerto

```txt
8071
```

## Base de datos

```txt
perfulandia_pedido_db
```

## Responsabilidades principales

- Crear pedidos.
- Listar pedidos.
- Buscar pedido por ID.
- Buscar pedidos por usuario.
- Buscar pedidos por tienda.
- Buscar pedidos por estado.
- Actualizar estado de pedido.
- Cancelar pedido.
- Registrar detalle de productos del pedido.

## Endpoints principales

```txt
POST /api/pedidos
GET  /api/pedidos
GET  /api/pedidos/{idPedido}
GET  /api/pedidos/usuario/{idUsuario}
GET  /api/pedidos/tienda/{idTienda}
GET  /api/pedidos/estado/{estado}
PUT  /api/pedidos/{idPedido}/estado
PUT  /api/pedidos/{idPedido}/cancelar
```

## Integraciones

| Servicio destino | Uso |
|---|---|
| usuario-service | Validar que el usuario exista y esté activo |
| tiendas-service | Validar que la tienda exista |
| inventario-catalogo-service | Validar que los productos existan |

## Reglas de negocio principales

- Un pedido debe tener al menos un producto.
- No se crea pedido si el usuario no existe o está inactivo.
- No se crea pedido si la tienda no existe.
- No se crea pedido si algún producto no existe.
- El total se calcula sumando los subtotales de cada detalle.
- No se puede modificar un pedido cancelado.
- No se puede modificar un pedido entregado.
- No se puede cancelar un pedido entregado.

## Estados del pedido

```txt
PENDIENTE
CONFIRMADO
PREPARANDO
ENVIADO
ENTREGADO
CANCELADO
```

## Swagger

```txt
http://localhost:8071/swagger-ui/index.html
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
