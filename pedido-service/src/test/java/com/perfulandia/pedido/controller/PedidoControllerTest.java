package com.perfulandia.pedido.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.perfulandia.pedido.dto.ActualizarEstadoPedidoRequest;
import com.perfulandia.pedido.dto.CrearPedidoRequest;
import com.perfulandia.pedido.dto.DetallePedidoRequest;
import com.perfulandia.pedido.model.EstadoPedido;
import com.perfulandia.pedido.model.Pedido;
import com.perfulandia.pedido.service.PedidoService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoService pedidoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void crearPedido_deberiaRetornarStatus200() throws Exception {
        DetallePedidoRequest detalle = new DetallePedidoRequest();
        detalle.setIdProducto(1L);
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(10000);

        CrearPedidoRequest request = new CrearPedidoRequest();
        request.setIdUsuario(1L);
        request.setIdTienda(1L);
        request.setDetalles(List.of(detalle));

        Pedido pedido = Pedido.builder()
                .idPedido(1L)
                .idUsuario(1L)
                .idTienda(1L)
                .estado(EstadoPedido.PENDIENTE)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .total(20000)
                .build();

        when(pedidoService.crearPedido(any(CrearPedidoRequest.class))).thenReturn(pedido);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPedido").value(1))
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.idTienda").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.total").value(20000));
    }

    @Test
    void crearPedido_conDatosInvalidos_deberiaRetornarStatus400() throws Exception {
        CrearPedidoRequest request = new CrearPedidoRequest();
        request.setIdUsuario(null);
        request.setIdTienda(null);
        request.setDetalles(List.of());

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarPedidos_deberiaRetornarStatus200() throws Exception {
        Pedido pedido1 = Pedido.builder()
                .idPedido(1L)
                .idUsuario(1L)
                .idTienda(1L)
                .estado(EstadoPedido.PENDIENTE)
                .total(10000)
                .build();

        Pedido pedido2 = Pedido.builder()
                .idPedido(2L)
                .idUsuario(2L)
                .idTienda(1L)
                .estado(EstadoPedido.CONFIRMADO)
                .total(20000)
                .build();

        when(pedidoService.listarPedidos()).thenReturn(List.of(pedido1, pedido2));

        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].idPedido").value(1))
                .andExpect(jsonPath("$[1].idPedido").value(2));
    }

    @Test
    void buscarPorId_deberiaRetornarStatus200() throws Exception {
        Pedido pedido = Pedido.builder()
                .idPedido(1L)
                .idUsuario(1L)
                .idTienda(1L)
                .estado(EstadoPedido.PENDIENTE)
                .total(15000)
                .build();

        when(pedidoService.buscarPorId(1L)).thenReturn(pedido);

        mockMvc.perform(get("/api/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPedido").value(1))
                .andExpect(jsonPath("$.total").value(15000));
    }

    @Test
    void buscarPorUsuario_deberiaRetornarStatus200() throws Exception {
        Pedido pedido = Pedido.builder()
                .idPedido(1L)
                .idUsuario(5L)
                .idTienda(1L)
                .estado(EstadoPedido.PENDIENTE)
                .total(12000)
                .build();

        when(pedidoService.buscarPorUsuario(5L)).thenReturn(List.of(pedido));

        mockMvc.perform(get("/api/pedidos/usuario/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUsuario").value(5));
    }

    @Test
    void buscarPorTienda_deberiaRetornarStatus200() throws Exception {
        Pedido pedido = Pedido.builder()
                .idPedido(1L)
                .idUsuario(1L)
                .idTienda(3L)
                .estado(EstadoPedido.PENDIENTE)
                .total(18000)
                .build();

        when(pedidoService.buscarPorTienda(3L)).thenReturn(List.of(pedido));

        mockMvc.perform(get("/api/pedidos/tienda/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idTienda").value(3));
    }

    @Test
    void buscarPorEstado_deberiaRetornarStatus200() throws Exception {
        Pedido pedido = Pedido.builder()
                .idPedido(1L)
                .idUsuario(1L)
                .idTienda(1L)
                .estado(EstadoPedido.CONFIRMADO)
                .total(30000)
                .build();

        when(pedidoService.buscarPorEstado(EstadoPedido.CONFIRMADO)).thenReturn(List.of(pedido));

        mockMvc.perform(get("/api/pedidos/estado/CONFIRMADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("CONFIRMADO"));
    }

    @Test
    void actualizarEstado_deberiaRetornarStatus200() throws Exception {
        ActualizarEstadoPedidoRequest request = new ActualizarEstadoPedidoRequest();
        request.setEstado(EstadoPedido.CONFIRMADO);

        Pedido pedido = Pedido.builder()
                .idPedido(1L)
                .idUsuario(1L)
                .idTienda(1L)
                .estado(EstadoPedido.CONFIRMADO)
                .total(10000)
                .build();

        when(pedidoService.actualizarEstado(eq(1L), any(ActualizarEstadoPedidoRequest.class))).thenReturn(pedido);

        mockMvc.perform(put("/api/pedidos/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"));
    }

    @Test
    void cancelarPedido_deberiaRetornarStatus200() throws Exception {
        Pedido pedido = Pedido.builder()
                .idPedido(1L)
                .idUsuario(1L)
                .idTienda(1L)
                .estado(EstadoPedido.CANCELADO)
                .total(10000)
                .build();

        when(pedidoService.cancelarPedido(1L)).thenReturn(pedido);

        mockMvc.perform(put("/api/pedidos/1/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADO"));
    }
}