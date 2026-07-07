package com.perfulandia.logistica.controller;

import com.perfulandia.logistica.dto.ActualizarEstadoEnvioRequest;
import com.perfulandia.logistica.dto.CrearEnvioRequest;
import com.perfulandia.logistica.model.Envio;
import com.perfulandia.logistica.model.EstadoEnvio;
import com.perfulandia.logistica.service.EnvioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/logistica/envios")
public class EnvioController {

    private final EnvioService envioService;

    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    @PostMapping
    public ResponseEntity<?> crearEnvio(@Valid @RequestBody CrearEnvioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(envioService.crearEnvio(request));
    }

    @GetMapping
    public ResponseEntity<?> listarEnvios() {
        return ResponseEntity.ok(envioService.listarEnvios());
    }

    @GetMapping("/{idEnvio}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long idEnvio) {
        return ResponseEntity.ok(envioService.buscarPorId(idEnvio));
    }

    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<?> buscarPorPedido(@PathVariable Long idPedido) {
        return ResponseEntity.ok(envioService.buscarPorPedido(idPedido));
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<?> listarPorCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(envioService.listarPorCliente(idCliente));
    }

    @GetMapping("/estado/{estadoEnvio}")
    public ResponseEntity<?> listarPorEstado(@PathVariable EstadoEnvio estadoEnvio) {
        return ResponseEntity.ok(envioService.listarPorEstado(estadoEnvio));
    }

    @GetMapping("/tienda/{idTiendaOrigen}")
    public ResponseEntity<?> listarPorTiendaOrigen(@PathVariable Long idTiendaOrigen) {
        return ResponseEntity.ok(envioService.listarPorTiendaOrigen(idTiendaOrigen));
    }

    @PutMapping("/{idEnvio}/estado")
    public ResponseEntity<?> actualizarEstado(
            @PathVariable Long idEnvio,
            @Valid @RequestBody ActualizarEstadoEnvioRequest request
    ) {
        return ResponseEntity.ok(envioService.actualizarEstado(idEnvio, request));
    }

    @DeleteMapping("/{idEnvio}")
    public ResponseEntity<?> eliminarEnvio(@PathVariable Long idEnvio) {
        envioService.eliminarEnvio(idEnvio);
        return ResponseEntity.noContent().build();
    }
}