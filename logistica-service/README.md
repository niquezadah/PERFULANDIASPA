# Logistica Service

Microservicio responsable de gestionar envíos y seguimiento logístico de pedidos de Perfulandia SPA.

## Puerto

```txt
8096
```

## Base de datos

```txt
perfulandia_logistica_db
```

## Responsabilidades principales

- Crear envíos.
- Listar envíos.
- Buscar envío por ID.
- Buscar envío por pedido.
- Buscar envíos por cliente.
- Buscar envíos por estado.
- Buscar envíos por tienda de origen.
- Actualizar estado del envío.
- Eliminar envío.

## Endpoints principales

```txt
POST   /api/v1/logistica/envios
GET    /api/v1/logistica/envios
GET    /api/v1/logistica/envios/{idEnvio}
GET    /api/v1/logistica/envios/pedido/{idPedido}
GET    /api/v1/logistica/envios/cliente/{idCliente}
GET    /api/v1/logistica/envios/estado/{estadoEnvio}
GET    /api/v1/logistica/envios/tienda/{idTiendaOrigen}
PUT    /api/v1/logistica/envios/{idEnvio}/estado
DELETE /api/v1/logistica/envios/{idEnvio}
```

## Integraciones

| Servicio destino | Uso |
|---|---|
| pedido-service | Validar que el pedido exista |
| usuario-service | Validar que el cliente exista |

## Estados del envío

```txt
PENDIENTE
PREPARANDO
EN_TRANSITO
ENTREGADO
FALLIDO
CANCELADO
```

## Tipos de entrega

```txt
DOMICILIO
RETIRO_EN_TIENDA
TRASLADO_A_SUCURSAL
```

## Reglas de negocio principales

- Un envío debe estar asociado a un pedido.
- Un envío debe estar asociado a un cliente.
- El estado del envío se puede actualizar durante el proceso logístico.
- Se puede consultar el seguimiento por pedido, cliente, estado o tienda.

## Swagger

```txt
http://localhost:8096/swagger-ui/index.html
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
