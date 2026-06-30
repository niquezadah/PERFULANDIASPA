package com.example.tiendas_service.controller;

import com.example.tiendas_service.dto.ActualizarEstadoTiendaDTO;
import com.example.tiendas_service.dto.TiendaDTO;
import com.example.tiendas_service.service.TiendaService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TiendaController.class)
@ActiveProfiles("test")
class TiendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TiendaService tiendaService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void listarTiendas_deberiaRetornar200YListaDeTiendas() throws Exception {
        //given
        TiendaDTO tienda = crearTiendaDTO(1L, true);

        Mockito.when(tiendaService.listarTiendas()).thenReturn(List.of(tienda));

        //when-then
        mockMvc.perform(get("/api/v1/tiendas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idTienda").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("PERFULANDIA CONCEPCION"))
                .andExpect(jsonPath("$[0].activa").value(true));

        Mockito.verify(tiendaService).listarTiendas();
    }

    @Test
    void buscarTiendaPorId_cuandoExiste_deberiaRetornar200YTienda() throws Exception {
        //given
        Long id = 1L;
        TiendaDTO tienda = crearTiendaDTO(id, true);

        Mockito.when(tiendaService.buscarTiendaPorId(id)).thenReturn(Optional.of(tienda));

        //when-then
        mockMvc.perform(get("/api/v1/tiendas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTienda").value(id))
                .andExpect(jsonPath("$.nombre").value("PERFULANDIA CONCEPCION"))
                .andExpect(jsonPath("$.ciudad").value("CONCEPCION"))
                .andExpect(jsonPath("$.activa").value(true));

        Mockito.verify(tiendaService).buscarTiendaPorId(id);
    }

    @Test
    void buscarTiendaPorId_cuandoNoExiste_deberiaRetornar404() throws Exception {
        //given
        Long id = 99L;

        Mockito.when(tiendaService.buscarTiendaPorId(id)).thenReturn(Optional.empty());

        //when-then
        mockMvc.perform(get("/api/v1/tiendas/{id}", id))
                .andExpect(status().isNotFound());

        Mockito.verify(tiendaService).buscarTiendaPorId(id);
    }

    @Test
    void crearTienda_conDatosValidos_deberiaRetornar201YTiendaCreada() throws Exception {
        //given
        TiendaDTO tiendaEntrada = crearTiendaDTO(null, true);
        TiendaDTO tiendaGuardada = crearTiendaDTO(1L, true);

        Mockito.when(tiendaService.guardarTienda(any(TiendaDTO.class))).thenReturn(tiendaGuardada);

        //when-then
        mockMvc.perform(post("/api/v1/tiendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tiendaEntrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idTienda").value(1L))
                .andExpect(jsonPath("$.nombre").value("PERFULANDIA CONCEPCION"))
                .andExpect(jsonPath("$.region").value("BIO BIO"))
                .andExpect(jsonPath("$.activa").value(true));

        Mockito.verify(tiendaService).guardarTienda(any(TiendaDTO.class));
    }

    @Test
    void crearTienda_conDatosInvalidos_deberiaRetornar400() throws Exception {
        //given
        TiendaDTO tiendaInvalida = crearTiendaDTO(null, true);
        tiendaInvalida.setNombre("");

        //when-then
        mockMvc.perform(post("/api/v1/tiendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tiendaInvalida)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("ERROR VALIDACIÓN"))
                .andExpect(jsonPath("$.mensajes.nombre").value("El NOMBRE de la TIENDA es OBLIGATORIO"));

        Mockito.verify(tiendaService, never()).guardarTienda(any(TiendaDTO.class));
    }

    @Test
    void actualizarTienda_cuandoExiste_deberiaRetornar200YTiendaActualizada() throws Exception {
        //given
        Long id = 1L;
        TiendaDTO tiendaEntrada = crearTiendaDTO(null, true);
        TiendaDTO tiendaActualizada = crearTiendaDTO(id, true);

        Mockito.when(tiendaService.existeTiendaPorId(id)).thenReturn(true);
        Mockito.when(tiendaService.guardarTienda(any(TiendaDTO.class))).thenReturn(tiendaActualizada);

        //when-then
        mockMvc.perform(put("/api/v1/tiendas/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tiendaEntrada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTienda").value(id))
                .andExpect(jsonPath("$.nombre").value("PERFULANDIA CONCEPCION"))
                .andExpect(jsonPath("$.activa").value(true));

        Mockito.verify(tiendaService).existeTiendaPorId(id);
        Mockito.verify(tiendaService).guardarTienda(any(TiendaDTO.class));
    }

    @Test
    void actualizarTienda_cuandoNoExiste_deberiaRetornar404() throws Exception {
        //given
        Long id = 99L;
        TiendaDTO tiendaEntrada = crearTiendaDTO(null, true);

        Mockito.when(tiendaService.existeTiendaPorId(id)).thenReturn(false);

        //when-then
        mockMvc.perform(put("/api/v1/tiendas/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tiendaEntrada)))
                .andExpect(status().isNotFound());

        Mockito.verify(tiendaService).existeTiendaPorId(id);
        Mockito.verify(tiendaService, never()).guardarTienda(any(TiendaDTO.class));
    }

    @Test
    void actualizarEstadoTienda_cuandoExiste_deberiaRetornar200YTiendaActualizada() throws Exception {
        //given
        Long id = 1L;
        TiendaDTO tiendaActualizada = crearTiendaDTO(id, false);

        ActualizarEstadoTiendaDTO estadoDTO = new ActualizarEstadoTiendaDTO();
        estadoDTO.setActiva(false);

        Mockito.when(tiendaService.actualizarEstadoTienda(eq(id), eq(false)))
                .thenReturn(Optional.of(tiendaActualizada));

        //when-then
        mockMvc.perform(patch("/api/v1/tiendas/{id}/estado", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(estadoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTienda").value(id))
                .andExpect(jsonPath("$.nombre").value("PERFULANDIA CONCEPCION"))
                .andExpect(jsonPath("$.activa").value(false));

        Mockito.verify(tiendaService).actualizarEstadoTienda(id, false);
    }

    @Test
    void actualizarEstadoTienda_cuandoNoExiste_deberiaRetornar404() throws Exception {
        //given
        Long id = 99L;

        ActualizarEstadoTiendaDTO estadoDTO = new ActualizarEstadoTiendaDTO();
        estadoDTO.setActiva(false);

        Mockito.when(tiendaService.actualizarEstadoTienda(eq(id), eq(false)))
                .thenReturn(Optional.empty());

        //when-then
        mockMvc.perform(patch("/api/v1/tiendas/{id}/estado", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(estadoDTO)))
                .andExpect(status().isNotFound());

        Mockito.verify(tiendaService).actualizarEstadoTienda(id, false);
    }

    @Test
    void actualizarEstadoTienda_conEstadoNulo_deberiaRetornar400() throws Exception {
        //given
        String bodyInvalido = "{}";

        //when-then
        mockMvc.perform(patch("/api/v1/tiendas/{id}/estado", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("ERROR VALIDACIÓN"))
                .andExpect(jsonPath("$.mensajes.activa").value("El ESTADO ACTIVO es OBLIGATORIO"));

        Mockito.verify(tiendaService, never()).actualizarEstadoTienda(any(), any());
    }

    @Test
    void eliminarTienda_cuandoExiste_deberiaRetornar204() throws Exception {
        //given
        Long id = 1L;

        Mockito.when(tiendaService.existeTiendaPorId(id)).thenReturn(true);

        //when-then
        mockMvc.perform(delete("/api/v1/tiendas/{id}", id))
                .andExpect(status().isNoContent());

        Mockito.verify(tiendaService).existeTiendaPorId(id);
        Mockito.verify(tiendaService).eliminarTienda(id);
    }

    @Test
    void eliminarTienda_cuandoNoExiste_deberiaRetornar404() throws Exception {
        //given
        Long id = 99L;

        Mockito.when(tiendaService.existeTiendaPorId(id)).thenReturn(false);

        //when-then
        mockMvc.perform(delete("/api/v1/tiendas/{id}", id))
                .andExpect(status().isNotFound());

        Mockito.verify(tiendaService).existeTiendaPorId(id);
        Mockito.verify(tiendaService, never()).eliminarTienda(id);
    }

    private TiendaDTO crearTiendaDTO(Long id, Boolean activa) {
        return new TiendaDTO(
                id,
                "PERFULANDIA CONCEPCION",
                "PEDRO DE VALDIVIA 123",
                "CONCEPCION",
                "CONCEPCION",
                "BIO BIO",
                "+56912345678",
                "WACOLDO SOTO",
                "09:00",
                "19:00",
                activa,
                "Uso de bolsas reutilizables"
        );
    }
}