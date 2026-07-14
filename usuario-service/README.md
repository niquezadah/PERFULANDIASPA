# Usuario Service

Microservicio responsable de gestionar usuarios, roles, permisos y validación de credenciales dentro del sistema Perfulandia SPA.

## Puerto

```txt
8082
```

## Base de datos

```txt
perfulandia_usuarios_db
```

## Responsabilidades principales

- Crear, listar, actualizar y eliminar usuarios.
- Activar o desactivar usuarios.
- Cambiar rol de usuario.
- Cambiar contraseña.
- Gestionar roles.
- Gestionar permisos.
- Validar credenciales para autenticación.
- Entregar datos de usuario a otros microservicios.

## Endpoints principales

### Usuarios

```txt
GET    /api/usuarios
GET    /api/usuarios/{id}
GET    /api/usuarios/estado
POST   /api/usuarios
PUT    /api/usuarios/{id}
PATCH  /api/usuarios/{id}/estado
PATCH  /api/usuarios/{id}/rol
PATCH  /api/usuarios/{id}/password
DELETE /api/usuarios/{id}
```

### Roles

```txt
GET    /api/roles
GET    /api/roles/{id}
POST   /api/roles
PUT    /api/roles/{id}
PUT    /api/roles/{idRol}/permisos/{idPermiso}
DELETE /api/roles/{id}
```

### Permisos

```txt
GET    /api/permisos
GET    /api/permisos/{id}
POST   /api/permisos
PUT    /api/permisos/{id}
DELETE /api/permisos/{id}
```

### Autenticación interna

```txt
POST /api/auth/login
POST /api/auth/validar-credenciales
```

## Integraciones

Este servicio es consultado por otros microservicios para validar usuarios existentes y activos.

Servicios que lo consumen:

- autenticacion-service
- carrito-service
- ventas-facturacion-service
- soporte-resena-service
- pedido-service
- logistica-service

## Swagger

```txt
http://localhost:8082/swagger-ui/index.html
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
