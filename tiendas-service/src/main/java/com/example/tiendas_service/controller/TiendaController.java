package com.example.tiendas_service.controller;

import com.example.tiendas_service.dto.ActualizarEstadoTiendaDTO;
import com.example.tiendas_service.dto.TiendaDTO;
import com.example.tiendas_service.service.TiendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tiendas")
@Tag(
        name = "Gestión de Tiendas",
        description = "Endpoints para registrar, consultar, actualizar, eliminar y cambiar el estado de las tiendas de Perfulandia."
)
public class TiendaController {

    private final TiendaService tiendaService;

    public TiendaController(TiendaService tiendaService) {
        this.tiendaService = tiendaService;
    }

    @Operation(
            summary = "Listar tiendas",
            description = "Obtiene todas las tiendas registradas en Perfulandia."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de tiendas obtenida correctamente"
            )
    })
    @GetMapping
    public ResponseEntity<List<TiendaDTO>> listarTiendas() {
        List<TiendaDTO> tiendas = tiendaService.listarTiendas();
        return ResponseEntity.ok(tiendas);
    }

    @Operation(
            summary = "Buscar tienda por ID",
            description = "Obtiene la información de una tienda específica usando su identificador."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tienda encontrada correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe una tienda con el ID indicado",
                    content = @Content()
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<TiendaDTO> buscarTiendaPorId(
            @Parameter(
                    description = "ID de la tienda que se desea consultar",
                    example = "1"
            )
            @PathVariable Long id) {

        return tiendaService.buscarTiendaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Registrar tienda",
            description = "Crea una nueva tienda para Perfulandia."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Tienda creada correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o campos obligatorios faltantes",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "object"),
                            examples = @ExampleObject(
                                    name = "Error de validación",
                                    value = """
                                            {
                                              "timestamp": "2026-06-19T12:00:00",
                                              "status": 400,
                                              "error": "ERROR VALIDACIÓN",
                                              "mensajes": {
                                                "nombre": "El NOMBRE de la TIENDA es OBLIGATORIO"
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<TiendaDTO> crearTienda(
            @Valid @RequestBody TiendaDTO tiendaDTO) {

        TiendaDTO nuevaTienda = tiendaService.guardarTienda(tiendaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaTienda);
    }

    @Operation(
            summary = "Actualizar tienda",
            description = "Actualiza todos los datos de una tienda existente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tienda actualizada correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o campos obligatorios faltantes",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "object"),
                            examples = @ExampleObject(
                                    name = "Error de validación",
                                    value = """
                                            {
                                              "timestamp": "2026-06-19T12:00:00",
                                              "status": 400,
                                              "error": "ERROR VALIDACIÓN",
                                              "mensajes": {
                                                "nombre": "El NOMBRE de la TIENDA es OBLIGATORIO"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe una tienda con el ID indicado",
                    content = @Content()
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<TiendaDTO> actualizarTienda(
            @Parameter(
                    description = "ID de la tienda que se desea actualizar",
                    example = "1"
            )
            @PathVariable Long id,
            @Valid @RequestBody TiendaDTO tiendaDTO) {

        if (!tiendaService.existeTiendaPorId(id)) {
            return ResponseEntity.notFound().build();
        }

        tiendaDTO.setIdTienda(id);
        TiendaDTO tiendaActualizada = tiendaService.guardarTienda(tiendaDTO);
        return ResponseEntity.ok(tiendaActualizada);
    }

    @Operation(
            summary = "Actualizar estado de tienda",
            description = "Activa o desactiva una tienda sin modificar el resto de sus datos."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Estado de la tienda actualizado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Estado activo no informado o inválido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "object"),
                            examples = @ExampleObject(
                                    name = "Error de validación",
                                    value = """
                                            {
                                              "timestamp": "2026-06-19T12:00:00",
                                              "status": 400,
                                              "error": "ERROR VALIDACIÓN",
                                              "mensajes": {
                                                "activa": "El ESTADO ACTIVO es OBLIGATORIO"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe una tienda con el ID indicado",
                    content = @Content()
            )
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<TiendaDTO> actualizarEstadoTienda(
            @Parameter(
                    description = "ID de la tienda cuyo estado se desea modificar",
                    example = "1"
            )
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoTiendaDTO estadoDTO) {

        return tiendaService.actualizarEstadoTienda(id, estadoDTO.getActiva())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Eliminar tienda",
            description = "Elimina una tienda existente según su identificador."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Tienda eliminada correctamente",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe una tienda con el ID indicado",
                    content = @Content()
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTienda(
            @Parameter(
                    description = "ID de la tienda que se desea eliminar",
                    example = "1"
            )
            @PathVariable Long id) {

        if (!tiendaService.existeTiendaPorId(id)) {
            return ResponseEntity.notFound().build();
        }

        tiendaService.eliminarTienda(id);
        return ResponseEntity.noContent().build();
    }
}