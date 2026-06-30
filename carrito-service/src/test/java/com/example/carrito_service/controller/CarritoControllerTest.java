package com.example.carrito_service.controller;

import com.example.carrito_service.dto.CarritoDTO;
import com.example.carrito_service.service.CarritoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarritoController.class)
@ActiveProfiles("test")
class CarritoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarritoService carritoService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void listarCarritos_deberiaRetornar200YListaDeCarritos() throws Exception {
        //given
        CarritoDTO carrito = crearCarritoDTO(1L, true);

        Mockito.when(carritoService.listarCarritos()).thenReturn(List.of(carrito));

        //when //then
        mockMvc.perform(get("/api/v1/carrito"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idCarrito").value(1L))
                .andExpect(jsonPath("$[0].idCliente").value(1L))
                .andExpect(jsonPath("$[0].idProducto").value(1L))
                .andExpect(jsonPath("$[0].nombreProducto").value("EAU DE PARFUM ROSAS DEL SUR"))
                .andExpect(jsonPath("$[0].cantidad").value(2))
                .andExpect(jsonPath("$[0].subtotal").value(49980.0))
                .andExpect(jsonPath("$[0].activo").value(true));

        Mockito.verify(carritoService).listarCarritos();
    }

    @Test
    void buscarCarritoPorId_cuandoExiste_deberiaRetornar200YCarrito() throws Exception {
        //given
        Long id = 1L;
        CarritoDTO carrito = crearCarritoDTO(id, true);

        Mockito.when(carritoService.buscarCarritoPorId(id)).thenReturn(Optional.of(carrito));

        //when //then
        mockMvc.perform(get("/api/v1/carrito/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCarrito").value(id))
                .andExpect(jsonPath("$.idCliente").value(1L))
                .andExpect(jsonPath("$.nombreProducto").value("EAU DE PARFUM ROSAS DEL SUR"))
                .andExpect(jsonPath("$.activo").value(true));

        Mockito.verify(carritoService).buscarCarritoPorId(id);
    }

    @Test
    void buscarCarritoPorId_cuandoNoExiste_deberiaRetornar404() throws Exception {
        //given
        Long id = 99L;

        Mockito.when(carritoService.buscarCarritoPorId(id)).thenReturn(Optional.empty());

        //when //then
        mockMvc.perform(get("/api/v1/carrito/{id}", id))
                .andExpect(status().isNotFound());

        Mockito.verify(carritoService).buscarCarritoPorId(id);
    }

    @Test
    void crearCarrito_conDatosValidos_deberiaRetornar201YCarritoCreado() throws Exception {
        //given
        CarritoDTO carritoEntrada = crearCarritoDTO(null, true);
        CarritoDTO carritoCreado = crearCarritoDTO(1L, true);

        Mockito.when(carritoService.guardarCarrito(any(CarritoDTO.class))).thenReturn(carritoCreado);

        //when //then
        mockMvc.perform(post("/api/v1/carrito")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carritoEntrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCarrito").value(1L))
                .andExpect(jsonPath("$.idCliente").value(1L))
                .andExpect(jsonPath("$.idProducto").value(1L))
                .andExpect(jsonPath("$.nombreProducto").value("EAU DE PARFUM ROSAS DEL SUR"))
                .andExpect(jsonPath("$.subtotal").value(49980.0));

        Mockito.verify(carritoService).guardarCarrito(any(CarritoDTO.class));
    }

    @Test
    void crearCarrito_conDatosInvalidos_deberiaRetornar400() throws Exception {
        //given
        String body = """
                {
                    "idCliente": 1,
                    "idProducto": 1,
                    "cantidad": 0,
                    "activo": true
                }
                """;

        //when //then
        mockMvc.perform(post("/api/v1/carrito")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("ERROR VALIDACIÓN"))
                .andExpect(jsonPath("$.mensajes.cantidad").value("La CANTIDAD mínima es 1"));

        Mockito.verify(carritoService, never()).guardarCarrito(any(CarritoDTO.class));
    }

    @Test
    void crearCarrito_cuandoServiceLanzaRuntimeException_deberiaRetornar400() throws Exception {
        //given
        CarritoDTO carritoEntrada = crearCarritoDTO(null, true);

        Mockito.when(carritoService.guardarCarrito(any(CarritoDTO.class)))
                .thenThrow(new RuntimeException("El producto con ID 1 no existe"));

        //when //then
        mockMvc.perform(post("/api/v1/carrito")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carritoEntrada)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("ERROR DE SOLICITUD"))
                .andExpect(jsonPath("$.mensaje").value("El producto con ID 1 no existe"));

        Mockito.verify(carritoService).guardarCarrito(any(CarritoDTO.class));
    }

    @Test
    void actualizarCarrito_cuandoExiste_deberiaRetornar200YCarritoActualizado() throws Exception {
        //given
        Long id = 1L;
        CarritoDTO carritoEntrada = crearCarritoDTO(null, true);
        CarritoDTO carritoActualizado = crearCarritoDTO(id, true);

        Mockito.when(carritoService.existeCarritoPorId(id)).thenReturn(true);
        Mockito.when(carritoService.actualizarCarrito(any(CarritoDTO.class))).thenReturn(carritoActualizado);

        //when //then
        mockMvc.perform(put("/api/v1/carrito/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carritoEntrada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCarrito").value(id))
                .andExpect(jsonPath("$.cantidad").value(2))
                .andExpect(jsonPath("$.subtotal").value(49980.0));

        Mockito.verify(carritoService).existeCarritoPorId(id);
        Mockito.verify(carritoService).actualizarCarrito(any(CarritoDTO.class));
    }

    @Test
    void actualizarCarrito_cuandoNoExiste_deberiaRetornar404() throws Exception {
        //given
        Long id = 99L;
        CarritoDTO carritoEntrada = crearCarritoDTO(null, true);

        Mockito.when(carritoService.existeCarritoPorId(id)).thenReturn(false);

        //when //then
        mockMvc.perform(put("/api/v1/carrito/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carritoEntrada)))
                .andExpect(status().isNotFound());

        Mockito.verify(carritoService).existeCarritoPorId(id);
        Mockito.verify(carritoService, never()).actualizarCarrito(any(CarritoDTO.class));
    }

    @Test
    void actualizarCarrito_conDatosInvalidos_deberiaRetornar400() throws Exception {
        //given
        String body = """
                {
                    "idCliente": 1,
                    "idProducto": 1,
                    "cantidad": 0,
                    "activo": true
                }
                """;

        //when //then
        mockMvc.perform(put("/api/v1/carrito/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("ERROR VALIDACIÓN"))
                .andExpect(jsonPath("$.mensajes.cantidad").value("La CANTIDAD mínima es 1"));

        Mockito.verify(carritoService, never()).existeCarritoPorId(1L);
        Mockito.verify(carritoService, never()).actualizarCarrito(any(CarritoDTO.class));
    }

    @Test
    void eliminarCarrito_cuandoExiste_deberiaRetornar204() throws Exception {
        //given
        Long id = 1L;

        Mockito.when(carritoService.existeCarritoPorId(id)).thenReturn(true);

        //when //then
        mockMvc.perform(delete("/api/v1/carrito/{id}", id))
                .andExpect(status().isNoContent());

        Mockito.verify(carritoService).existeCarritoPorId(id);
        Mockito.verify(carritoService).eliminarCarrito(id);
    }

    @Test
    void eliminarCarrito_cuandoNoExiste_deberiaRetornar404() throws Exception {
        //given
        Long id = 99L;

        Mockito.when(carritoService.existeCarritoPorId(id)).thenReturn(false);

        //when //then
        mockMvc.perform(delete("/api/v1/carrito/{id}", id))
                .andExpect(status().isNotFound());

        Mockito.verify(carritoService).existeCarritoPorId(id);
        Mockito.verify(carritoService, never()).eliminarCarrito(id);
    }

    @Test
    void listarCarritosPorCliente_deberiaRetornar200YCarritosDelCliente() throws Exception {
        //given
        Long idCliente = 1L;
        CarritoDTO carrito = crearCarritoDTO(1L, true);

        Mockito.when(carritoService.listarCarritosPorCliente(idCliente)).thenReturn(List.of(carrito));

        //when //then
        mockMvc.perform(get("/api/v1/carrito/cliente/{idCliente}", idCliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idCliente").value(idCliente))
                .andExpect(jsonPath("$[0].nombreProducto").value("EAU DE PARFUM ROSAS DEL SUR"));

        Mockito.verify(carritoService).listarCarritosPorCliente(idCliente);
    }

    @Test
    void listarCarritosPorProducto_deberiaRetornar200YCarritosDelProducto() throws Exception {
        //given
        Long idProducto = 1L;
        CarritoDTO carrito = crearCarritoDTO(1L, true);

        Mockito.when(carritoService.listarCarritosPorProducto(idProducto)).thenReturn(List.of(carrito));

        //when //then
        mockMvc.perform(get("/api/v1/carrito/producto/{idProducto}", idProducto))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idProducto").value(idProducto))
                .andExpect(jsonPath("$[0].nombreProducto").value("EAU DE PARFUM ROSAS DEL SUR"));

        Mockito.verify(carritoService).listarCarritosPorProducto(idProducto);
    }

    @Test
    void listarCarritosActivosPorCliente_deberiaRetornar200YCarritosActivos() throws Exception {
        //given
        Long idCliente = 1L;
        CarritoDTO carrito = crearCarritoDTO(1L, true);

        Mockito.when(carritoService.listarCarritosActivosPorCliente(idCliente)).thenReturn(List.of(carrito));

        //when //then
        mockMvc.perform(get("/api/v1/carrito/cliente/{idCliente}/activos", idCliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idCliente").value(idCliente))
                .andExpect(jsonPath("$[0].activo").value(true));

        Mockito.verify(carritoService).listarCarritosActivosPorCliente(idCliente);
    }

    @Test
    void calcularTotalCarritoPorCliente_deberiaRetornar200YTotal() throws Exception {
        //given
        Long idCliente = 1L;

        Mockito.when(carritoService.calcularTotalCarritoPorCliente(idCliente)).thenReturn(49980.0);

        //when //then
        mockMvc.perform(get("/api/v1/carrito/cliente/{idCliente}/total", idCliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(49980.0));

        Mockito.verify(carritoService).calcularTotalCarritoPorCliente(idCliente);
    }

    @Test
    void vaciarCarritoPorCliente_deberiaRetornar204() throws Exception {
        //given
        Long idCliente = 1L;

        //when //then
        mockMvc.perform(delete("/api/v1/carrito/cliente/{idCliente}", idCliente))
                .andExpect(status().isNoContent());

        Mockito.verify(carritoService).vaciarCarritoPorCliente(idCliente);
    }

    private CarritoDTO crearCarritoDTO(Long id, Boolean activo) {
        return new CarritoDTO(
                id,
                1L,
                1L,
                "EAU DE PARFUM ROSAS DEL SUR",
                2,
                24990.0,
                49980.0,
                activo
        );
    }
}
