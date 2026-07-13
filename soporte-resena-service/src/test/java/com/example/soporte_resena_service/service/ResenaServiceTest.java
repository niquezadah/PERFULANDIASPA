package com.example.soporte_resena_service.service;

import com.example.soporte_resena_service.dto.ProductoDTO;
import com.example.soporte_resena_service.dto.ResenaDTO;
import com.example.soporte_resena_service.dto.UsuarioDTO;
import com.example.soporte_resena_service.model.Resena;
import com.example.soporte_resena_service.repository.ResenaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResenaServiceTest {

    private static final String URL_USUARIO_1 = "http://localhost:8082/api/usuarios/1";
    private static final String URL_PRODUCTO_1 = "http://localhost:8092/api/v1/productos/1";

    @Mock
    private ResenaRepository resenaRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ResenaService resenaService;

    @Test
    void listarResenas_deberiaRetornarListaDeResenas() {
        //given
        Resena resena = crearResena(1L, true);
        when(resenaRepository.findAll()).thenReturn(List.of(resena));

        //when
        List<ResenaDTO> resultado = resenaService.listarResenas();

        //then
        assertAll(
                () -> assertEquals(1, resultado.size()),
                () -> assertEquals(1L, resultado.get(0).getIdResena()),
                () -> assertEquals(1L, resultado.get(0).getIdCliente()),
                () -> assertEquals(1L, resultado.get(0).getIdProducto()),
                () -> assertEquals("Nicolás Quezada", resultado.get(0).getNombreCliente()),
                () -> assertEquals(5, resultado.get(0).getCalificacion()),
                () -> assertEquals("Aroma elegante, buena fijación y presentación muy cuidada.", resultado.get(0).getComentario()),
                () -> assertTrue(resultado.get(0).getActiva())
        );

        verify(resenaRepository).findAll();
    }

    @Test
    void buscarResenaPorId_cuandoExiste_deberiaRetornarResena() {
        //given
        Long id = 1L;
        Resena resena = crearResena(id, true);

        when(resenaRepository.findById(id)).thenReturn(Optional.of(resena));

        //when
        Optional<ResenaDTO> resultado = resenaService.buscarResenaPorId(id);

        //then
        assertTrue(resultado.isPresent());
        assertAll(
                () -> assertEquals(id, resultado.get().getIdResena()),
                () -> assertEquals(1L, resultado.get().getIdCliente()),
                () -> assertEquals(1L, resultado.get().getIdProducto()),
                () -> assertEquals("Nicolás Quezada", resultado.get().getNombreCliente()),
                () -> assertEquals(5, resultado.get().getCalificacion()),
                () -> assertTrue(resultado.get().getActiva())
        );

        verify(resenaRepository).findById(id);
    }

    @Test
    void buscarResenaPorId_cuandoNoExiste_deberiaRetornarOptionalVacio() {
        //given
        Long id = 99L;

        when(resenaRepository.findById(id)).thenReturn(Optional.empty());

        //when
        Optional<ResenaDTO> resultado = resenaService.buscarResenaPorId(id);

        //then
        assertTrue(resultado.isEmpty());

        verify(resenaRepository).findById(id);
    }

    @Test
    void guardarResena_cuandoClienteYProductoExisten_deberiaGuardarYRetornarResenaDTO() {
        //given
        ResenaDTO resenaDTO = crearResenaDTO(null, true);
        Resena resenaGuardada = crearResena(1L, true);

        simularClienteValido();
        simularProductoValido();
        when(resenaRepository.save(any(Resena.class))).thenReturn(resenaGuardada);

        //when
        ResenaDTO resultado = resenaService.guardarResena(resenaDTO);

        //then
        assertAll(
                () -> assertEquals(1L, resultado.getIdResena()),
                () -> assertEquals(1L, resultado.getIdCliente()),
                () -> assertEquals(1L, resultado.getIdProducto()),
                () -> assertEquals("Nicolás Quezada", resultado.getNombreCliente()),
                () -> assertEquals(5, resultado.getCalificacion()),
                () -> assertEquals("Aroma elegante, buena fijación y presentación muy cuidada.", resultado.getComentario()),
                () -> assertTrue(resultado.getActiva())
        );

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate).getForObject(URL_PRODUCTO_1, ProductoDTO.class);
        verify(resenaRepository).save(any(Resena.class));
    }

    @Test
    void guardarResena_cuandoClienteNoExiste_deberiaLanzarRuntimeException() {
        //given
        ResenaDTO resenaDTO = crearResenaDTO(null, true);
        HttpClientErrorException.NotFound error = mock(HttpClientErrorException.NotFound.class);

        when(restTemplate.getForObject(URL_USUARIO_1, UsuarioDTO.class)).thenThrow(error);

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> resenaService.guardarResena(resenaDTO)
        );

        //then
        assertEquals("El cliente con ID 1 no existe", exception.getMessage());

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate, never()).getForObject(URL_PRODUCTO_1, ProductoDTO.class);
        verify(resenaRepository, never()).save(any(Resena.class));
    }

    @Test
    void guardarResena_cuandoUsuarioServiceNoResponde_deberiaLanzarRuntimeException() {
        //given
        ResenaDTO resenaDTO = crearResenaDTO(null, true);

        when(restTemplate.getForObject(URL_USUARIO_1, UsuarioDTO.class))
                .thenThrow(new RestClientException("Error de conexión"));

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> resenaService.guardarResena(resenaDTO)
        );

        //then
        assertEquals("No se pudo validar el cliente con ID 1", exception.getMessage());

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate, never()).getForObject(URL_PRODUCTO_1, ProductoDTO.class);
        verify(resenaRepository, never()).save(any(Resena.class));
    }

    @Test
    void guardarResena_cuandoClienteRetornaNull_deberiaLanzarRuntimeException() {
        //given
        ResenaDTO resenaDTO = crearResenaDTO(null, true);

        when(restTemplate.getForObject(URL_USUARIO_1, UsuarioDTO.class)).thenReturn(null);

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> resenaService.guardarResena(resenaDTO)
        );

        //then
        assertEquals("El cliente con ID 1 no existe", exception.getMessage());

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate, never()).getForObject(URL_PRODUCTO_1, ProductoDTO.class);
        verify(resenaRepository, never()).save(any(Resena.class));
    }

    @Test
    void guardarResena_cuandoClienteEstaInactivo_deberiaLanzarRuntimeException() {
        //given
        ResenaDTO resenaDTO = crearResenaDTO(null, true);

        when(restTemplate.getForObject(URL_USUARIO_1, UsuarioDTO.class))
                .thenReturn(crearUsuarioDTO(false));

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> resenaService.guardarResena(resenaDTO)
        );

        //then
        assertEquals("El cliente con ID 1 está inactivo", exception.getMessage());

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate, never()).getForObject(URL_PRODUCTO_1, ProductoDTO.class);
        verify(resenaRepository, never()).save(any(Resena.class));
    }

    @Test
    void guardarResena_cuandoProductoNoExiste_deberiaLanzarRuntimeException() {
        //given
        ResenaDTO resenaDTO = crearResenaDTO(null, true);
        HttpClientErrorException.NotFound error = mock(HttpClientErrorException.NotFound.class);

        simularClienteValido();
        when(restTemplate.getForObject(URL_PRODUCTO_1, ProductoDTO.class)).thenThrow(error);

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> resenaService.guardarResena(resenaDTO)
        );

        //then
        assertEquals("El producto con ID 1 no existe", exception.getMessage());

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate).getForObject(URL_PRODUCTO_1, ProductoDTO.class);
        verify(resenaRepository, never()).save(any(Resena.class));
    }

    @Test
    void guardarResena_cuandoProductoServiceNoResponde_deberiaLanzarRuntimeException() {
        //given
        ResenaDTO resenaDTO = crearResenaDTO(null, true);

        simularClienteValido();
        when(restTemplate.getForObject(URL_PRODUCTO_1, ProductoDTO.class))
                .thenThrow(new RestClientException("Error de conexión"));

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> resenaService.guardarResena(resenaDTO)
        );

        //then
        assertEquals("No se pudo validar el producto con ID 1", exception.getMessage());

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate).getForObject(URL_PRODUCTO_1, ProductoDTO.class);
        verify(resenaRepository, never()).save(any(Resena.class));
    }

    @Test
    void guardarResena_cuandoProductoRetornaNull_deberiaLanzarRuntimeException() {
        //given
        ResenaDTO resenaDTO = crearResenaDTO(null, true);

        simularClienteValido();
        when(restTemplate.getForObject(URL_PRODUCTO_1, ProductoDTO.class)).thenReturn(null);

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> resenaService.guardarResena(resenaDTO)
        );

        //then
        assertEquals("El producto con ID 1 no existe", exception.getMessage());

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate).getForObject(URL_PRODUCTO_1, ProductoDTO.class);
        verify(resenaRepository, never()).save(any(Resena.class));
    }

    @Test
    void actualizarResena_cuandoClienteYProductoExisten_deberiaActualizarYRetornarResenaDTO() {
        //given
        ResenaDTO resenaDTO = crearResenaDTO(1L, true);
        Resena resenaActualizada = crearResena(1L, true);

        simularClienteValido();
        simularProductoValido();
        when(resenaRepository.save(any(Resena.class))).thenReturn(resenaActualizada);

        //when
        ResenaDTO resultado = resenaService.actualizarResena(resenaDTO);

        //then
        assertAll(
                () -> assertEquals(1L, resultado.getIdResena()),
                () -> assertEquals(1L, resultado.getIdCliente()),
                () -> assertEquals(1L, resultado.getIdProducto()),
                () -> assertEquals("Nicolás Quezada", resultado.getNombreCliente()),
                () -> assertEquals(5, resultado.getCalificacion()),
                () -> assertTrue(resultado.getActiva())
        );

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate).getForObject(URL_PRODUCTO_1, ProductoDTO.class);
        verify(resenaRepository).save(any(Resena.class));
    }

    @Test
    void actualizarResena_cuandoClienteNoExiste_deberiaLanzarRuntimeException() {
        //given
        ResenaDTO resenaDTO = crearResenaDTO(1L, true);
        HttpClientErrorException.NotFound error = mock(HttpClientErrorException.NotFound.class);

        when(restTemplate.getForObject(URL_USUARIO_1, UsuarioDTO.class)).thenThrow(error);

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> resenaService.actualizarResena(resenaDTO)
        );

        //then
        assertEquals("El cliente con ID 1 no existe", exception.getMessage());

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate, never()).getForObject(URL_PRODUCTO_1, ProductoDTO.class);
        verify(resenaRepository, never()).save(any(Resena.class));
    }

    @Test
    void actualizarResena_cuandoProductoNoExiste_deberiaLanzarRuntimeException() {
        //given
        ResenaDTO resenaDTO = crearResenaDTO(1L, true);
        HttpClientErrorException.NotFound error = mock(HttpClientErrorException.NotFound.class);

        simularClienteValido();
        when(restTemplate.getForObject(URL_PRODUCTO_1, ProductoDTO.class)).thenThrow(error);

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> resenaService.actualizarResena(resenaDTO)
        );

        //then
        assertEquals("El producto con ID 1 no existe", exception.getMessage());

        verify(restTemplate).getForObject(URL_USUARIO_1, UsuarioDTO.class);
        verify(restTemplate).getForObject(URL_PRODUCTO_1, ProductoDTO.class);
        verify(resenaRepository, never()).save(any(Resena.class));
    }

    @Test
    void existeResenaPorId_cuandoExiste_deberiaRetornarTrue() {
        //given
        Long id = 1L;
        when(resenaRepository.existsById(id)).thenReturn(true);

        //when
        boolean resultado = resenaService.existeResenaPorId(id);

        //then
        assertTrue(resultado);

        verify(resenaRepository).existsById(id);
    }

    @Test
    void existeResenaPorId_cuandoNoExiste_deberiaRetornarFalse() {
        //given
        Long id = 99L;
        when(resenaRepository.existsById(id)).thenReturn(false);

        //when
        boolean resultado = resenaService.existeResenaPorId(id);

        //then
        assertFalse(resultado);

        verify(resenaRepository).existsById(id);
    }

    @Test
    void eliminarResena_deberiaEliminarResenaPorId() {
        //given
        Long id = 1L;

        //when
        resenaService.eliminarResena(id);

        //then
        verify(resenaRepository).deleteById(id);
    }

    @Test
    void listarResenasPorProducto_deberiaRetornarResenasDeUnProducto() {
        //given
        Long idProducto = 1L;
        Resena resena = crearResena(1L, true);

        when(resenaRepository.findByIdProducto(idProducto)).thenReturn(List.of(resena));

        //when
        List<ResenaDTO> resultado = resenaService.listarResenasPorProducto(idProducto);

        //then
        assertEquals(1, resultado.size());
        assertEquals(idProducto, resultado.get(0).getIdProducto());

        verify(resenaRepository).findByIdProducto(idProducto);
    }

    @Test
    void listarResenasActivas_deberiaRetornarResenasActivas() {
        //given
        Resena resena = crearResena(1L, true);

        when(resenaRepository.findByActiva(true)).thenReturn(List.of(resena));

        //when
        List<ResenaDTO> resultado = resenaService.listarResenasActivas();

        //then
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getActiva());

        verify(resenaRepository).findByActiva(true);
    }

    @Test
    void listarResenasPorCalificacion_deberiaRetornarResenasDeUnaCalificacion() {
        //given
        Integer calificacion = 5;
        Resena resena = crearResena(1L, true);

        when(resenaRepository.findByCalificacion(calificacion)).thenReturn(List.of(resena));

        //when
        List<ResenaDTO> resultado = resenaService.listarResenasPorCalificacion(calificacion);

        //then
        assertEquals(1, resultado.size());
        assertEquals(calificacion, resultado.get(0).getCalificacion());

        verify(resenaRepository).findByCalificacion(calificacion);
    }

    private void simularClienteValido() {
        when(restTemplate.getForObject(URL_USUARIO_1, UsuarioDTO.class))
                .thenReturn(crearUsuarioDTO(true));
    }

    private void simularProductoValido() {
        when(restTemplate.getForObject(URL_PRODUCTO_1, ProductoDTO.class))
                .thenReturn(crearProductoDTO());
    }

    private UsuarioDTO crearUsuarioDTO(Boolean estado) {
        return new UsuarioDTO(
                1L,
                "Nicolás",
                "Quezada",
                "nicolas.quezada@perfulandia.cl",
                "Concepción, Región del Biobío",
                estado,
                1L,
                "CLIENTE"
        );
    }

    private ProductoDTO crearProductoDTO() {
        return new ProductoDTO(
                1L,
                "Eau de Parfum Rosas del Sur",
                "Perfume floral de larga duración para uso diario",
                "PERFUMERIA",
                10,
                24990.0,
                true,
                1L
        );
    }

    private Resena crearResena(Long id, Boolean activa) {
        return new Resena(
                id,
                1L,
                1L,
                "Nicolás Quezada",
                5,
                "Aroma elegante, buena fijación y presentación muy cuidada.",
                activa
        );
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