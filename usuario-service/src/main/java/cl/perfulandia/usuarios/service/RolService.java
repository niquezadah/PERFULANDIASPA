package cl.perfulandia.usuarios.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cl.perfulandia.usuarios.model.Permiso;
import cl.perfulandia.usuarios.model.Rol;
import cl.perfulandia.usuarios.repository.PermisoRepository;
import cl.perfulandia.usuarios.repository.RolRepository;

@Service
public class RolService {

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;

    public RolService(RolRepository rolRepository, PermisoRepository permisoRepository) {
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
    }

    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    public Optional<Rol> buscarRolPorId(Long id) {
        return rolRepository.findById(id);
    }

    public Rol guardarRol(Rol rol) {
        String nombreRol = limpiarTexto(rol.getNombreRol());

        if (rolRepository.existsByNombreRol(nombreRol)) {
            throw new RuntimeException("Ya existe un rol con el nombre: " + nombreRol);
        }

        rol.setNombreRol(nombreRol);
        rol.setDescripcion(limpiarTextoOpcional(rol.getDescripcion()));

        return rolRepository.save(rol);
    }

    public Rol actualizarRol(Long id, Rol rolActualizado) {
        Rol rolExistente = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));

        String nuevoNombre = limpiarTexto(rolActualizado.getNombreRol());

        if (!rolExistente.getNombreRol().equalsIgnoreCase(nuevoNombre)
                && rolRepository.existsByNombreRol(nuevoNombre)) {
            throw new RuntimeException("Ya existe un rol con el nombre: " + nuevoNombre);
        }

        rolExistente.setNombreRol(nuevoNombre);
        rolExistente.setDescripcion(limpiarTextoOpcional(rolActualizado.getDescripcion()));

        return rolRepository.save(rolExistente);
    }

    public Rol agregarPermisoARol(Long idRol, Long idPermiso) {
        Rol rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + idRol));

        Permiso permiso = permisoRepository.findById(idPermiso)
                .orElseThrow(() -> new RuntimeException("Permiso no encontrado con ID: " + idPermiso));

        rol.getPermisos().add(permiso);

        return rolRepository.save(rol);
    }

    public void eliminarRol(Long id) {
        if (!rolRepository.existsById(id)) {
            throw new RuntimeException("Rol no encontrado con ID: " + id);
        }

        rolRepository.deleteById(id);
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