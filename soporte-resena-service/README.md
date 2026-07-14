# Soporte Resena Service

Microservicio responsable de gestionar reseñas de productos realizadas por clientes de Perfulandia SPA.

## Puerto

```txt
8093
```

## Base de datos

```txt
perfulandia_soporte_resena_db
```

## Responsabilidades principales

- Crear reseñas.
- Listar reseñas.
- Buscar reseña por ID.
- Actualizar reseña.
- Eliminar reseña.
- Listar reseñas por producto.
- Listar reseñas activas.
- Listar reseñas por calificación.

## Endpoints principales

```txt
GET    /api/v1/resenas
GET    /api/v1/resenas/{id}
POST   /api/v1/resenas
PUT    /api/v1/resenas/{id}
DELETE /api/v1/resenas/{id}
GET    /api/v1/resenas/producto/{idProducto}
GET    /api/v1/resenas/activas
GET    /api/v1/resenas/calificacion/{calificacion}
```

## Integraciones

| Servicio destino | Uso |
|---|---|
| usuario-service | Validar que el cliente exista y esté activo |
| inventario-catalogo-service | Validar que el producto exista |

## Reglas de negocio principales

- No se registra reseña si el cliente no existe.
- No se registra reseña si el cliente está inactivo.
- No se registra reseña si el producto no existe.
- La calificación debe estar entre 1 y 5.
- El comentario y el nombre del cliente son obligatorios.

## Swagger

```txt
http://localhost:8093/swagger-ui/index.html
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
