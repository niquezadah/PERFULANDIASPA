# Microservicio Usuarios - PerfuLandia SPA

Este repositorio contiene el microservicio de **usuarios y seguridad** para el sistema PerfuLandia SPA.

El microservicio permite gestionar usuarios, roles, permisos, autenticación básica de login, cambio de contraseña y administración del estado de los usuarios. Está desarrollado con Java 21, Spring Boot, Maven, MySQL, JPA, Validation, HATEOAS, Swagger UI, JUnit, Mockito y JaCoCo.

---

## Tecnologías utilizadas

* Java 21
* Spring Boot
* Maven
* MySQL
* Spring Web
* Spring Data JPA
* Spring Validation
* Spring HATEOAS
* Springdoc OpenAPI / Swagger UI
* JUnit 5
* Mockito
* JaCoCo
* XAMPP / MySQL local
* Postman

---

## Estructura principal del proyecto

```txt
src/main/java/cl/perfulandia/usuarios
├── controller
├── dto
├── model
├── repository
├── security
├── service
└── UsuariosApplication.java
```

```txt
src/test/java/cl/perfulandia/usuarios
├── controller
├── security
├── service
└── UsuariosApplicationTests.java
```

---

## Configuración del proyecto

La configuración principal del microservicio se encuentra en:

```txt
src/main/resources/application.yml
```

El microservicio utiliza el puerto:

```txt
8081
```

URL base local:

```txt
http://localhost:8081
```

---

## Base de datos

El microservicio utiliza MySQL en el puerto `3306`.

Base de datos utilizada:

```txt
perfulandia_usuarios_db
```

Antes de ejecutar el proyecto, se debe iniciar MySQL desde XAMPP o desde los servicios de Windows.

---

## Ejecución local

Para compilar el proyecto:

```powershell
.\mvnw.cmd clean compile
```

Para ejecutar el microservicio:

```powershell
.\mvnw.cmd spring-boot:run
```

El servicio queda disponible en:

```txt
http://localhost:8081
```

En caso de que Maven use una versión de Java distinta, se puede configurar Java 21 en la terminal actual con:

```powershell
$env:JAVA_HOME="C:\Program Files\Microsoft\jdk-21.0.11.10-hotspot"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
```

---

## Swagger UI

La documentación Swagger se encuentra en:

```txt
http://localhost:8081/swagger-ui/index.html
```

También puede probarse con:

```txt
http://localhost:8081/swagger-ui.html
```

Evidencia de Swagger:

```txt
docs/evidencias/05-swagger/01-swagger-ui-endpoints.png
```

---

## Endpoints principales

### Autenticación

```txt
POST /api/auth/login
```

### Usuarios

```txt
GET    /api/usuarios
GET    /api/usuarios/{id}
GET    /api/usuarios/estado/{estado}
POST   /api/usuarios
PUT    /api/usuarios/{id}
PUT    /api/usuarios/{id}/estado
PUT    /api/usuarios/{id}/rol
PUT    /api/usuarios/{id}/password
DELETE /api/usuarios/{id}
```

### Roles

```txt
GET    /api/roles
GET    /api/roles/{id}
POST   /api/roles
PUT    /api/roles/{id}
PUT    /api/roles/{id}/permisos/{idPermiso}
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

---

## Manejo de errores HTTP

El microservicio cuenta con un manejador global de errores mediante la clase:

```txt
ApiExceptionHandler
```

Este componente permite entregar respuestas HTTP claras y consistentes cuando ocurre una excepción o una validación incorrecta.

Códigos validados en Postman:

```txt
200 OK - Consulta correcta de roles
400 Bad Request - Datos inválidos al crear usuario
401 Unauthorized - Login con credenciales incorrectas
404 Not Found - Búsqueda de usuario inexistente
409 Conflict - Intento de crear un rol duplicado
```

Evidencias Postman:

```txt
docs/evidencias/06-postman
```

---

## Colección Postman

El proyecto incluye una colección Postman importable para probar los principales endpoints del microservicio.

Ruta:

```txt
docs/postman/PerfuLandia-Usuarios.postman_collection.json
```

La colección contiene pruebas para los siguientes casos:

```txt
GET  /api/roles              - 200 OK
POST /api/auth/login         - 401 Unauthorized
POST /api/usuarios           - 400 Bad Request
GET  /api/usuarios/999999    - 404 Not Found
POST /api/roles              - 409 Conflict
```

Para usarla en Postman:

1. Abrir Postman.
2. Seleccionar `Import`.
3. Importar el archivo:

```txt
docs/postman/PerfuLandia-Usuarios.postman_collection.json
```

---

## Pruebas unitarias

Para ejecutar las pruebas unitarias:

```powershell
.\mvnw.cmd clean test
```

Para ejecutar pruebas y generar reporte de cobertura:

```powershell
.\mvnw.cmd clean test jacoco:report
```

El reporte JaCoCo se genera en:

```txt
target/site/jacoco/index.html
```

---

## Cobertura de pruebas

La cobertura oficial del microservicio se mide con JaCoCo sobre las capas:

```txt
service
security
```

Estas capas contienen la lógica de negocio principal del microservicio.

Se excluyen de la medición principal:

```txt
controller
dto
model
repository
UsuariosApplication
```

Motivo de exclusión:

* `dto`: contiene objetos de transferencia de datos.
* `model`: contiene entidades JPA.
* `repository`: contiene interfaces de acceso a datos.
* `controller`: se valida funcionalmente mediante Swagger/Postman.
* `UsuariosApplication`: clase de arranque de Spring Boot.

Evidencias de cobertura:

```txt
docs/evidencias/04-cobertura/01-clean-test-build-success.png
docs/evidencias/04-cobertura/02-jacoco-reporte-general-98.png
```

---

## Evidencias disponibles

```txt
docs/evidencias/04-cobertura
docs/evidencias/05-swagger
docs/evidencias/06-postman
docs/postman
```

---

## Estado del microservicio

* Microservicio compila correctamente.
* Configuración migrada a `application.yml`.
* Pruebas unitarias ejecutadas correctamente.
* Cobertura JaCoCo generada correctamente.
* Swagger UI habilitado correctamente.
* Manejo global de errores HTTP implementado.
* Evidencias Postman generadas para respuestas 200, 400, 401, 404 y 409.
* Colección Postman incluida en el repositorio.
* MySQL funcionando localmente mediante XAMPP.
* Proyecto versionado en GitHub con commits técnicos.
