# Autenticacion Service

Microservicio responsable del login, generación de token y validación de permisos para usuarios de Perfulandia SPA.

## Puerto

```txt
8032
```

## Responsabilidades principales

- Recibir credenciales de login.
- Validar usuario y contraseña consultando a usuario-service.
- Generar token de autenticación.
- Validar token.
- Verificar permisos asociados al usuario autenticado.

## Endpoints principales

```txt
POST /api/auth/login
POST /api/auth/validar-token
GET  /api/auth/tiene-permiso
```

## Integraciones

| Servicio destino | Uso |
|---|---|
| usuario-service | Validar credenciales, rol y permisos del usuario |

## Configuración importante

```yml
app:
  usuario-service:
    url: "http://localhost:8082"
```

## Swagger

```txt
http://localhost:8032/swagger-ui/index.html
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

## Nota

Este servicio maneja autenticación. Los microservicios de negocio validan usuarios consultando a usuario-service, sin exigir token directamente en cada endpoint.
