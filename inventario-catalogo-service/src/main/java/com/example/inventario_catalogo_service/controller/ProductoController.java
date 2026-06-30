package com.example.inventario_catalogo_service.controller;

import com.example.inventario_catalogo_service.dto.ProductoDTO;
import com.example.inventario_catalogo_service.service.ProductoService;
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
@RequestMapping("/api/v1/productos")
@Tag(
        name = "Gestión de Productos",
        description = "Endpoints para registrar, consultar, actualizar, eliminar y filtrar productos del catálogo de Perfulandia."
)
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @Operation(
            summary = "Listar productos",
            description = "Obtiene todos los productos registrados en el catálogo de Perfulandia."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de productos obtenida correctamente"
            )
    })
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listarProductos() {
        List<ProductoDTO> productos = productoService.listarProductos();
        return ResponseEntity.ok(productos);
    }

    @Operation(
            summary = "Buscar producto por ID",
            description = "Obtiene la información de un producto específico usando su identificador."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto encontrado correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe un producto con el ID indicado",
                    content = @Content()
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> buscarProductoPorId(
            @Parameter(
                    description = "ID del producto que se desea consultar",
                    example = "1"
            )
            @PathVariable Long id) {

        return productoService.buscarProductoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Registrar producto",
            description = "Crea un nuevo producto en el catálogo de Perfulandia. El producto debe estar asociado a una tienda existente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Producto creado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos, campos obligatorios faltantes o tienda inexistente",
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
                                                        "nombre": "El NOMBRE del PRODUCTO es OBLIGATORIO"
                                                      }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Tienda inexistente",
                                            value = """
                                                    {
                                                      "timestamp": "2026-06-23T12:00:00",
                                                      "status": 400,
                                                      "error": "ERROR DE SOLICITUD",
                                                      "mensaje": "La tienda con ID 99 no existe"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping
    public ResponseEntity<ProductoDTO> crearProducto(
            @Valid @RequestBody ProductoDTO productoDTO) {

        ProductoDTO nuevoProducto = productoService.guardarProducto(productoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    @Operation(
            summary = "Actualizar producto",
            description = "Actualiza todos los datos de un producto existente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto actualizado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos, campos obligatorios faltantes o tienda inexistente",
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
                                                        "nombre": "El NOMBRE del PRODUCTO es OBLIGATORIO"
                                                      }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Tienda inexistente",
                                            value = """
                                                    {
                                                      "timestamp": "2026-06-23T12:00:00",
                                                      "status": 400,
                                                      "error": "ERROR DE SOLICITUD",
                                                      "mensaje": "La tienda con ID 99 no existe"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe un producto con el ID indicado",
                    content = @Content()
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizarProducto(
            @Parameter(
                    description = "ID del producto que se desea actualizar",
                    example = "1"
            )
            @PathVariable Long id,
            @Valid @RequestBody ProductoDTO productoDTO) {

        if (!productoService.existeProductoPorId(id)) {
            return ResponseEntity.notFound().build();
        }

        productoDTO.setIdProducto(id);
        ProductoDTO productoActualizado = productoService.guardarProducto(productoDTO);
        return ResponseEntity.ok(productoActualizado);
    }

    @Operation(
            summary = "Eliminar producto",
            description = "Elimina un producto existente según su identificador."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Producto eliminado correctamente",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe un producto con el ID indicado",
                    content = @Content()
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(
            @Parameter(
                    description = "ID del producto que se desea eliminar",
                    example = "1"
            )
            @PathVariable Long id) {

        if (!productoService.existeProductoPorId(id)) {
            return ResponseEntity.notFound().build();
        }

        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Listar productos por tienda",
            description = "Obtiene todos los productos asociados a una tienda específica."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Productos de la tienda obtenidos correctamente"
            )
    })
    @GetMapping("/tienda/{idTienda}")
    public ResponseEntity<List<ProductoDTO>> listarProductosPorTienda(
            @Parameter(
                    description = "ID de la tienda asociada a los productos",
                    example = "1"
            )
            @PathVariable Long idTienda) {

        List<ProductoDTO> productos = productoService.listarProductosPorTienda(idTienda);
        return ResponseEntity.ok(productos);
    }

    @Operation(
            summary = "Listar productos disponibles",
            description = "Obtiene solo los productos marcados como disponibles en el catálogo."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Productos disponibles obtenidos correctamente"
            )
    })
    @GetMapping("/disponibles")
    public ResponseEntity<List<ProductoDTO>> listarProductosDisponibles() {
        List<ProductoDTO> productos = productoService.listarProductosDisponibles();
        return ResponseEntity.ok(productos);
    }

    @Operation(
            summary = "Listar productos por categoría",
            description = "Obtiene los productos que pertenecen a una categoría específica."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Productos de la categoría obtenidos correctamente"
            )
    })
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProductoDTO>> listarProductosPorCategoria(
            @Parameter(
                    description = "Categoría de productos que se desea consultar",
                    example = "CUIDADO PERSONAL"
            )
            @PathVariable String categoria) {

        List<ProductoDTO> productos = productoService.listarProductosPorCategoria(categoria);
        return ResponseEntity.ok(productos);
    }
}