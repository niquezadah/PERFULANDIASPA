package com.perfulandia.pedido.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CrearPedidoRequest {

    @NotNull(message = "El id del usuario es obligatorio")
    private Long idUsuario;

    @NotNull(message = "El id de la tienda es obligatorio")
    private Long idTienda;

    @NotEmpty(message = "El pedido debe tener al menos un producto")
    @Valid
    private List<DetallePedidoRequest> detalles;
}