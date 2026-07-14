# Inventario Catalogo Service

Microservicio responsable de gestionar productos, stock y catálogo de Perfulandia SPA.

## Puerto

```txt
8092
```

## Base de datos

```txt
perfulandia_inventario_catalogo_db
```

## Responsabilidades principales

- Crear productos.
- Listar productos.
- Buscar producto por ID.
- Actualizar productos.
- Eliminar productos.
- Listar productos por tienda.
- Listar productos disponibles.
- Listar productos por categoría.
- Validar que la tienda asociada exista.

## Endpoints principales

```txt
GET    /api/v1/productos
GET    /api/v1/productos/{id}
POST   /api/v1/productos
PUT    /api/v1/productos/{id}
DELETE /api/v1/productos/{id}
GET    /api/v1/productos/tienda/{idTienda}
GET    /api/v1/productos/disponibles
GET    /api/v1/productos/categoria/{categoria}
```

## Integraciones

| Servicio destino | Uso |
|---|---|
| tiendas-service | Validar que el producto esté asociado a una tienda existente |

## Datos principales

Un producto maneja:

- nombre
- descripción
- categoría
- stock
- precio
- disponibilidad
- ID de tienda

## Swagger

```txt
http://localhost:8092/swagger-ui/index.html
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
