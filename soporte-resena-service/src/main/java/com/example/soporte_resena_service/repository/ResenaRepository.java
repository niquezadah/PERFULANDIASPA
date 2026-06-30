package com.example.soporte_resena_service.repository;

import com.example.soporte_resena_service.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {

    List<Resena> findByIdProducto(Long idProducto);

    List<Resena> findByActiva(Boolean activa);

    List<Resena> findByCalificacion(Integer calificacion);
}
