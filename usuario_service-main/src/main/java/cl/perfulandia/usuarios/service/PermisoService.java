package cl.perfulandia.usuarios.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cl.perfulandia.usuarios.model.Permiso;
import cl.perfulandia.usuarios.repository.PermisoRepository;

@Service
public class PermisoService {

    private final PermisoRepository permisoRepository;

    public PermisoService(PermisoRepository permisoRepository) {
        this.permisoRepository = permisoRepository;
    }

    public List<Permiso> listarPermisos() {
        return permisoRepository.findAll();
    }

    public Optional<Permiso> buscarPermisoPorId(Long id) {
        return permisoRepository.findById(id);
    }

    public Permiso guardarPermiso(Permiso permiso) {
        String nombrePermiso = limpiarTexto(permiso.getNombrePermiso());

        if (permisoRepository.existsByNombrePermiso(nombrePermiso)) {
            throw new RuntimeException("Ya existe un permiso con el nombre: " + nombrePermiso);
        }

        permiso.setNombrePermiso(nombrePermiso);
        permiso.setDescripcion(limpiarTextoOpcional(permiso.getDescripcion()));

        return permisoRepository.save(permiso);
    }

    public Permiso actualizarPermiso(Long id, Permiso permisoActualizado) {
        Permiso permisoExistente = permisoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permiso no encontrado con ID: " + id));

        String nuevoNombre = limpiarTexto(permisoActualizado.getNombrePermiso());

        if (!permisoExistente.getNombrePermiso().equalsIgnoreCase(nuevoNombre)
                && permisoRepository.existsByNombrePermiso(nuevoNombre)) {
            throw new RuntimeException("Ya existe un permiso con el nombre: " + nuevoNombre);
        }

        permisoExistente.setNombrePermiso(nuevoNombre);
        permisoExistente.setDescripcion(limpiarTextoOpcional(permisoActualizado.getDescripcion()));

        return permisoRepository.save(permisoExistente);
    }

    public void eliminarPermiso(Long id) {
        if (!permisoRepository.existsById(id)) {
            throw new RuntimeException("Permiso no encontrado con ID: " + id);
        }

        permisoRepository.deleteById(id);
    }

    private String limpiarTexto(String texto) {
        if (texto == null) {
            return null;
        }

        return texto.trim();
    }

    private String limpiarTextoOpcional(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }

        return texto.trim();
    }
}