package cl.perfulandia.usuarios.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.perfulandia.usuarios.dto.CambiarEstadoRequest;
import cl.perfulandia.usuarios.dto.CambiarPasswordRequest;
import cl.perfulandia.usuarios.dto.CambiarRolRequest;
import cl.perfulandia.usuarios.dto.UsuarioResponse;
import cl.perfulandia.usuarios.model.Usuario;
import cl.perfulandia.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public CollectionModel<EntityModel<UsuarioResponse>> listarUsuarios() {
        List<EntityModel<UsuarioResponse>> usuarios = usuarioService.listarUsuarios()
                .stream()
                .map(usuarioService::convertirAUsuarioResponse)
                .map(this::agregarLinks)
                .toList();

        return CollectionModel.of(
                usuarios,
                linkTo(methodOn(UsuarioController.class).listarUsuarios()).withSelfRel()
        );
    }

    @GetMapping("/estado")
    public CollectionModel<EntityModel<UsuarioResponse>> listarUsuariosPorEstado(
            @RequestParam Boolean estado) {

        List<EntityModel<UsuarioResponse>> usuarios = usuarioService.listarUsuariosPorEstado(estado)
                .stream()
                .map(usuarioService::convertirAUsuarioResponse)
                .map(this::agregarLinks)
                .toList();

        return CollectionModel.of(
                usuarios,
                linkTo(methodOn(UsuarioController.class)
                        .listarUsuariosPorEstado(estado)).withSelfRel(),
                linkTo(methodOn(UsuarioController.class)
                        .listarUsuarios()).withRel("usuarios")
        );
    }

    @Operation(
            summary = "Buscar usuario por ID",
            description = "Obtiene la información de un usuario según su identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario encontrado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "idUsuario": 1,
                                      "nombre": "Juan",
                                      "apellido": "Perez",
                                      "correo": "juan.perez@perfulandia.cl",
                                      "direccionEnvio": "Av. Siempre Viva 123",
                                      "estado": true,
                                      "idRol": 2,
                                      "nombreRol": "CLIENTE"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-06-25T16:10:00",
                                      "status": 404,
                                      "error": "Not Found",
                                      "mensaje": "Usuario no encontrado con ID: 99"
                                    }
                                    """)
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioResponse>> buscarUsuarioPorId(
            @PathVariable Long id) {

        Optional<Usuario> usuario = usuarioService.buscarUsuarioPorId(id);

        if (usuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UsuarioResponse response = usuarioService.convertirAUsuarioResponse(usuario.get());

        return ResponseEntity.ok(agregarLinks(response));
    }

    @Operation(
            summary = "Crear usuario",
            description = "Registra un nuevo usuario en el microservicio."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del usuario que se desea crear.",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Usuario.class),
                    examples = @ExampleObject(value = """
                            {
                              "nombre": "Juan",
                              "apellido": "Perez",
                              "correo": "juan.perez@perfulandia.cl",
                              "passwordHash": "usuario123",
                              "direccionEnvio": "Av. Siempre Viva 123",
                              "estado": true,
                              "rol": {
                                "idRol": 2
                              }
                            }
                            """)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario creado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "idUsuario": 1,
                                      "nombre": "Juan",
                                      "apellido": "Perez",
                                      "correo": "juan.perez@perfulandia.cl",
                                      "direccionEnvio": "Av. Siempre Viva 123",
                                      "estado": true,
                                      "idRol": 2,
                                      "nombreRol": "CLIENTE"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-06-25T16:20:00",
                                      "status": 400,
                                      "error": "Bad Request",
                                      "mensaje": "Error de validación",
                                      "errores": [
                                        "nombre: El nombre es obligatorio",
                                        "correo: El correo debe tener un formato válido"
                                      ]
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe un usuario con el correo indicado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-06-25T16:20:00",
                                      "status": 409,
                                      "error": "Conflict",
                                      "mensaje": "Ya existe un usuario con el correo: juan.perez@perfulandia.cl"
                                    }
                                    """)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<EntityModel<UsuarioResponse>> guardarUsuario(
            @Valid @RequestBody Usuario usuario) {

        Usuario usuarioGuardado = usuarioService.guardarUsuario(usuario);
        UsuarioResponse response = usuarioService.convertirAUsuarioResponse(usuarioGuardado);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(agregarLinks(response));
    }

    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza los datos principales de un usuario existente."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos actualizados del usuario.",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Usuario.class),
                    examples = @ExampleObject(value = """
                            {
                              "nombre": "Juan Actualizado",
                              "apellido": "Perez",
                              "correo": "juan.actualizado@perfulandia.cl",
                              "passwordHash": "usuario123",
                              "direccionEnvio": "Av. Nueva 456",
                              "estado": true,
                              "rol": {
                                "idRol": 2
                              }
                            }
                            """)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario actualizado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "idUsuario": 1,
                                      "nombre": "Juan Actualizado",
                                      "apellido": "Perez",
                                      "correo": "juan.actualizado@perfulandia.cl",
                                      "direccionEnvio": "Av. Nueva 456",
                                      "estado": true,
                                      "idRol": 2,
                                      "nombreRol": "CLIENTE"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-06-25T16:25:00",
                                      "status": 400,
                                      "error": "Bad Request",
                                      "mensaje": "Error de validación",
                                      "errores": [
                                        "nombre: El nombre es obligatorio",
                                        "correo: El correo debe tener un formato válido"
                                      ]
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-06-25T16:25:00",
                                      "status": 404,
                                      "error": "Not Found",
                                      "mensaje": "Usuario no encontrado con ID: 99"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El correo ya pertenece a otro usuario",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-06-25T16:25:00",
                                      "status": 409,
                                      "error": "Conflict",
                                      "mensaje": "Ya existe un usuario con el correo: juan.actualizado@perfulandia.cl"
                                    }
                                    """)
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioResponse>> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody Usuario usuario) {

        Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, usuario);
        UsuarioResponse response = usuarioService.convertirAUsuarioResponse(usuarioActualizado);

        return ResponseEntity.ok(agregarLinks(response));
    }

    @Operation(
            summary = "Cambiar estado de usuario",
            description = "Activa o desactiva un usuario existente."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Nuevo estado del usuario.",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CambiarEstadoRequest.class),
                    examples = @ExampleObject(value = """
                            {
                              "estado": false
                            }
                            """)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estado del usuario actualizado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "idUsuario": 1,
                                      "nombre": "Juan",
                                      "apellido": "Perez",
                                      "correo": "juan.perez@perfulandia.cl",
                                      "direccionEnvio": "Av. Siempre Viva 123",
                                      "estado": false,
                                      "idRol": 2,
                                      "nombreRol": "CLIENTE"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida. El estado es obligatorio.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-06-25T16:35:00",
                                      "status": 400,
                                      "error": "Bad Request",
                                      "mensaje": "Error de validación",
                                      "errores": [
                                        "estado: El estado es obligatorio"
                                      ]
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-06-25T16:35:00",
                                      "status": 404,
                                      "error": "Not Found",
                                      "mensaje": "Usuario no encontrado con ID: 99"
                                    }
                                    """)
                    )
            )
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<EntityModel<UsuarioResponse>> cambiarEstadoUsuario(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoRequest request) {

        Usuario usuarioActualizado =
                usuarioService.cambiarEstadoUsuario(id, request.getEstado());

        UsuarioResponse response =
                usuarioService.convertirAUsuarioResponse(usuarioActualizado);

        return ResponseEntity.ok(agregarLinks(response));
    }

    @Operation(
            summary = "Cambiar rol de usuario",
            description = "Asigna un nuevo rol a un usuario existente."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Identificador del nuevo rol.",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CambiarRolRequest.class),
                    examples = @ExampleObject(value = """
                            {
                              "idRol": 1
                            }
                            """)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rol del usuario actualizado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "idUsuario": 1,
                                      "nombre": "Juan",
                                      "apellido": "Perez",
                                      "correo": "juan.perez@perfulandia.cl",
                                      "direccionEnvio": "Av. Siempre Viva 123",
                                      "estado": true,
                                      "idRol": 1,
                                      "nombreRol": "ADMINISTRADOR"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida. El id del rol es obligatorio.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-06-25T16:40:00",
                                      "status": 400,
                                      "error": "Bad Request",
                                      "mensaje": "Error de validación",
                                      "errores": [
                                        "idRol: El id del rol es obligatorio"
                                      ]
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario o rol no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-06-25T16:40:00",
                                      "status": 404,
                                      "error": "Not Found",
                                      "mensaje": "Rol no encontrado con ID: 99"
                                    }
                                    """)
                    )
            )
    })
    @PatchMapping("/{id}/rol")
    public ResponseEntity<EntityModel<UsuarioResponse>> cambiarRolUsuario(
            @PathVariable Long id,
            @Valid @RequestBody CambiarRolRequest request) {

        Usuario usuarioActualizado =
                usuarioService.cambiarRolUsuario(id, request.getIdRol());

        UsuarioResponse response =
                usuarioService.convertirAUsuarioResponse(usuarioActualizado);

        return ResponseEntity.ok(agregarLinks(response));
    }

    @Operation(
            summary = "Cambiar contraseña de usuario",
            description = "Actualiza la contraseña de un usuario validando su contraseña actual."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Contraseña actual y nueva contraseña.",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CambiarPasswordRequest.class),
                    examples = @ExampleObject(value = """
                            {
                              "passwordActual": "usuario123",
                              "passwordNueva": "usuario456"
                            }
                            """)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Contraseña actualizada correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "idUsuario": 1,
                                      "nombre": "Juan",
                                      "apellido": "Perez",
                                      "correo": "juan.perez@perfulandia.cl",
                                      "direccionEnvio": "Av. Siempre Viva 123",
                                      "estado": true,
                                      "idRol": 2,
                                      "nombreRol": "CLIENTE"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida o contraseña actual incorrecta.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-06-25T16:45:00",
                                      "status": 400,
                                      "error": "Bad Request",
                                      "mensaje": "La contraseña actual es incorrecta"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-06-25T16:45:00",
                                      "status": 404,
                                      "error": "Not Found",
                                      "mensaje": "Usuario no encontrado con ID: 99"
                                    }
                                    """)
                    )
            )
    })
    @PatchMapping("/{id}/password")
    public ResponseEntity<EntityModel<UsuarioResponse>> cambiarPassword(
            @PathVariable Long id,
            @Valid @RequestBody CambiarPasswordRequest request) {

        Usuario usuarioActualizado = usuarioService.cambiarPassword(
                id,
                request.getPasswordActual(),
                request.getPasswordNueva()
        );

        UsuarioResponse response =
                usuarioService.convertirAUsuarioResponse(usuarioActualizado);

        return ResponseEntity.ok(agregarLinks(response));
    }

    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina un usuario existente según su identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Usuario eliminado correctamente",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-06-25T16:30:00",
                                      "status": 404,
                                      "error": "Not Found",
                                      "mensaje": "Usuario no encontrado con ID: 99"
                                    }
                                    """)
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<UsuarioResponse> agregarLinks(UsuarioResponse response) {
        EntityModel<UsuarioResponse> usuarioModel = EntityModel.of(response);

        usuarioModel.add(
                linkTo(methodOn(UsuarioController.class)
                        .buscarUsuarioPorId(response.getIdUsuario()))
                        .withSelfRel()
        );

        usuarioModel.add(
                linkTo(methodOn(UsuarioController.class)
                        .listarUsuarios())
                        .withRel("usuarios")
        );

        usuarioModel.add(
                linkTo(methodOn(UsuarioController.class)
                        .cambiarEstadoUsuario(response.getIdUsuario(), null))
                        .withRel("cambiar-estado")
        );

        usuarioModel.add(
                linkTo(methodOn(UsuarioController.class)
                        .cambiarRolUsuario(response.getIdUsuario(), null))
                        .withRel("cambiar-rol")
        );

        usuarioModel.add(
                linkTo(methodOn(UsuarioController.class)
                        .cambiarPassword(response.getIdUsuario(), null))
                        .withRel("cambiar-password")
        );

        return usuarioModel;
    }
}