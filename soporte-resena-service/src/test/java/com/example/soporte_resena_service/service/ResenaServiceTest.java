package com.example.soporte_resena_service.service;

import com.example.soporte_resena_service.dto.ProductoDTO;
import com.example.soporte_resena_service.dto.ResenaDTO;
import com.example.soporte_resena_service.model.Resena;
import com.example.soporte_resena_service.repository.ResenaRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResenaServiceTest {

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
                () -> assertEquals(1L, resultado.get(0).getIdProducto()),
                () -> assertEquals("JUAN PEREZ", resultado.get(0).getNombreCliente()),
                () -> assertEquals(5, resultado.get(0).getCalificacion()),
                () -> assertEquals("Excelente producto ecológico", resultado.get(0).getComentario()),
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
                () -> assertEquals(1L, resultado.get().getIdProducto()),
                () -> assertEquals("JUAN PEREZ", resultado.get().getNombreCliente()),
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
    void guardarResena_cuandoProductoExiste_deberiaGuardarYRetornarResenaDTO() {
        //given
        ResenaDTO resenaDTO = crearResenaDTO(null, true);
        Resena resenaGuardada = crearResena(1L, true);

        String url = "http://localhost:8092/api/v1/productos/1";

        when(restTemplate.getForObject(url, ProductoDTO.class)).thenReturn(crearProductoDTO(1L));
        when(resenaRepository.save(any(Resena.class))).thenReturn(resenaGuardada);

        //when
        ResenaDTO resultado = resenaService.guardarResena(resenaDTO);

        //then
        assertAll(
                () -> assertEquals(1L, resultado.getIdResena()),
                () -> assertEquals(1L, resultado.getIdProducto()),
                () -> assertEquals("JUAN PEREZ", resultado.getNombreCliente()),
                () -> assertEquals(5, resultado.getCalificacion()),
                () -> assertEquals("Excelente producto ecológico", resultado.getComentario()),
                () -> assertTrue(resultado.getActiva())
        );

        verify(restTemplate).getForObject(url, ProductoDTO.class);
        verify(resenaRepository).save(any(Resena.class));
    }

    @Test
    void guardarResena_cuandoProductoNoExiste_deberiaLanzarRuntimeException() {
        //given
        ResenaDTO resenaDTO = crearResenaDTO(null, true);
        String url = "http://localhost:8092/api/v1/productos/1";

        HttpClientErrorException errorNotFound = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                HttpHeaders.EMPTY,
                new byte[0],
                StandardCharsets.UTF_8
        );

        when(restTemplate.getForObject(url, ProductoDTO.class)).thenThrow(errorNotFound);

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> resenaService.guardarResena(resenaDTO)
        );

        //then
        assertEquals("El producto con ID 1 no existe", exception.getMessage());

        verify(restTemplate).getForObject(url, ProductoDTO.class);
        verify(resenaRepository, never()).save(any(Resena.class));
    }

    @Test
    void actualizarResena_cuandoProductoExiste_deberiaActualizarYRetornarResenaDTO() {
        //given
        ResenaDTO resenaDTO = crearResenaDTO(1L, true);
        Resena resenaActualizada = crearResena(1L, true);

        String url = "http://localhost:8092/api/v1/productos/1";

        when(restTemplate.getForObject(url, ProductoDTO.class)).thenReturn(crearProductoDTO(1L));
        when(resenaRepository.save(any(Resena.class))).thenReturn(resenaActualizada);

        //when
        ResenaDTO resultado = resenaService.actualizarResena(resenaDTO);

        //then
        assertAll(
                () -> assertEquals(1L, resultado.getIdResena()),
                () -> assertEquals(1L, resultado.getIdProducto()),
                () -> assertEquals("JUAN PEREZ", resultado.getNombreCliente()),
                () -> assertEquals(5, resultado.getCalificacion()),
                () -> assertTrue(resultado.getActiva())
        );

        verify(restTemplate).getForObject(url, ProductoDTO.class);
        verify(resenaRepository).save(any(Resena.class));
    }

    @Test
    void actualizarResena_cuandoProductoNoExiste_deberiaLanzarRuntimeException() {
        //given
        ResenaDTO resenaDTO = crearResenaDTO(1L, true);
        String url = "http://localhost:8092/api/v1/productos/1";

        HttpClientErrorException errorNotFound = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                HttpHeaders.EMPTY,
                new byte[0],
                StandardCharsets.UTF_8
        );

        when(restTemplate.getForObject(url, ProductoDTO.class)).thenThrow(errorNotFound);

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> resenaService.actualizarResena(resenaDTO)
        );

        //then
        assertEquals("El producto con ID 1 no existe", exception.getMessage());

        verify(restTemplate).getForObject(url, ProductoDTO.class);
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
        assertAll(
                () -> assertEquals(1, resultado.size()),
                () -> assertEquals(idProducto, resultado.get(0).getIdProducto()),
                () -> assertEquals("JUAN PEREZ", resultado.get(0).getNombreCliente())
        );

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
        assertAll(
                () -> assertEquals(1, resultado.size()),
                () -> assertTrue(resultado.get(0).getActiva()),
                () -> assertEquals("JUAN PEREZ", resultado.get(0).getNombreCliente())
        );

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
        assertAll(
                () -> assertEquals(1, resultado.size()),
                () -> assertEquals(calificacion, resultado.get(0).getCalificacion()),
                () -> assertEquals("Excelente producto ecológico", resultado.get(0).getComentario())
        );

        verify(resenaRepository).findByCalificacion(calificacion);
    }

    private Resena crearResena(Long id, Boolean activa) {
        return new Resena(
                id,
                1L,
                "JUAN PEREZ",
                5,
                "Excelente producto ecológico",
                activa
        );
    }

    private ResenaDTO crearResenaDTO(Long id, Boolean activa) {
        return new ResenaDTO(
                id,
                1L,
                "JUAN PEREZ",
                5,
                "Excelente producto ecológico",
                activa
        );
    }

    private ProductoDTO crearProductoDTO(Long id) {
        return new ProductoDTO(
                id,
                "SHAMPOO ECOLOGICO",
                "Producto ecológico para el cuidado personal",
                "CUIDADO PERSONAL",
                20,
                4990.0,
                true,
                1L
        );
    }
}