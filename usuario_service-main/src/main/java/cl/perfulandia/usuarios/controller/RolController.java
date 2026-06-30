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

import cl.perfulandia.usuarios.model.Rol;
import cl.perfulandia.usuarios.service.RolService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping
    public ResponseEntity<List<Rol>> listarRoles() {
        return ResponseEntity.ok(rolService.listarRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rol> buscarRolPorId(@PathVariable Long id) {
        return rolService.buscarRolPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Rol> guardarRol(@Valid @RequestBody Rol rol) {
        Rol rolGuardado = rolService.guardarRol(rol);
        return ResponseEntity.ok(rolGuardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rol> actualizarRol(
            @PathVariable Long id,
            @Valid @RequestBody Rol rol) {

        Rol rolActualizado = rolService.actualizarRol(id, rol);
        return ResponseEntity.ok(rolActualizado);
    }

    @PutMapping("/{idRol}/permisos/{idPermiso}")
    public ResponseEntity<Rol> agregarPermisoARol(
            @PathVariable Long idRol,
            @PathVariable Long idPermiso) {

        Rol rolActualizado = rolService.agregarPermisoARol(idRol, idPermiso);
        return ResponseEntity.ok(rolActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRol(@PathVariable Long id) {
        rolService.eliminarRol(id);
        return ResponseEntity.noContent().build();
    }
}