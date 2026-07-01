package com.perfulandia.pedido.dto;

import com.perfulandia.pedido.model.EstadoPedido;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActualizarEstadoPedidoRequest {

    @NotNull(message = "El estado del pedido es obligatorio")
    private EstadoPedido estado;
}