package com.example.carrito_service.controller;

import com.example.carrito_service.dto.CarritoDTO;
import com.example.carrito_service.service.CarritoService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/v1/carrito")
@Tag(
        name = "Gestión de Carrito",
        description = "Endpoints para registrar, consultar, actualizar y eliminar productos del carrito de compra de Perfulandia."
)
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @Operation(
            summary = "Listar registros del carrito",
            description = "Obtiene todos los productos registrados en carritos de compra."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de registros del carrito obtenida correctamente"
            )
    })
    @GetMapping
    public ResponseEntity<List<CarritoDTO>> listarCarritos() {
        List<CarritoDTO> carritos = carritoService.listarCarritos();
        return ResponseEntity.ok(carritos);
    }

    @Operation(
            summary = "Buscar registro del carrito por ID",
            description = "Obtiene un registro específico del carrito usando su identificador."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Registro del carrito encontrado correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe un registro del carrito con el ID indicado",
                    content = @Content()
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CarritoDTO> buscarCarritoPorId(
            @Parameter(
                    description = "ID del registro del carrito que se desea consultar",
                    example = "1"
            )
            @PathVariable Long id) {

        return carritoService.buscarCarritoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Agregar producto al carrito",
            description = "Crea un nuevo registro en el carrito. El producto debe existir, estar disponible y tener stock suficiente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Producto agregado al carrito correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos, campos obligatorios faltantes o producto no válido",
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
                                                        "idProducto": "El ID del PRODUCTO es OBLIGATORIO"
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
                                    ),
                                    @ExampleObject(
                                            name = "Stock insuficiente",
                                            value = """
                                                    {
                                                      "timestamp": "2026-06-23T12:00:00",
                                                      "status": 400,
                                                      "error": "ERROR DE SOLICITUD",
                                                      "mensaje": "No hay stock suficiente para el producto con ID 1"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping
    public ResponseEntity<CarritoDTO> crearCarrito(
            @Valid @RequestBody CarritoDTO carritoDTO) {

        CarritoDTO nuevoCarrito = carritoService.guardarCarrito(carritoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCarrito);
    }

    @Operation(
            summary = "Actualizar producto del carrito",
            description = "Actualiza la cantidad o estado de un producto existente en el carrito."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Registro del carrito actualizado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos, campos obligatorios faltantes o producto no válido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "object"),
                            examples = @ExampleObject(
                                    name = "Error de validación",
                                    value = """
                                            {
                                              "timestamp": "2026-06-23T12:00:00",
                                              "status": 400,
                                              "error": "ERROR VALIDACIÓN",
                                              "mensajes": {
                                                "cantidad": "La CANTIDAD mínima es 1"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe un registro del carrito con el ID indicado",
                    content = @Content()
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<CarritoDTO> actualizarCarrito(
            @Parameter(
                    description = "ID del registro del carrito que se desea actualizar",
                    example = "1"
            )
            @PathVariable Long id,
            @Valid @RequestBody CarritoDTO carritoDTO) {

        if (!carritoService.existeCarritoPorId(id)) {
            return ResponseEntity.notFound().build();
        }

        carritoDTO.setIdCarrito(id);
        CarritoDTO carritoActualizado = carritoService.actualizarCarrito(carritoDTO);
        return ResponseEntity.ok(carritoActualizado);
    }

    @Operation(
            summary = "Eliminar registro del carrito",
            description = "Elimina un producto específico del carrito según su identificador."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Registro del carrito eliminado correctamente",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe un registro del carrito con el ID indicado",
                    content = @Content()
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCarrito(
            @Parameter(
                    description = "ID del registro del carrito que se desea eliminar",
                    example = "1"
            )
            @PathVariable Long id) {

        if (!carritoService.existeCarritoPorId(id)) {
            return ResponseEntity.notFound().build();
        }

        carritoService.eliminarCarrito(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Listar carrito por cliente",
            description = "Obtiene todos los productos del carrito asociados a un cliente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Carrito del cliente obtenido correctamente"
            )
    })
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<CarritoDTO>> listarCarritosPorCliente(
            @Parameter(description = "ID del cliente", example = "1")
            @PathVariable Long idCliente) {

        List<CarritoDTO> carritos = carritoService.listarCarritosPorCliente(idCliente);
        return ResponseEntity.ok(carritos);
    }

    @Operation(
            summary = "Listar carritos por producto",
            description = "Obtiene los registros de carrito asociados a un producto específico."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Registros asociados al producto obtenidos correctamente"
            )
    })
    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<CarritoDTO>> listarCarritosPorProducto(
            @Parameter(description = "ID del producto", example = "1")
            @PathVariable Long idProducto) {

        List<CarritoDTO> carritos = carritoService.listarCarritosPorProducto(idProducto);
        return ResponseEntity.ok(carritos);
    }

    @Operation(
            summary = "Listar carrito activo por cliente",
            description = "Obtiene los productos activos del carrito de un cliente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Carrito activo obtenido correctamente"
            )
    })
    @GetMapping("/cliente/{idCliente}/activos")
    public ResponseEntity<List<CarritoDTO>> listarCarritosActivosPorCliente(
            @Parameter(description = "ID del cliente", example = "1")
            @PathVariable Long idCliente) {

        List<CarritoDTO> carritos = carritoService.listarCarritosActivosPorCliente(idCliente);
        return ResponseEntity.ok(carritos);
    }

    @Operation(
            summary = "Calcular total del carrito",
            description = "Calcula el total de los productos activos del carrito de un cliente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Total del carrito calculado correctamente"
            )
    })
    @GetMapping("/cliente/{idCliente}/total")
    public ResponseEntity<Map<String, Double>> calcularTotalCarritoPorCliente(
            @Parameter(description = "ID del cliente", example = "1")
            @PathVariable Long idCliente) {

        Double total = carritoService.calcularTotalCarritoPorCliente(idCliente);
        return ResponseEntity.ok(Map.of("total", total));
    }

    @Operation(
            summary = "Vaciar carrito por cliente",
            description = "Elimina todos los productos del carrito asociados a un cliente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Carrito del cliente eliminado correctamente",
                    content = @Content()
            )
    })
    @DeleteMapping("/cliente/{idCliente}")
    public ResponseEntity<Void> vaciarCarritoPorCliente(
            @Parameter(description = "ID del cliente", example = "1")
            @PathVariable Long idCliente) {

        carritoService.vaciarCarritoPorCliente(idCliente);
        return ResponseEntity.noContent().build();
    }
}
