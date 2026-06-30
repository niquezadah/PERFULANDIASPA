package cl.perfulandia.usuarios.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.perfulandia.usuarios.model.Permiso;

public interface PermisoRepository extends JpaRepository<Permiso, Long> {

    Optional<Permiso> findByNombrePermiso(String nombrePermiso);

    boolean existsByNombrePermiso(String nombrePermiso);
}