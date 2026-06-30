package com.example.inventario_catalogo_service.service;

import com.example.inventario_catalogo_service.dto.ProductoDTO;
import com.example.inventario_catalogo_service.dto.TiendaDTO;
import com.example.inventario_catalogo_service.model.Producto;
import com.example.inventario_catalogo_service.repository.ProductoRepository;

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
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void listarProductos_deberiaRetornarListaDeProductos() {
        //given
        Producto producto = crearProducto(1L, true);
        when(productoRepository.findAll()).thenReturn(List.of(producto));

        //when
        List<ProductoDTO> resultado = productoService.listarProductos();

        //then
        assertAll(
                () -> assertEquals(1, resultado.size()),
                () -> assertEquals(1L, resultado.get(0).getIdProducto()),
                () -> assertEquals("SHAMPOO ECOLOGICO", resultado.get(0).getNombre()),
                () -> assertEquals("CUIDADO PERSONAL", resultado.get(0).getCategoria()),
                () -> assertEquals(20, resultado.get(0).getStock()),
                () -> assertEquals(4990.0, resultado.get(0).getPrecio()),
                () -> assertTrue(resultado.get(0).getDisponible()),
                () -> assertEquals(1L, resultado.get(0).getIdTienda())
        );

        verify(productoRepository).findAll();
    }

    @Test
    void buscarProductoPorId_cuandoExiste_deberiaRetornarProducto() {
        //given
        Long id = 1L;
        Producto producto = crearProducto(id, true);

        when(productoRepository.findById(id)).thenReturn(Optional.of(producto));

        //when
        Optional<ProductoDTO> resultado = productoService.buscarProductoPorId(id);

        //then
        assertTrue(resultado.isPresent());
        assertAll(
                () -> assertEquals(id, resultado.get().getIdProducto()),
                () -> assertEquals("SHAMPOO ECOLOGICO", resultado.get().getNombre()),
                () -> assertEquals("CUIDADO PERSONAL", resultado.get().getCategoria()),
                () -> assertEquals(1L, resultado.get().getIdTienda())
        );

        verify(productoRepository).findById(id);
    }

    @Test
    void buscarProductoPorId_cuandoNoExiste_deberiaRetornarOptionalVacio() {
        //given
        Long id = 99L;

        when(productoRepository.findById(id)).thenReturn(Optional.empty());

        //when
        Optional<ProductoDTO> resultado = productoService.buscarProductoPorId(id);

        //then
        assertTrue(resultado.isEmpty());

        verify(productoRepository).findById(id);
    }

    @Test
    void guardarProducto_cuandoTiendaExiste_deberiaGuardarYRetornarProductoDTO() {
        //given
        ProductoDTO productoDTO = crearProductoDTO(null, true);
        Producto productoGuardado = crearProducto(1L, true);

        String url = "http://localhost:8091/api/v1/tiendas/1";

        when(restTemplate.getForObject(url, TiendaDTO.class)).thenReturn(crearTiendaDTO(1L));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoGuardado);

        //when
        ProductoDTO resultado = productoService.guardarProducto(productoDTO);

        //then
        assertAll(
                () -> assertEquals(1L, resultado.getIdProducto()),
                () -> assertEquals("SHAMPOO ECOLOGICO", resultado.getNombre()),
                () -> assertEquals("Producto ecológico para el cuidado personal", resultado.getDescripcion()),
                () -> assertEquals("CUIDADO PERSONAL", resultado.getCategoria()),
                () -> assertEquals(20, resultado.getStock()),
                () -> assertEquals(4990.0, resultado.getPrecio()),
                () -> assertTrue(resultado.getDisponible()),
                () -> assertEquals(1L, resultado.getIdTienda())
        );

        verify(restTemplate).getForObject(url, TiendaDTO.class);
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void guardarProducto_cuandoTiendaNoExiste_deberiaLanzarRuntimeException() {
        //given
        ProductoDTO productoDTO = crearProductoDTO(null, true);
        String url = "http://localhost:8091/api/v1/tiendas/1";

        HttpClientErrorException errorNotFound = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                HttpHeaders.EMPTY,
                new byte[0],
                StandardCharsets.UTF_8
        );

        when(restTemplate.getForObject(url, TiendaDTO.class)).thenThrow(errorNotFound);

        //when
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productoService.guardarProducto(productoDTO)
        );

        //then
        assertEquals("La tienda con ID 1 no existe", exception.getMessage());

        verify(restTemplate).getForObject(url, TiendaDTO.class);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void existeProductoPorId_cuandoExiste_deberiaRetornarTrue() {
        //given
        Long id = 1L;
        when(productoRepository.existsById(id)).thenReturn(true);

        //when
        boolean resultado = productoService.existeProductoPorId(id);

        //then
        assertTrue(resultado);

        verify(productoRepository).existsById(id);
    }

    @Test
    void existeProductoPorId_cuandoNoExiste_deberiaRetornarFalse() {
        //given
        Long id = 99L;
        when(productoRepository.existsById(id)).thenReturn(false);

        //when
        boolean resultado = productoService.existeProductoPorId(id);

        //then
        assertFalse(resultado);

        verify(productoRepository).existsById(id);
    }

    @Test
    void eliminarProducto_deberiaEliminarProductoPorId() {
        //given
        Long id = 1L;

        //when
        productoService.eliminarProducto(id);

        //then
        verify(productoRepository).deleteById(id);
    }

    @Test
    void listarProductosPorTienda_deberiaRetornarProductosDeUnaTienda() {
        //given
        Long idTienda = 1L;
        Producto producto = crearProducto(1L, true);

        when(productoRepository.findByIdTienda(idTienda)).thenReturn(List.of(producto));

        //when
        List<ProductoDTO> resultado = productoService.listarProductosPorTienda(idTienda);

        //then
        assertAll(
                () -> assertEquals(1, resultado.size()),
                () -> assertEquals(idTienda, resultado.get(0).getIdTienda()),
                () -> assertEquals("SHAMPOO ECOLOGICO", resultado.get(0).getNombre())
        );

        verify(productoRepository).findByIdTienda(idTienda);
    }

    @Test
    void listarProductosDisponibles_deberiaRetornarProductosDisponibles() {
        //given
        Producto producto = crearProducto(1L, true);

        when(productoRepository.findByDisponible(true)).thenReturn(List.of(producto));

        //when
        List<ProductoDTO> resultado = productoService.listarProductosDisponibles();

        //then
        assertAll(
                () -> assertEquals(1, resultado.size()),
                () -> assertTrue(resultado.get(0).getDisponible()),
                () -> assertEquals("SHAMPOO ECOLOGICO", resultado.get(0).getNombre())
        );

        verify(productoRepository).findByDisponible(true);
    }

    @Test
    void listarProductosPorCategoria_deberiaRetornarProductosDeUnaCategoria() {
        //given
        String categoria = "CUIDADO PERSONAL";
        Producto producto = crearProducto(1L, true);

        when(productoRepository.findByCategoria(categoria)).thenReturn(List.of(producto));

        //when
        List<ProductoDTO> resultado = productoService.listarProductosPorCategoria(categoria);

        //then
        assertAll(
                () -> assertEquals(1, resultado.size()),
                () -> assertEquals(categoria, resultado.get(0).getCategoria()),
                () -> assertEquals("SHAMPOO ECOLOGICO", resultado.get(0).getNombre())
        );

        verify(productoRepository).findByCategoria(categoria);
    }

    private Producto crearProducto(Long id, Boolean disponible) {
        return new Producto(
                id,
                "SHAMPOO ECOLOGICO",
                "Producto ecológico para el cuidado personal",
                "CUIDADO PERSONAL",
                20,
                4990.0,
                disponible,
                1L
        );
    }

    private ProductoDTO crearProductoDTO(Long id, Boolean disponible) {
        return new ProductoDTO(
                id,
                "SHAMPOO ECOLOGICO",
                "Producto ecológico para el cuidado personal",
                "CUIDADO PERSONAL",
                20,
                4990.0,
                disponible,
                1L
        );
    }

    private TiendaDTO crearTiendaDTO(Long id) {
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
                true,
                "Uso de bolsas reutilizables"
        );
    }
}