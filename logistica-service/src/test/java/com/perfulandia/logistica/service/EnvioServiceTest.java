package com.perfulandia.logistica.service;

import com.perfulandia.logistica.client.PedidoClient;
import com.perfulandia.logistica.client.UsuarioClient;
import com.perfulandia.logistica.client.dto.PedidoResponse;
import com.perfulandia.logistica.dto.ActualizarEstadoEnvioRequest;
import com.perfulandia.logistica.dto.CrearEnvioRequest;
import com.perfulandia.logistica.exception.RecursoNoEncontradoException;
import com.perfulandia.logistica.exception.ReglaNegocioException;
import com.perfulandia.logistica.model.Envio;
import com.perfulandia.logistica.model.EstadoEnvio;
import com.perfulandia.logistica.model.TipoEntrega;
import com.perfulandia.logistica.repository.EnvioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnvioServiceTest {

    @Mock
    private EnvioRepository envioRepository;

    @Mock
    private PedidoClient pedidoClient;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private EnvioService envioService;

    @Test
    void crearEnvio_deberiaCrearEnvioCuandoPedidoYUsuarioSonValidos() {
        CrearEnvioRequest request = crearRequestValido();
        PedidoResponse pedidoResponse = crearPedidoResponseValido();

        when(envioRepository.findByIdPedido(1L)).thenReturn(Optional.empty());
        when(pedidoClient.obtenerPedido(1L)).thenReturn(pedidoResponse);
        doNothing().when(usuarioClient).validarUsuarioExiste(5L);
        when(envioRepository.save(any(Envio.class))).thenAnswer(invocation -> {
            Envio envio = invocation.getArgument(0);
            envio.setIdEnvio(1L);
            return envio;
        });

        Envio resultado = envioService.crearEnvio(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdEnvio());
        assertEquals(1L, resultado.getIdPedido());
        assertEquals(5L, resultado.getIdCliente());
        assertEquals(1L, resultado.getIdTiendaOrigen());
        assertEquals(EstadoEnvio.PENDIENTE, resultado.getEstadoEnvio());
        assertEquals(TipoEntrega.DOMICILIO, resultado.getTipoEntrega());

        verify(envioRepository).findByIdPedido(1L);
        verify(pedidoClient).obtenerPedido(1L);
        verify(usuarioClient).validarUsuarioExiste(5L);
        verify(envioRepository).save(any(Envio.class));
    }

    @Test
    void crearEnvio_deberiaLanzarErrorSiYaExisteEnvioParaPedido() {
        CrearEnvioRequest request = crearRequestValido();
        Envio envioExistente = crearEnvioBase();
        envioExistente.setIdEnvio(1L);

        when(envioRepository.findByIdPedido(1L)).thenReturn(Optional.of(envioExistente));

        ReglaNegocioException exception = assertThrows(
                ReglaNegocioException.class,
                () -> envioService.crearEnvio(request)
        );

        assertEquals("Ya existe un envío asociado al pedido 1", exception.getMessage());

        verify(envioRepository).findByIdPedido(1L);
        verify(pedidoClient, never()).obtenerPedido(any());
        verify(usuarioClient, never()).validarUsuarioExiste(any());
        verify(envioRepository, never()).save(any());
    }

    @Test
    void crearEnvio_deberiaLanzarErrorSiPedidoNoExiste() {
        CrearEnvioRequest request = crearRequestValido();

        when(envioRepository.findByIdPedido(1L)).thenReturn(Optional.empty());
        when(pedidoClient.obtenerPedido(1L))
                .thenThrow(new ReglaNegocioException("No existe un pedido con id 1"));

        ReglaNegocioException exception = assertThrows(
                ReglaNegocioException.class,
                () -> envioService.crearEnvio(request)
        );

        assertEquals("No existe un pedido con id 1", exception.getMessage());

        verify(envioRepository).findByIdPedido(1L);
        verify(pedidoClient).obtenerPedido(1L);
        verify(usuarioClient, never()).validarUsuarioExiste(any());
        verify(envioRepository, never()).save(any());
    }

    @Test
    void crearEnvio_deberiaLanzarErrorSiUsuarioNoExiste() {
        CrearEnvioRequest request = crearRequestValido();
        PedidoResponse pedidoResponse = crearPedidoResponseValido();

        when(envioRepository.findByIdPedido(1L)).thenReturn(Optional.empty());
        when(pedidoClient.obtenerPedido(1L)).thenReturn(pedidoResponse);
        doThrow(new ReglaNegocioException("No existe un usuario con id 5"))
                .when(usuarioClient).validarUsuarioExiste(5L);

        ReglaNegocioException exception = assertThrows(
                ReglaNegocioException.class,
                () -> envioService.crearEnvio(request)
        );

        assertEquals("No existe un usuario con id 5", exception.getMessage());

        verify(envioRepository).findByIdPedido(1L);
        verify(pedidoClient).obtenerPedido(1L);
        verify(usuarioClient).validarUsuarioExiste(5L);
        verify(envioRepository, never()).save(any());
    }

    @Test
    void crearEnvio_deberiaLanzarErrorSiPedidoNoTieneUsuarioAsociado() {
        CrearEnvioRequest request = crearRequestValido();

        PedidoResponse pedidoResponse = new PedidoResponse();
        pedidoResponse.setIdPedido(1L);
        pedidoResponse.setIdUsuario(null);
        pedidoResponse.setIdTienda(1L);
        pedidoResponse.setEstado("PENDIENTE");

        when(envioRepository.findByIdPedido(1L)).thenReturn(Optional.empty());
        when(pedidoClient.obtenerPedido(1L)).thenReturn(pedidoResponse);
        doNothing().when(usuarioClient).validarUsuarioExiste(5L);

        ReglaNegocioException exception = assertThrows(
                ReglaNegocioException.class,
                () -> envioService.crearEnvio(request)
        );

        assertEquals("El pedido no tiene usuario asociado", exception.getMessage());

        verify(envioRepository).findByIdPedido(1L);
        verify(pedidoClient).obtenerPedido(1L);
        verify(usuarioClient).validarUsuarioExiste(5L);
        verify(envioRepository, never()).save(any());
    }

    @Test
    void crearEnvio_deberiaLanzarErrorSiPedidoNoPerteneceAlCliente() {
        CrearEnvioRequest request = crearRequestValido();
        request.setIdCliente(2L);

        PedidoResponse pedidoResponse = new PedidoResponse();
        pedidoResponse.setIdPedido(1L);
        pedidoResponse.setIdUsuario(5L);
        pedidoResponse.setIdTienda(1L);
        pedidoResponse.setEstado("PENDIENTE");

        when(envioRepository.findByIdPedido(1L)).thenReturn(Optional.empty());
        when(pedidoClient.obtenerPedido(1L)).thenReturn(pedidoResponse);
        doNothing().when(usuarioClient).validarUsuarioExiste(2L);

        ReglaNegocioException exception = assertThrows(
                ReglaNegocioException.class,
                () -> envioService.crearEnvio(request)
        );

        assertEquals("El pedido 1 no pertenece al cliente 2", exception.getMessage());

        verify(envioRepository).findByIdPedido(1L);
        verify(pedidoClient).obtenerPedido(1L);
        verify(usuarioClient).validarUsuarioExiste(2L);
        verify(envioRepository, never()).save(any());
    }

    @Test
    void listarEnvios_deberiaRetornarTodosLosEnvios() {
        Envio envio1 = crearEnvioBase();
        envio1.setIdEnvio(1L);

        Envio envio2 = crearEnvioBase();
        envio2.setIdEnvio(2L);
        envio2.setIdPedido(2L);

        when(envioRepository.findAll()).thenReturn(List.of(envio1, envio2));

        List<Envio> resultado = envioService.listarEnvios();

        assertEquals(2, resultado.size());
        verify(envioRepository).findAll();
    }

    @Test
    void buscarPorId_deberiaRetornarEnvioSiExiste() {
        Envio envio = crearEnvioBase();
        envio.setIdEnvio(1L);

        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));

        Envio resultado = envioService.buscarPorId(1L);

        assertEquals(1L, resultado.getIdEnvio());
        verify(envioRepository).findById(1L);
    }

    @Test
    void buscarPorId_deberiaLanzarErrorSiNoExiste() {
        when(envioRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(
                RecursoNoEncontradoException.class,
                () -> envioService.buscarPorId(99L)
        );

        assertEquals("No existe un envío con id 99", exception.getMessage());

        verify(envioRepository).findById(99L);
    }

    @Test
    void buscarPorPedido_deberiaRetornarEnvioSiExiste() {
        Envio envio = crearEnvioBase();
        envio.setIdEnvio(1L);

        when(envioRepository.findByIdPedido(1L)).thenReturn(Optional.of(envio));

        Envio resultado = envioService.buscarPorPedido(1L);

        assertEquals(1L, resultado.getIdPedido());
        verify(envioRepository).findByIdPedido(1L);
    }

    @Test
    void buscarPorPedido_deberiaLanzarErrorSiNoExiste() {
        when(envioRepository.findByIdPedido(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(
                RecursoNoEncontradoException.class,
                () -> envioService.buscarPorPedido(99L)
        );

        assertEquals("No existe un envío para el pedido 99", exception.getMessage());

        verify(envioRepository).findByIdPedido(99L);
    }

    @Test
    void listarPorCliente_deberiaRetornarEnviosDelCliente() {
        Envio envio = crearEnvioBase();
        envio.setIdCliente(5L);

        when(envioRepository.findByIdCliente(5L)).thenReturn(List.of(envio));

        List<Envio> resultado = envioService.listarPorCliente(5L);

        assertEquals(1, resultado.size());
        assertEquals(5L, resultado.get(0).getIdCliente());
        verify(envioRepository).findByIdCliente(5L);
    }

    @Test
    void listarPorEstado_deberiaRetornarEnviosPorEstado() {
        Envio envio = crearEnvioBase();
        envio.setEstadoEnvio(EstadoEnvio.PENDIENTE);

        when(envioRepository.findByEstadoEnvio(EstadoEnvio.PENDIENTE)).thenReturn(List.of(envio));

        List<Envio> resultado = envioService.listarPorEstado(EstadoEnvio.PENDIENTE);

        assertEquals(1, resultado.size());
        assertEquals(EstadoEnvio.PENDIENTE, resultado.get(0).getEstadoEnvio());
        verify(envioRepository).findByEstadoEnvio(EstadoEnvio.PENDIENTE);
    }

    @Test
    void listarPorTiendaOrigen_deberiaRetornarEnviosPorTienda() {
        Envio envio = crearEnvioBase();
        envio.setIdTiendaOrigen(1L);

        when(envioRepository.findByIdTiendaOrigen(1L)).thenReturn(List.of(envio));

        List<Envio> resultado = envioService.listarPorTiendaOrigen(1L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getIdTiendaOrigen());
        verify(envioRepository).findByIdTiendaOrigen(1L);
    }

    @Test
    void actualizarEstado_deberiaCambiarEstadoAEnTransito() {
        Envio envio = crearEnvioBase();
        envio.setIdEnvio(1L);
        envio.setEstadoEnvio(EstadoEnvio.PENDIENTE);

        ActualizarEstadoEnvioRequest request = new ActualizarEstadoEnvioRequest();
        request.setEstadoEnvio(EstadoEnvio.EN_TRANSITO);
        request.setObservacion("Pedido salió a reparto");

        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));
        when(envioRepository.save(any(Envio.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Envio resultado = envioService.actualizarEstado(1L, request);

        assertEquals(EstadoEnvio.EN_TRANSITO, resultado.getEstadoEnvio());
        assertEquals("Pedido salió a reparto", resultado.getObservacion());
        assertNull(resultado.getFechaEntregaReal());

        verify(envioRepository).findById(1L);
        verify(envioRepository).save(envio);
    }

    @Test
    void actualizarEstado_deberiaRegistrarFechaRealCuandoEstadoEsEntregado() {
        Envio envio = crearEnvioBase();
        envio.setIdEnvio(1L);
        envio.setEstadoEnvio(EstadoEnvio.EN_TRANSITO);

        ActualizarEstadoEnvioRequest request = new ActualizarEstadoEnvioRequest();
        request.setEstadoEnvio(EstadoEnvio.ENTREGADO);
        request.setObservacion("Entregado correctamente");

        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));
        when(envioRepository.save(any(Envio.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Envio resultado = envioService.actualizarEstado(1L, request);

        assertEquals(EstadoEnvio.ENTREGADO, resultado.getEstadoEnvio());
        assertNotNull(resultado.getFechaEntregaReal());
        assertEquals("Entregado correctamente", resultado.getObservacion());

        verify(envioRepository).findById(1L);
        verify(envioRepository).save(envio);
    }

    @Test
    void actualizarEstado_deberiaLanzarErrorSiEnvioYaEstaEntregado() {
        Envio envio = crearEnvioBase();
        envio.setIdEnvio(1L);
        envio.setEstadoEnvio(EstadoEnvio.ENTREGADO);

        ActualizarEstadoEnvioRequest request = new ActualizarEstadoEnvioRequest();
        request.setEstadoEnvio(EstadoEnvio.EN_TRANSITO);
        request.setObservacion("Intento no permitido");

        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));

        ReglaNegocioException exception = assertThrows(
                ReglaNegocioException.class,
                () -> envioService.actualizarEstado(1L, request)
        );

        assertEquals("No se puede modificar un envío en estado ENTREGADO", exception.getMessage());

        verify(envioRepository).findById(1L);
        verify(envioRepository, never()).save(any());
    }

    @Test
    void actualizarEstado_deberiaLanzarErrorSiEnvioEstaCancelado() {
        Envio envio = crearEnvioBase();
        envio.setIdEnvio(1L);
        envio.setEstadoEnvio(EstadoEnvio.CANCELADO);

        ActualizarEstadoEnvioRequest request = new ActualizarEstadoEnvioRequest();
        request.setEstadoEnvio(EstadoEnvio.EN_TRANSITO);
        request.setObservacion("Intento no permitido");

        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));

        ReglaNegocioException exception = assertThrows(
                ReglaNegocioException.class,
                () -> envioService.actualizarEstado(1L, request)
        );

        assertEquals("No se puede modificar un envío en estado CANCELADO", exception.getMessage());

        verify(envioRepository).findById(1L);
        verify(envioRepository, never()).save(any());
    }

    @Test
    void eliminarEnvio_deberiaEliminarEnvioSiEstaPendiente() {
        Envio envio = crearEnvioBase();
        envio.setIdEnvio(1L);
        envio.setEstadoEnvio(EstadoEnvio.PENDIENTE);

        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));

        envioService.eliminarEnvio(1L);

        verify(envioRepository).findById(1L);
        verify(envioRepository).delete(envio);
    }

    @Test
    void eliminarEnvio_deberiaLanzarErrorSiEstaEnTransito() {
        Envio envio = crearEnvioBase();
        envio.setIdEnvio(1L);
        envio.setEstadoEnvio(EstadoEnvio.EN_TRANSITO);

        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));

        ReglaNegocioException exception = assertThrows(
                ReglaNegocioException.class,
                () -> envioService.eliminarEnvio(1L)
        );

        assertEquals("No se puede eliminar un envío en estado EN_TRANSITO", exception.getMessage());

        verify(envioRepository).findById(1L);
        verify(envioRepository, never()).delete(any());
    }

    @Test
    void eliminarEnvio_deberiaLanzarErrorSiEstaEntregado() {
        Envio envio = crearEnvioBase();
        envio.setIdEnvio(1L);
        envio.setEstadoEnvio(EstadoEnvio.ENTREGADO);

        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));

        ReglaNegocioException exception = assertThrows(
                ReglaNegocioException.class,
                () -> envioService.eliminarEnvio(1L)
        );

        assertEquals("No se puede eliminar un envío en estado ENTREGADO", exception.getMessage());

        verify(envioRepository).findById(1L);
        verify(envioRepository, never()).delete(any());
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
        request.setObservacion("Entrega validada con pruebas unitarias");
        return request;
    }

    private PedidoResponse crearPedidoResponseValido() {
        PedidoResponse pedidoResponse = new PedidoResponse();
        pedidoResponse.setIdPedido(1L);
        pedidoResponse.setIdUsuario(5L);
        pedidoResponse.setIdTienda(1L);
        pedidoResponse.setEstado("PENDIENTE");
        return pedidoResponse;
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