package com.example.tiendas_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tienda")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tienda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTienda;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 150)
    private String direccion;

    @Column(nullable = false, length = 50)
    private String comuna;

    @Column(nullable = false, length = 50)
    private String ciudad;

    @Column(nullable = false, length = 50)
    private String region;

    @Column(length = 12)
    private String telefono;

    @Column(length = 300)
    private String personalAsignado;

    @Column(nullable = false, length = 10)
    private String horarioApertura;

    @Column(nullable = false, length = 10)
    private String horarioCierre;

    @Column(nullable = false)
    private Boolean activa;

    @Column(length = 500)
    private String politicasLocales;
}
