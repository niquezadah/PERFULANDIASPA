package cl.perfulandia.usuarios.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.perfulandia.usuarios.model.Permiso;
import cl.perfulandia.usuarios.service.PermisoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/permisos")
public class PermisoController {

    private final PermisoService permisoService;

    public PermisoController(PermisoService permisoService) {
        this.permisoService = permisoService;
    }

    @GetMapping
    public ResponseEntity<List<Permiso>> listarPermisos() {
        return ResponseEntity.ok(permisoService.listarPermisos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Permiso> buscarPermisoPorId(@PathVariable Long id) {
        return permisoService.buscarPermisoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Permiso> guardarPermiso(@Valid @RequestBody Permiso permiso) {
        Permiso permisoGuardado = permisoService.guardarPermiso(permiso);
        return ResponseEntity.ok(permisoGuardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Permiso> actualizarPermiso(
            @PathVariable Long id,
            @Valid @RequestBody Permiso permiso) {

        Permiso permisoActualizado = permisoService.actualizarPermiso(id, permiso);
        return ResponseEntity.ok(permisoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPermiso(@PathVariable Long id) {
        permisoService.eliminarPermiso(id);
        return ResponseEntity.noContent().build();
    }
}