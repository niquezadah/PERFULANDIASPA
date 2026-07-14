# Tiendas Service

Microservicio responsable de gestionar las tiendas o sucursales de Perfulandia SPA.

## Puerto

```txt
8091
```

## Base de datos

```txt
perfulandia_tiendas_db
```

## Responsabilidades principales

- Crear tiendas.
- Listar tiendas.
- Buscar tienda por ID.
- Actualizar datos de tienda.
- Activar o desactivar tienda.
- Eliminar tienda.

## Endpoints principales

```txt
GET    /api/v1/tiendas
GET    /api/v1/tiendas/{id}
POST   /api/v1/tiendas
PUT    /api/v1/tiendas/{id}
PATCH  /api/v1/tiendas/{id}/estado
DELETE /api/v1/tiendas/{id}
```

## Datos principales

Una tienda maneja datos como:

- nombre
- dirección
- comuna
- ciudad
- región
- teléfono
- personal asignado
- horario de apertura y cierre
- estado activo/inactivo
- políticas locales

## Integraciones

Este servicio es consultado por:

- inventario-catalogo-service
- pedido-service

## Swagger

```txt
http://localhost:8091/swagger-ui/index.html
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
