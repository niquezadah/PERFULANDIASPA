package com.example.inventario_catalogo_service.repository;
import com.example.inventario_catalogo_service.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByIdTienda(Long idTienda);

    List<Producto> findByDisponible(Boolean disponible);

    List<Producto> findByCategoria(String categoria);
}
