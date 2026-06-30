package com.example.carrito_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {

    private Long idProducto;
    private String nombre;
    private String descripcion;
    private String categoria;
    private Integer stock;
    private Double precio;
    private Boolean disponible;
    private Long idTienda;
}
