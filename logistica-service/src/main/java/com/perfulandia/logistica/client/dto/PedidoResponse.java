package com.perfulandia.logistica.client.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PedidoResponse {

    @JsonAlias({"idPedido", "id_pedido"})
    private Long idPedido;

    @JsonAlias({"idUsuario", "id_usuario", "idCliente", "id_cliente"})
    private Long idUsuario;

    @JsonAlias({"idTienda", "id_tienda"})
    private Long idTienda;

    @JsonAlias({"estado", "estadoPedido"})
    private String estado;
}