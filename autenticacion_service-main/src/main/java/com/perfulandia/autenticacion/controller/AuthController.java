package com.perfulandia.autenticacion.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.perfulandia.autenticacion.dto.LoginRequest;
import com.perfulandia.autenticacion.dto.LoginResponse;
import com.perfulandia.autenticacion.dto.ValidarTokenRequest;
import com.perfulandia.autenticacion.dto.ValidarTokenResponse;
import com.perfulandia.autenticacion.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Iniciar sesión",
            description = "Valida las credenciales del usuario y genera un token JWT con rol y permisos."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login correcto",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "token": "eyJhbGciOiJIUzM4NCJ9...",
                                      "tipo": "Bearer",
                                      "idUsuario": 1,
                                      "correo": "admin@perfulandia.cl",
                                      "rol": "ADMINISTRADOR",
                                      "permisos": [
                                        "CREAR_USUARIO",
                                        "EDITAR_USUARIO",
                                        "ELIMINAR_USUARIO",
                                        "VER_USUARIOS",
                                        "GESTIONAR_ROLES",
                                        "GESTIONAR_PERMISOS"
                                      ]
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
                                      "status": 400,
                                      "error": "Bad Request",
                                      "mensaje": "Error de validación",
                                      "errores": [
                                        "correo: El correo es obligatorio",
                                        "password: La contraseña es obligatoria"
                                      ]
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciales inválidas",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 401,
                                      "error": "Unauthorized",
                                      "mensaje": "Credenciales inválidas"
                                    }
                                    """)
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(
            summary = "Validar token JWT",
            description = "Recibe un token JWT y responde si es válido, junto con los datos principales del usuario."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token validado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidarTokenResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "valido": true,
                                      "mensaje": "Token válido",
                                      "idUsuario": 1,
                                      "correo": "admin@perfulandia.cl",
                                      "rol": "ADMINISTRADOR",
                                      "permisos": [
                                        "CREAR_USUARIO",
                                        "EDITAR_USUARIO"
                                      ]
                                    }
                                    """)
                    )
            )
    })
    @PostMapping("/validar-token")
    public ResponseEntity<ValidarTokenResponse> validarToken(@Valid @RequestBody ValidarTokenRequest request) {
        return ResponseEntity.ok(authService.validarToken(request.getToken()));
    }

    @Operation(
            summary = "Verificar permiso",
            description = "Verifica si el token JWT enviado en el header Authorization contiene un permiso específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Resultado de autorización",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "permitido": true,
                                      "permiso": "CREAR_USUARIO"
                                    }
                                    """)
                    )
            )
    })
    @GetMapping("/tiene-permiso")
    public ResponseEntity<Map<String, Object>> tienePermiso(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam String permiso
    ) {
        boolean permitido = authService.tienePermiso(authorization, permiso);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("permitido", permitido);
        respuesta.put("permiso", permiso);

        return ResponseEntity.ok(respuesta);
    }
}