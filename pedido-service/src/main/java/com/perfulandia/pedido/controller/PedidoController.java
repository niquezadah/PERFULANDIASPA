package com.perfulandia.pedido.controller;

import com.perfulandia.pedido.dto.ActualizarEstadoPedidoRequest;
import com.perfulandia.pedido.dto.CrearPedidoRequest;
import com.perfulandia.pedido.model.EstadoPedido;
import com.perfulandia.pedido.model.Pedido;
import com.perfulandia.pedido.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<Pedido> crearPedido(@Valid @RequestBody CrearPedidoRequest request) {
        return ResponseEntity.ok(pedidoService.crearPedido(request));
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> listarPedidos() {
        return ResponseEntity.ok(pedidoService.listarPedidos());
    }

    @GetMapping("/{idPedido}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long idPedido) {
        return ResponseEntity.ok(pedidoService.buscarPorId(idPedido));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Pedido>> buscarPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(pedidoService.buscarPorUsuario(idUsuario));
    }

    @GetMapping("/tienda/{idTienda}")
    public ResponseEntity<List<Pedido>> buscarPorTienda(@PathVariable Long idTienda) {
        return ResponseEntity.ok(pedidoService.buscarPorTienda(idTienda));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Pedido>> buscarPorEstado(@PathVariable EstadoPedido estado) {
        return ResponseEntity.ok(pedidoService.buscarPorEstado(estado));
    }

    @PutMapping("/{idPedido}/estado")
    public ResponseEntity<Pedido> actualizarEstado(
            @PathVariable Long idPedido,
            @Valid @RequestBody ActualizarEstadoPedidoRequest request
    ) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(idPedido, request));
    }

    @PutMapping("/{idPedido}/cancelar")
    public ResponseEntity<Pedido> cancelarPedido(@PathVariable Long idPedido) {
        return ResponseEntity.ok(pedidoService.cancelarPedido(idPedido));
    }
}