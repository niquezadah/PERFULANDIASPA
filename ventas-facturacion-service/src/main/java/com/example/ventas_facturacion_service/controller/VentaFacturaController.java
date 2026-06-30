package com.example.ventas_facturacion_service.controller;

import com.example.ventas_facturacion_service.dto.EstadoVentaDTO;
import com.example.ventas_facturacion_service.dto.VentaFacturaDTO;
import com.example.ventas_facturacion_service.service.VentaFacturaService;
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
@RequestMapping("/api/v1/ventas-facturacion")
@Tag(
        name = "Gestión de Ventas y Facturación",
        description = "Endpoints para registrar ventas, calcular totales desde carrito y generar facturas de Perfulandia."
)
public class VentaFacturaController {

    private final VentaFacturaService ventaFacturaService;

    public VentaFacturaController(VentaFacturaService ventaFacturaService) {
        this.ventaFacturaService = ventaFacturaService;
    }

    @Operation(
            summary = "Listar ventas y facturas",
            description = "Obtiene todas las ventas y facturas registradas en Perfulandia."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de ventas y facturas obtenida correctamente"
            )
    })
    @GetMapping
    public ResponseEntity<List<VentaFacturaDTO>> listarVentasFacturas() {
        List<VentaFacturaDTO> ventas = ventaFacturaService.listarVentasFacturas();
        return ResponseEntity.ok(ventas);
    }

    @Operation(
            summary = "Buscar venta por ID",
            description = "Obtiene la información de una venta y factura usando su identificador."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Venta encontrada correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe una venta con el ID indicado",
                    content = @Content()
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<VentaFacturaDTO> buscarVentaFacturaPorId(
            @Parameter(
                    description = "ID de la venta que se desea consultar",
                    example = "1"
            )
            @PathVariable Long id) {

        return ventaFacturaService.buscarVentaFacturaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Registrar venta y generar factura",
            description = "Registra una venta para un cliente. El total se obtiene desde el carrito activo del cliente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Venta registrada y factura generada correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos, campos obligatorios faltantes o carrito sin total válido",
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
                                                        "idCliente": "El ID del CLIENTE es OBLIGATORIO"
                                                      }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Carrito vacío",
                                            value = """
                                                    {
                                                      "timestamp": "2026-06-23T12:00:00",
                                                      "status": 400,
                                                      "error": "ERROR DE SOLICITUD",
                                                      "mensaje": "El carrito del cliente con ID 1 está vacío"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping
    public ResponseEntity<VentaFacturaDTO> crearVentaFactura(
            @Valid @RequestBody VentaFacturaDTO ventaFacturaDTO) {

        VentaFacturaDTO nuevaVenta = ventaFacturaService.guardarVentaFactura(ventaFacturaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaVenta);
    }

    @Operation(
            summary = "Actualizar venta y factura",
            description = "Actualiza los datos principales de una venta existente y recalcula el total desde el carrito del cliente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Venta actualizada correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o carrito sin total válido",
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
                                                "metodoPago": "El MÉTODO de PAGO es OBLIGATORIO"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe una venta con el ID indicado",
                    content = @Content()
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<VentaFacturaDTO> actualizarVentaFactura(
            @Parameter(
                    description = "ID de la venta que se desea actualizar",
                    example = "1"
            )
            @PathVariable Long id,
            @Valid @RequestBody VentaFacturaDTO ventaFacturaDTO) {

        if (!ventaFacturaService.existeVentaFacturaPorId(id)) {
            return ResponseEntity.notFound().build();
        }

        ventaFacturaDTO.setIdVenta(id);
        VentaFacturaDTO ventaActualizada = ventaFacturaService.actualizarVentaFactura(ventaFacturaDTO);
        return ResponseEntity.ok(ventaActualizada);
    }

    @Operation(
            summary = "Eliminar venta",
            description = "Elimina una venta y factura existente según su identificador."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Venta eliminada correctamente",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe una venta con el ID indicado",
                    content = @Content()
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVentaFactura(
            @Parameter(
                    description = "ID de la venta que se desea eliminar",
                    example = "1"
            )
            @PathVariable Long id) {

        if (!ventaFacturaService.existeVentaFacturaPorId(id)) {
            return ResponseEntity.notFound().build();
        }

        ventaFacturaService.eliminarVentaFactura(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Listar ventas por cliente",
            description = "Obtiene todas las ventas asociadas a un cliente específico."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ventas del cliente obtenidas correctamente"
            )
    })
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<VentaFacturaDTO>> listarVentasPorCliente(
            @Parameter(description = "ID del cliente", example = "1")
            @PathVariable Long idCliente) {

        List<VentaFacturaDTO> ventas = ventaFacturaService.listarVentasPorCliente(idCliente);
        return ResponseEntity.ok(ventas);
    }

    @Operation(
            summary = "Listar ventas por estado",
            description = "Obtiene ventas según su estado, por ejemplo PAGADA, PENDIENTE o ANULADA."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ventas filtradas por estado obtenidas correctamente"
            )
    })
    @GetMapping("/estado/{estadoVenta}")
    public ResponseEntity<List<VentaFacturaDTO>> listarVentasPorEstado(
            @Parameter(description = "Estado de la venta", example = "PAGADA")
            @PathVariable String estadoVenta) {

        List<VentaFacturaDTO> ventas = ventaFacturaService.listarVentasPorEstado(estadoVenta);
        return ResponseEntity.ok(ventas);
    }

    @Operation(
            summary = "Listar ventas facturadas",
            description = "Obtiene todas las ventas que tienen factura emitida."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ventas facturadas obtenidas correctamente"
            )
    })
    @GetMapping("/facturadas")
    public ResponseEntity<List<VentaFacturaDTO>> listarVentasFacturadas() {
        List<VentaFacturaDTO> ventas = ventaFacturaService.listarVentasFacturadas();
        return ResponseEntity.ok(ventas);
    }

    @Operation(
            summary = "Actualizar estado de venta",
            description = "Actualiza solo el estado de una venta existente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Estado de venta actualizado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Estado inválido o venta inexistente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "object"),
                            examples = @ExampleObject(
                                    name = "Error de venta inexistente",
                                    value = """
                                            {
                                              "timestamp": "2026-06-23T12:00:00",
                                              "status": 400,
                                              "error": "ERROR DE SOLICITUD",
                                              "mensaje": "La venta con ID 99 no existe"
                                            }
                                            """
                            )
                    )
            )
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<VentaFacturaDTO> actualizarEstadoVenta(
            @Parameter(description = "ID de la venta", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody EstadoVentaDTO estadoVentaDTO) {

        VentaFacturaDTO ventaActualizada = ventaFacturaService.actualizarEstadoVenta(id, estadoVentaDTO);
        return ResponseEntity.ok(ventaActualizada);
    }
}
