# Soporte Service

Microservicio responsable de gestionar tickets y mensajes de soporte para clientes de Perfulandia SPA.

## Puerto

```txt
8070
```

## Base de datos

```txt
perfulandia_soporte_db
```

## Responsabilidades principales

- Crear tickets de soporte.
- Listar tickets.
- Buscar ticket por ID.
- Listar tickets por usuario.
- Listar tickets asignados a un responsable.
- Actualizar ticket.
- Cambiar estado de ticket.
- Asignar responsable.
- Cerrar ticket.
- Cancelar ticket.
- Eliminar ticket.
- Agregar y listar mensajes de ticket.

## Endpoints principales

### Tickets

```txt
GET    /api/tickets
GET    /api/tickets/{idTicket}
GET    /api/tickets/usuario/{idUsuario}
GET    /api/tickets/asignado/{idUsuarioAsignado}
POST   /api/tickets
PUT    /api/tickets/{idTicket}
PATCH  /api/tickets/{idTicket}/estado
PATCH  /api/tickets/{idTicket}/asignar
PATCH  /api/tickets/{idTicket}/cerrar
PATCH  /api/tickets/{idTicket}/cancelar
DELETE /api/tickets/{idTicket}
```

### Mensajes

```txt
GET    /api/tickets/{idTicket}/mensajes
POST   /api/tickets/{idTicket}/mensajes
GET    /api/tickets/{idTicket}/mensajes/{idMensaje}
DELETE /api/tickets/{idTicket}/mensajes/{idMensaje}
```

## Datos principales

Un ticket maneja:

- usuario solicitante
- asunto
- descripción
- prioridad
- estado
- fecha de creación
- fecha de actualización
- usuario asignado

## Estados del ticket

```txt
ABIERTO
EN_REVISION
RESPONDIDO
CERRADO
CANCELADO
```

## Prioridades

```txt
BAJA
MEDIA
ALTA
URGENTE
```

## Reglas de negocio principales

- No se puede actualizar un ticket cerrado.
- No se puede actualizar un ticket cancelado.
- No se puede cambiar estado de un ticket cerrado o cancelado.
- No se pueden agregar mensajes a tickets cerrados o cancelados.
- Al asignar responsable, el ticket pasa a `EN_REVISION`.
- Al agregar mensaje, el ticket pasa a `RESPONDIDO`.

## Swagger

```txt
http://localhost:8070/swagger-ui/index.html
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
