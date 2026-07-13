package com.example.soporte_resena_service.controller;

import com.example.soporte_resena_service.dto.ResenaDTO;
import com.example.soporte_resena_service.service.ResenaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

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

@WebMvcTest(ResenaController.class)
@ActiveProfiles("test")
class ResenaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResenaService resenaService;

    @Test
    void listarResenas_deberiaRetornar200YListaDeResenas() throws Exception {
        //given
        ResenaDTO resena = crearResenaDTO(1L, true);
        Mockito.when(resenaService.listarResenas()).thenReturn(List.of(resena));

        //when
        ResultActions resultado = mockMvc.perform(get("/api/v1/resenas"));

        //then
        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idResena").value(1L))
                .andExpect(jsonPath("$[0].idCliente").value(1L))
                .andExpect(jsonPath("$[0].idProducto").value(1L))
                .andExpect(jsonPath("$[0].nombreCliente").value("Nicolás Quezada"))
                .andExpect(jsonPath("$[0].calificacion").value(5))
                .andExpect(jsonPath("$[0].activa").value(true));

        Mockito.verify(resenaService).listarResenas();
    }

    @Test
    void buscarResenaPorId_cuandoExiste_deberiaRetornar200YResena() throws Exception {
        //given
        Long id = 1L;
        ResenaDTO resena = crearResenaDTO(id, true);

        Mockito.when(resenaService.buscarResenaPorId(id)).thenReturn(Optional.of(resena));

        //when
        ResultActions resultado = mockMvc.perform(get("/api/v1/resenas/{id}", id));

        //then
        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$.idResena").value(id))
                .andExpect(jsonPath("$.idCliente").value(1L))
                .andExpect(jsonPath("$.idProducto").value(1L))
                .andExpect(jsonPath("$.nombreCliente").value("Nicolás Quezada"))
                .andExpect(jsonPath("$.calificacion").value(5))
                .andExpect(jsonPath("$.comentario").value("Aroma elegante, buena fijación y presentación muy cuidada."))
                .andExpect(jsonPath("$.activa").value(true));

        Mockito.verify(resenaService).buscarResenaPorId(id);
    }

    @Test
    void buscarResenaPorId_cuandoNoExiste_deberiaRetornar404() throws Exception {
        //given
        Long id = 99L;

        Mockito.when(resenaService.buscarResenaPorId(id)).thenReturn(Optional.empty());

        //when
        ResultActions resultado = mockMvc.perform(get("/api/v1/resenas/{id}", id));

        //then
        resultado.andExpect(status().isNotFound());

        Mockito.verify(resenaService).buscarResenaPorId(id);
    }

    @Test
    void crearResena_conDatosValidos_deberiaRetornar201YResenaCreada() throws Exception {
        //given
        ResenaDTO resenaCreada = crearResenaDTO(1L, true);

        Mockito.when(resenaService.guardarResena(any(ResenaDTO.class))).thenReturn(resenaCreada);

        //when
        ResultActions resultado = mockMvc.perform(post("/api/v1/resenas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyValido()));

        //then
        resultado.andExpect(status().isCreated())
                .andExpect(jsonPath("$.idResena").value(1L))
                .andExpect(jsonPath("$.idCliente").value(1L))
                .andExpect(jsonPath("$.idProducto").value(1L))
                .andExpect(jsonPath("$.nombreCliente").value("Nicolás Quezada"))
                .andExpect(jsonPath("$.calificacion").value(5))
                .andExpect(jsonPath("$.activa").value(true));

        Mockito.verify(resenaService).guardarResena(any(ResenaDTO.class));
    }

    @Test
    void crearResena_conNombreClienteVacio_deberiaRetornar400() throws Exception {
        //given
        String body = """
                {
                    "idCliente": 1,
                    "idProducto": 1,
                    "nombreCliente": "",
                    "calificacion": 5,
                    "comentario": "Aroma elegante, buena fijación y presentación muy cuidada.",
                    "activa": true
                }
                """;

        //when
        ResultActions resultado = mockMvc.perform(post("/api/v1/resenas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        //then
        resultado.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("ERROR VALIDACIÓN"))
                .andExpect(jsonPath("$.mensajes.nombreCliente").value("El NOMBRE del CLIENTE es OBLIGATORIO"));

        Mockito.verify(resenaService, never()).guardarResena(any(ResenaDTO.class));
    }

    @Test
    void crearResena_sinIdCliente_deberiaRetornar400() throws Exception {
        //given
        String body = """
                {
                    "idProducto": 1,
                    "nombreCliente": "Nicolás Quezada",
                    "calificacion": 5,
                    "comentario": "Aroma elegante, buena fijación y presentación muy cuidada.",
                    "activa": true
                }
                """;

        //when
        ResultActions resultado = mockMvc.perform(post("/api/v1/resenas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        //then
        resultado.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("ERROR VALIDACIÓN"))
                .andExpect(jsonPath("$.mensajes.idCliente").value("El ID del CLIENTE es OBLIGATORIO"));

        Mockito.verify(resenaService, never()).guardarResena(any(ResenaDTO.class));
    }

    @Test
    void crearResena_conCalificacionMayorA5_deberiaRetornar400() throws Exception {
        //given
        String body = """
                {
                    "idCliente": 1,
                    "idProducto": 1,
                    "nombreCliente": "Nicolás Quezada",
                    "calificacion": 6,
                    "comentario": "Aroma elegante, buena fijación y presentación muy cuidada.",
                    "activa": true
                }
                """;

        //when
        ResultActions resultado = mockMvc.perform(post("/api/v1/resenas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        //then
        resultado.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("ERROR VALIDACIÓN"))
                .andExpect(jsonPath("$.mensajes.calificacion").value("La CALIFICACIÓN máxima es 5"));

        Mockito.verify(resenaService, never()).guardarResena(any(ResenaDTO.class));
    }

    @Test
    void crearResena_cuandoServiceLanzaRuntimeException_deberiaRetornar400() throws Exception {
        //given
        Mockito.when(resenaService.guardarResena(any(ResenaDTO.class)))
                .thenThrow(new RuntimeException("El cliente con ID 1 no existe"));

        //when
        ResultActions resultado = mockMvc.perform(post("/api/v1/resenas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyValido()));

        //then
        resultado.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("ERROR DE SOLICITUD"))
                .andExpect(jsonPath("$.mensaje").value("El cliente con ID 1 no existe"));

        Mockito.verify(resenaService).guardarResena(any(ResenaDTO.class));
    }

    @Test
    void actualizarResena_cuandoExiste_deberiaRetornar200YResenaActualizada() throws Exception {
        //given
        Long id = 1L;
        ResenaDTO resenaActualizada = crearResenaDTO(id, true);

        Mockito.when(resenaService.existeResenaPorId(id)).thenReturn(true);
        Mockito.when(resenaService.actualizarResena(any(ResenaDTO.class))).thenReturn(resenaActualizada);

        //when
        ResultActions resultado = mockMvc.perform(put("/api/v1/resenas/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyValido()));

        //then
        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$.idResena").value(id))
                .andExpect(jsonPath("$.idCliente").value(1L))
                .andExpect(jsonPath("$.idProducto").value(1L))
                .andExpect(jsonPath("$.nombreCliente").value("Nicolás Quezada"))
                .andExpect(jsonPath("$.calificacion").value(5));

        Mockito.verify(resenaService).existeResenaPorId(id);
        Mockito.verify(resenaService).actualizarResena(any(ResenaDTO.class));
    }

    @Test
    void actualizarResena_cuandoNoExiste_deberiaRetornar404() throws Exception {
        //given
        Long id = 99L;

        Mockito.when(resenaService.existeResenaPorId(id)).thenReturn(false);

        //when
        ResultActions resultado = mockMvc.perform(put("/api/v1/resenas/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyValido()));

        //then
        resultado.andExpect(status().isNotFound());

        Mockito.verify(resenaService).existeResenaPorId(id);
        Mockito.verify(resenaService, never()).actualizarResena(any(ResenaDTO.class));
    }

    @Test
    void actualizarResena_cuandoServiceLanzaRuntimeException_deberiaRetornar400() throws Exception {
        //given
        Long id = 1L;

        Mockito.when(resenaService.existeResenaPorId(id)).thenReturn(true);
        Mockito.when(resenaService.actualizarResena(any(ResenaDTO.class)))
                .thenThrow(new RuntimeException("El producto con ID 1 no existe"));

        //when
        ResultActions resultado = mockMvc.perform(put("/api/v1/resenas/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyValido()));

        //then
        resultado.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("ERROR DE SOLICITUD"))
                .andExpect(jsonPath("$.mensaje").value("El producto con ID 1 no existe"));

        Mockito.verify(resenaService).existeResenaPorId(id);
        Mockito.verify(resenaService).actualizarResena(any(ResenaDTO.class));
    }

    @Test
    void eliminarResena_cuandoExiste_deberiaRetornar204() throws Exception {
        //given
        Long id = 1L;

        Mockito.when(resenaService.existeResenaPorId(id)).thenReturn(true);

        //when
        ResultActions resultado = mockMvc.perform(delete("/api/v1/resenas/{id}", id));

        //then
        resultado.andExpect(status().isNoContent());

        Mockito.verify(resenaService).existeResenaPorId(id);
        Mockito.verify(resenaService).eliminarResena(id);
    }

    @Test
    void eliminarResena_cuandoNoExiste_deberiaRetornar404() throws Exception {
        //given
        Long id = 99L;

        Mockito.when(resenaService.existeResenaPorId(id)).thenReturn(false);

        //when
        ResultActions resultado = mockMvc.perform(delete("/api/v1/resenas/{id}", id));

        //then
        resultado.andExpect(status().isNotFound());

        Mockito.verify(resenaService).existeResenaPorId(id);
        Mockito.verify(resenaService, never()).eliminarResena(id);
    }

    @Test
    void listarResenasPorProducto_deberiaRetornar200YResenasDeUnProducto() throws Exception {
        //given
        Long idProducto = 1L;
        ResenaDTO resena = crearResenaDTO(1L, true);

        Mockito.when(resenaService.listarResenasPorProducto(idProducto)).thenReturn(List.of(resena));

        //when
        ResultActions resultado = mockMvc.perform(get("/api/v1/resenas/producto/{idProducto}", idProducto));

        //then
        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idCliente").value(1L))
                .andExpect(jsonPath("$[0].idProducto").value(idProducto))
                .andExpect(jsonPath("$[0].nombreCliente").value("Nicolás Quezada"));

        Mockito.verify(resenaService).listarResenasPorProducto(idProducto);
    }

    @Test
    void listarResenasActivas_deberiaRetornar200YResenasActivas() throws Exception {
        //given
        ResenaDTO resena = crearResenaDTO(1L, true);

        Mockito.when(resenaService.listarResenasActivas()).thenReturn(List.of(resena));

        //when
        ResultActions resultado = mockMvc.perform(get("/api/v1/resenas/activas"));

        //then
        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idCliente").value(1L))
                .andExpect(jsonPath("$[0].activa").value(true))
                .andExpect(jsonPath("$[0].nombreCliente").value("Nicolás Quezada"));

        Mockito.verify(resenaService).listarResenasActivas();
    }

    @Test
    void listarResenasPorCalificacion_deberiaRetornar200YResenasDeUnaCalificacion() throws Exception {
        //given
        Integer calificacion = 5;
        ResenaDTO resena = crearResenaDTO(1L, true);

        Mockito.when(resenaService.listarResenasPorCalificacion(calificacion)).thenReturn(List.of(resena));

        //when
        ResultActions resultado = mockMvc.perform(get("/api/v1/resenas/calificacion/{calificacion}", calificacion));

        //then
        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idCliente").value(1L))
                .andExpect(jsonPath("$[0].calificacion").value(calificacion))
                .andExpect(jsonPath("$[0].comentario").value("Aroma elegante, buena fijación y presentación muy cuidada."));

        Mockito.verify(resenaService).listarResenasPorCalificacion(calificacion);
    }

    private String bodyValido() {
        return """
                {
                    "idCliente": 1,
                    "idProducto": 1,
                    "nombreCliente": "Nicolás Quezada",
                    "calificacion": 5,
                    "comentario": "Aroma elegante, buena fijación y presentación muy cuidada.",
                    "activa": true
                }
                """;
    }

    private ResenaDTO crearResenaDTO(Long id, Boolean activa) {
        return new ResenaDTO(
                id,
                1L,
                1L,
                "Nicolás Quezada",
                5,
                "Aroma elegante, buena fijación y presentación muy cuidada.",
                activa
        );
    }
}