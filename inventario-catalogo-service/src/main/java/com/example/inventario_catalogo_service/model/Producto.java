package com.example.inventario_catalogo_service.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProducto;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false, length = 80)
    private String categoria;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private Boolean disponible;

    @Column(nullable = false)
    private Long idTienda;
}
