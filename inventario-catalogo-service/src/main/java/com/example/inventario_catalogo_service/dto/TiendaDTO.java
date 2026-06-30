package com.example.inventario_catalogo_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TiendaDTO {

    private Long idTienda;
    private String nombre;
    private String direccion;
    private String comuna;
    private String ciudad;
    private String region;
    private String telefono;
    private String personalAsignado;
    private String horarioApertura;
    private String horarioCierre;
    private Boolean activa;
    private String politicasLocales;
}
