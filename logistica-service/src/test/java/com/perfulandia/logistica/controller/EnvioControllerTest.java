package com.perfulandia.logistica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfulandia.logistica.dto.ActualizarEstadoEnvioRequest;
import com.perfulandia.logistica.dto.CrearEnvioRequest;
import com.perfulandia.logistica.exception.GlobalExceptionHandler;
import com.perfulandia.logistica.exception.RecursoNoEncontradoException;
import com.perfulandia.logistica.exception.ReglaNegocioException;
import com.perfulandia.logistica.model.Envio;
import com.perfulandia.logistica.model.EstadoEnvio;
import com.perfulandia.logistica.model.TipoEntrega;
import com.perfulandia.logistica.service.EnvioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnvioController.class)
@Import(GlobalExceptionHandler.class)
class EnvioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EnvioService envioService;

    @Test
    void crearEnvio_deberiaRetornarCreatedCuandoRequestEsValido() throws Exception {
        CrearEnvioRequest request = crearRequestValido();
        Envio envio = crearEnvioBase();
        envio.setIdEnvio(1L);

        when(envioService.crearEnvio(any(CrearEnvioRequest.class))).thenReturn(envio);

        mockMvc.perform(post("/api/v1/logistica/envios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idEnvio").value(1))
                .andExpect(jsonPath("$.idPedido").value(1))
                .andExpect(jsonPath("$.idCliente").value(5))
                .andExpect(jsonPath("$.estadoEnvio").value("PENDIENTE"));
    }

    @Test
    void crearEnvio_deberiaRetornarBadRequestCuandoFaltanDatosObligatorios() throws Exception {
        CrearEnvioRequest request = new CrearEnvioRequest();

        mockMvc.perform(post("/api/v1/logistica/envios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearEnvio_deberiaRetornarBadRequestCuandoExisteEnvioDuplicado() throws Exception {
        CrearEnvioRequest request = crearRequestValido();

        when(envioService.crearEnvio(any(CrearEnvioRequest.class)))
                .thenThrow(new ReglaNegocioException("Ya existe un envío asociado al pedido 1"));

        mockMvc.perform(post("/api/v1/logistica/envios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Ya existe un envío asociado al pedido 1"))
                .andExpect(jsonPath("$.error").value("Regla de negocio"));
    }

    @Test
    void listarEnvios_deberiaRetornarOkConLista() throws Exception {
        Envio envio = crearEnvioBase();
        envio.setIdEnvio(1L);

        when(envioService.listarEnvios()).thenReturn(List.of(envio));

        mockMvc.perform(get("/api/v1/logistica/envios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idEnvio").value(1))
                .andExpect(jsonPath("$[0].idPedido").value(1));
    }

    @Test
    void buscarPorId_deberiaRetornarOkCuandoExiste() throws Exception {
        Envio envio = crearEnvioBase();
        envio.setIdEnvio(1L);

        when(envioService.buscarPorId(1L)).thenReturn(envio);

        mockMvc.perform(get("/api/v1/logistica/envios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEnvio").value(1))
                .andExpect(jsonPath("$.idPedido").value(1));
    }

    @Test
    void buscarPorId_deberiaRetornarNotFoundCuandoNoExiste() throws Exception {
        when(envioService.buscarPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("No existe un envío con id 99"));

        mockMvc.perform(get("/api/v1/logistica/envios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No existe un envío con id 99"))
                .andExpect(jsonPath("$.error").value("Recurso no encontrado"));
    }

    @Test
    void buscarPorPedido_deberiaRetornarOk() throws Exception {
        Envio envio = crearEnvioBase();
        envio.setIdEnvio(1L);

        when(envioService.buscarPorPedido(1L)).thenReturn(envio);

        mockMvc.perform(get("/api/v1/logistica/envios/pedido/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPedido").value(1));
    }

    @Test
    void listarPorCliente_deberiaRetornarOk() throws Exception {
        Envio envio = crearEnvioBase();
        envio.setIdEnvio(1L);

        when(envioService.listarPorCliente(5L)).thenReturn(List.of(envio));

        mockMvc.perform(get("/api/v1/logistica/envios/cliente/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCliente").value(5));
    }

    @Test
    void listarPorEstado_deberiaRetornarOk() throws Exception {
        Envio envio = crearEnvioBase();
        envio.setIdEnvio(1L);
        envio.setEstadoEnvio(EstadoEnvio.PENDIENTE);

        when(envioService.listarPorEstado(EstadoEnvio.PENDIENTE)).thenReturn(List.of(envio));

        mockMvc.perform(get("/api/v1/logistica/envios/estado/PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estadoEnvio").value("PENDIENTE"));
    }

    @Test
    void listarPorTiendaOrigen_deberiaRetornarOk() throws Exception {
        Envio envio = crearEnvioBase();
        envio.setIdEnvio(1L);

        when(envioService.listarPorTiendaOrigen(1L)).thenReturn(List.of(envio));

        mockMvc.perform(get("/api/v1/logistica/envios/tienda/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idTiendaOrigen").value(1));
    }

    @Test
    void actualizarEstado_deberiaRetornarOk() throws Exception {
        ActualizarEstadoEnvioRequest request = new ActualizarEstadoEnvioRequest();
        request.setEstadoEnvio(EstadoEnvio.EN_TRANSITO);
        request.setObservacion("Pedido salió a reparto");

        Envio envio = crearEnvioBase();
        envio.setIdEnvio(1L);
        envio.setEstadoEnvio(EstadoEnvio.EN_TRANSITO);
        envio.setObservacion("Pedido salió a reparto");

        when(envioService.actualizarEstado(any(Long.class), any(ActualizarEstadoEnvioRequest.class)))
                .thenReturn(envio);

        mockMvc.perform(put("/api/v1/logistica/envios/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoEnvio").value("EN_TRANSITO"))
                .andExpect(jsonPath("$.observacion").value("Pedido salió a reparto"));
    }

    @Test
    void actualizarEstado_deberiaRetornarBadRequestCuandoEstadoEsInvalidoPorRegla() throws Exception {
        ActualizarEstadoEnvioRequest request = new ActualizarEstadoEnvioRequest();
        request.setEstadoEnvio(EstadoEnvio.EN_TRANSITO);
        request.setObservacion("Intento no permitido");

        when(envioService.actualizarEstado(any(Long.class), any(ActualizarEstadoEnvioRequest.class)))
                .thenThrow(new ReglaNegocioException("No se puede modificar un envío en estado ENTREGADO"));

        mockMvc.perform(put("/api/v1/logistica/envios/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("No se puede modificar un envío en estado ENTREGADO"));
    }

    @Test
    void actualizarEstado_deberiaRetornarBadRequestCuandoFaltaEstado() throws Exception {
    ActualizarEstadoEnvioRequest request = new ActualizarEstadoEnvioRequest();
    request.setObservacion("Sin estado");

    mockMvc.perform(put("/api/v1/logistica/envios/1/estado")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
      @Test
        void eliminarEnvio_deberiaRetornarNoContent() throws Exception {
        doNothing().when(envioService).eliminarEnvio(1L);

        mockMvc.perform(delete("/api/v1/logistica/envios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminarEnvio_deberiaRetornarBadRequestCuandoNoSePuedeEliminar() throws Exception {
        doThrow(new ReglaNegocioException("No se puede eliminar un envío en estado EN_TRANSITO"))
                .when(envioService).eliminarEnvio(1L);

        mockMvc.perform(delete("/api/v1/logistica/envios/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("No se puede eliminar un envío en estado EN_TRANSITO"));
    }

    private CrearEnvioRequest crearRequestValido() {
        CrearEnvioRequest request = new CrearEnvioRequest();
        request.setIdPedido(1L);
        request.setIdCliente(5L);
        request.setIdTiendaOrigen(1L);
        request.setDireccionDestino("Villa del Mar 123, Santiago");
        request.setTipoEntrega(TipoEntrega.DOMICILIO);
        request.setFechaEntregaEstimada(LocalDateTime.of(2026, 7, 10, 15, 30));
        request.setTransportista("Chilexpress");
        request.setNumeroSeguimiento("PERF-ENV-001");
        request.setObservacion("Entrega validada con controller test");
        return request;
    }

    private Envio crearEnvioBase() {
        Envio envio = new Envio();
        envio.setIdPedido(1L);
        envio.setIdCliente(5L);
        envio.setIdTiendaOrigen(1L);
        envio.setDireccionDestino("Villa del Mar 123, Santiago");
        envio.setTipoEntrega(TipoEntrega.DOMICILIO);
        envio.setEstadoEnvio(EstadoEnvio.PENDIENTE);
        envio.setFechaEntregaEstimada(LocalDateTime.of(2026, 7, 10, 15, 30));
        envio.setTransportista("Chilexpress");
        envio.setNumeroSeguimiento("PERF-ENV-001");
        envio.setObservacion("Envío de prueba");
        return envio;
    }
}