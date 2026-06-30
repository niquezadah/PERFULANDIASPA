package com.example.soporte_resena_service.controller;

import com.example.soporte_resena_service.dto.ResenaDTO;
import com.example.soporte_resena_service.service.ResenaService;
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
@RequestMapping("/api/v1/resenas")
@Tag(
        name = "Gestión de Reseñas",
        description = "Endpoints para registrar, consultar, actualizar, eliminar y filtrar reseñas de productos de Perfulandia."
)
public class ResenaController {

    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @Operation(
            summary = "Listar reseñas",
            description = "Obtiene todas las reseñas registradas en Perfulandia."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de reseñas obtenida correctamente"
            )
    })
    @GetMapping
    public ResponseEntity<List<ResenaDTO>> listarResenas() {
        List<ResenaDTO> resenas = resenaService.listarResenas();
        return ResponseEntity.ok(resenas);
    }

    @Operation(
            summary = "Buscar reseña por ID",
            description = "Obtiene la información de una reseña específica usando su identificador."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reseña encontrada correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe una reseña con el ID indicado",
                    content = @Content()
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResenaDTO> buscarResenaPorId(
            @Parameter(
                    description = "ID de la reseña que se desea consultar",
                    example = "1"
            )
            @PathVariable Long id) {

        return resenaService.buscarResenaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Registrar reseña",
            description = "Crea una nueva reseña asociada a un producto existente de Perfulandia."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Reseña creada correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos, campos obligatorios faltantes o producto inexistente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "object"),
                            examples = {
                                    @ExampleObject(
                                            name = "Error de validación",
                                            value = """
                                                    {
                                                      "timestamp": "2026-06-23T12:00:00",
                                                      "status": 400,
                                                      "error": "ERROR VALIDACIÓN",
                                                      "mensajes": {
                                                        "nombreCliente": "El NOMBRE del CLIENTE es OBLIGATORIO"
                                                      }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Producto inexistente",
                                            value = """
                                                    {
                                                      "timestamp": "2026-06-23T12:00:00",
                                                      "status": 400,
                                                      "error": "ERROR DE SOLICITUD",
                                                      "mensaje": "El producto con ID 99 no existe"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping
    public ResponseEntity<ResenaDTO> crearResena(
            @Valid @RequestBody ResenaDTO resenaDTO) {

        ResenaDTO nuevaResena = resenaService.guardarResena(resenaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaResena);
    }

    @Operation(
            summary = "Actualizar reseña",
            description = "Actualiza todos los datos de una reseña existente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reseña actualizada correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos, campos obligatorios faltantes o producto inexistente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "object"),
                            examples = {
                                    @ExampleObject(
                                            name = "Error de validación",
                                            value = """
                                                    {
                                                      "timestamp": "2026-06-23T12:00:00",
                                                      "status": 400,
                                                      "error": "ERROR VALIDACIÓN",
                                                      "mensajes": {
                                                        "calificacion": "La CALIFICACIÓN máxima es 5"
                                                      }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Producto inexistente",
                                            value = """
                                                    {
                                                      "timestamp": "2026-06-23T12:00:00",
                                                      "status": 400,
                                                      "error": "ERROR DE SOLICITUD",
                                                      "mensaje": "El producto con ID 99 no existe"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe una reseña con el ID indicado",
                    content = @Content()
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResenaDTO> actualizarResena(
            @Parameter(
                    description = "ID de la reseña que se desea actualizar",
                    example = "1"
            )
            @PathVariable Long id,
            @Valid @RequestBody ResenaDTO resenaDTO) {

        if (!resenaService.existeResenaPorId(id)) {
            return ResponseEntity.notFound().build();
        }

        resenaDTO.setIdResena(id);
        ResenaDTO resenaActualizada = resenaService.actualizarResena(resenaDTO);
        return ResponseEntity.ok(resenaActualizada);
    }

    @Operation(
            summary = "Eliminar reseña",
            description = "Elimina una reseña existente según su identificador."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Reseña eliminada correctamente",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe una reseña con el ID indicado",
                    content = @Content()
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarResena(
            @Parameter(
                    description = "ID de la reseña que se desea eliminar",
                    example = "1"
            )
            @PathVariable Long id) {

        if (!resenaService.existeResenaPorId(id)) {
            return ResponseEntity.notFound().build();
        }

        resenaService.eliminarResena(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Listar reseñas por producto",
            description = "Obtiene todas las reseñas asociadas a un producto específico."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reseñas del producto obtenidas correctamente"
            )
    })
    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<ResenaDTO>> listarResenasPorProducto(
            @Parameter(
                    description = "ID del producto asociado a las reseñas",
                    example = "1"
            )
            @PathVariable Long idProducto) {

        List<ResenaDTO> resenas = resenaService.listarResenasPorProducto(idProducto);
        return ResponseEntity.ok(resenas);
    }

    @Operation(
            summary = "Listar reseñas activas",
            description = "Obtiene solo las reseñas marcadas como activas y visibles."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reseñas activas obtenidas correctamente"
            )
    })
    @GetMapping("/activas")
    public ResponseEntity<List<ResenaDTO>> listarResenasActivas() {
        List<ResenaDTO> resenas = resenaService.listarResenasActivas();
        return ResponseEntity.ok(resenas);
    }

    @Operation(
            summary = "Listar reseñas por calificación",
            description = "Obtiene las reseñas que coinciden con una calificación específica."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reseñas de la calificación obtenidas correctamente"
            )
    })
    @GetMapping("/calificacion/{calificacion}")
    public ResponseEntity<List<ResenaDTO>> listarResenasPorCalificacion(
            @Parameter(
                    description = "Calificación que se desea consultar. Debe estar entre 1 y 5.",
                    example = "5"
            )
            @PathVariable Integer calificacion) {

        List<ResenaDTO> resenas = resenaService.listarResenasPorCalificacion(calificacion);
        return ResponseEntity.ok(resenas);
    }
}