package com.example.inventario_catalogo_service.controller;

import com.example.inventario_catalogo_service.dto.ProductoDTO;
import com.example.inventario_catalogo_service.service.ProductoService;

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

@WebMvcTest(ProductoController.class)
@ActiveProfiles("test")
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService productoService;

    @Test
    void listarProductos_deberiaRetornar200YListaDeProductos() throws Exception {
        //given
        ProductoDTO producto = crearProductoDTO(1L, true);
        Mockito.when(productoService.listarProductos()).thenReturn(List.of(producto));

        //when //then
        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idProducto").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("SHAMPOO ECOLOGICO"))
                .andExpect(jsonPath("$[0].categoria").value("CUIDADO PERSONAL"))
                .andExpect(jsonPath("$[0].disponible").value(true));

        Mockito.verify(productoService).listarProductos();
    }

    @Test
    void buscarProductoPorId_cuandoExiste_deberiaRetornar200YProducto() throws Exception {
        //given
        Long id = 1L;
        ProductoDTO producto = crearProductoDTO(id, true);

        Mockito.when(productoService.buscarProductoPorId(id)).thenReturn(Optional.of(producto));

        //when //then
        mockMvc.perform(get("/api/v1/productos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProducto").value(id))
                .andExpect(jsonPath("$.nombre").value("SHAMPOO ECOLOGICO"))
                .andExpect(jsonPath("$.stock").value(20))
                .andExpect(jsonPath("$.precio").value(4990.0))
                .andExpect(jsonPath("$.idTienda").value(1L));

        Mockito.verify(productoService).buscarProductoPorId(id);
    }

    @Test
    void buscarProductoPorId_cuandoNoExiste_deberiaRetornar404() throws Exception {
        //given
        Long id = 99L;

        Mockito.when(productoService.buscarProductoPorId(id)).thenReturn(Optional.empty());

        //when //then
        mockMvc.perform(get("/api/v1/productos/{id}", id))
                .andExpect(status().isNotFound());

        Mockito.verify(productoService).buscarProductoPorId(id);
    }

    @Test
    void crearProducto_conDatosValidos_deberiaRetornar201YProductoCreado() throws Exception {
        //given
        ProductoDTO productoCreado = crearProductoDTO(1L, true);

        Mockito.when(productoService.guardarProducto(any(ProductoDTO.class))).thenReturn(productoCreado);

        String body = """
                {
                    "nombre": "SHAMPOO ECOLOGICO",
                    "descripcion": "Producto ecológico para el cuidado personal",
                    "categoria": "CUIDADO PERSONAL",
                    "stock": 20,
                    "precio": 4990.0,
                    "disponible": true,
                    "idTienda": 1
                }
                """;

        //when //then
        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idProducto").value(1L))
                .andExpect(jsonPath("$.nombre").value("SHAMPOO ECOLOGICO"))
                .andExpect(jsonPath("$.categoria").value("CUIDADO PERSONAL"))
                .andExpect(jsonPath("$.disponible").value(true));

        Mockito.verify(productoService).guardarProducto(any(ProductoDTO.class));
    }

    @Test
    void crearProducto_conDatosInvalidos_deberiaRetornar400() throws Exception {
        //given
        String body = """
                {
                    "nombre": "",
                    "descripcion": "Producto ecológico para el cuidado personal",
                    "categoria": "CUIDADO PERSONAL",
                    "stock": 20,
                    "precio": 4990.0,
                    "disponible": true,
                    "idTienda": 1
                }
                """;

        //when //then
        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("ERROR VALIDACIÓN"))
                .andExpect(jsonPath("$.mensajes.nombre").value("El NOMBRE del PRODUCTO es OBLIGATORIO"));

        Mockito.verify(productoService, never()).guardarProducto(any(ProductoDTO.class));
    }

    @Test
    void crearProducto_cuandoServiceLanzaRuntimeException_deberiaRetornar400() throws Exception {
        //given
        Mockito.when(productoService.guardarProducto(any(ProductoDTO.class)))
                .thenThrow(new RuntimeException("La tienda con ID 1 no existe"));

        String body = """
                {
                    "nombre": "SHAMPOO ECOLOGICO",
                    "descripcion": "Producto ecológico para el cuidado personal",
                    "categoria": "CUIDADO PERSONAL",
                    "stock": 20,
                    "precio": 4990.0,
                    "disponible": true,
                    "idTienda": 1
                }
                """;

        //when //then
        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("ERROR DE SOLICITUD"))
                .andExpect(jsonPath("$.mensaje").value("La tienda con ID 1 no existe"));

        Mockito.verify(productoService).guardarProducto(any(ProductoDTO.class));
    }

    @Test
    void actualizarProducto_cuandoExiste_deberiaRetornar200YProductoActualizado() throws Exception {
        //given
        Long id = 1L;
        ProductoDTO productoActualizado = crearProductoDTO(id, true);

        Mockito.when(productoService.existeProductoPorId(id)).thenReturn(true);
        Mockito.when(productoService.guardarProducto(any(ProductoDTO.class))).thenReturn(productoActualizado);

        String body = """
                {
                    "nombre": "SHAMPOO ECOLOGICO",
                    "descripcion": "Producto ecológico para el cuidado personal",
                    "categoria": "CUIDADO PERSONAL",
                    "stock": 20,
                    "precio": 4990.0,
                    "disponible": true,
                    "idTienda": 1
                }
                """;

        //when //then
        mockMvc.perform(put("/api/v1/productos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProducto").value(id))
                .andExpect(jsonPath("$.nombre").value("SHAMPOO ECOLOGICO"))
                .andExpect(jsonPath("$.stock").value(20));

        Mockito.verify(productoService).existeProductoPorId(id);
        Mockito.verify(productoService).guardarProducto(any(ProductoDTO.class));
    }

    @Test
    void actualizarProducto_cuandoNoExiste_deberiaRetornar404() throws Exception {
        //given
        Long id = 99L;

        Mockito.when(productoService.existeProductoPorId(id)).thenReturn(false);

        String body = """
                {
                    "nombre": "SHAMPOO ECOLOGICO",
                    "descripcion": "Producto ecológico para el cuidado personal",
                    "categoria": "CUIDADO PERSONAL",
                    "stock": 20,
                    "precio": 4990.0,
                    "disponible": true,
                    "idTienda": 1
                }
                """;

        //when //then
        mockMvc.perform(put("/api/v1/productos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());

        Mockito.verify(productoService).existeProductoPorId(id);
        Mockito.verify(productoService, never()).guardarProducto(any(ProductoDTO.class));
    }

    @Test
    void eliminarProducto_cuandoExiste_deberiaRetornar204() throws Exception {
        //given
        Long id = 1L;

        Mockito.when(productoService.existeProductoPorId(id)).thenReturn(true);

        //when //then
        mockMvc.perform(delete("/api/v1/productos/{id}", id))
                .andExpect(status().isNoContent());

        Mockito.verify(productoService).existeProductoPorId(id);
        Mockito.verify(productoService).eliminarProducto(id);
    }

    @Test
    void eliminarProducto_cuandoNoExiste_deberiaRetornar404() throws Exception {
        //given
        Long id = 99L;

        Mockito.when(productoService.existeProductoPorId(id)).thenReturn(false);

        //when //then
        mockMvc.perform(delete("/api/v1/productos/{id}", id))
                .andExpect(status().isNotFound());

        Mockito.verify(productoService).existeProductoPorId(id);
        Mockito.verify(productoService, never()).eliminarProducto(id);
    }

    @Test
    void listarProductosPorTienda_deberiaRetornar200YProductosDeUnaTienda() throws Exception {
        //given
        Long idTienda = 1L;
        ProductoDTO producto = crearProductoDTO(1L, true);

        Mockito.when(productoService.listarProductosPorTienda(idTienda)).thenReturn(List.of(producto));

        //when //then
        mockMvc.perform(get("/api/v1/productos/tienda/{idTienda}", idTienda))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idTienda").value(idTienda))
                .andExpect(jsonPath("$[0].nombre").value("SHAMPOO ECOLOGICO"));

        Mockito.verify(productoService).listarProductosPorTienda(idTienda);
    }

    @Test
    void listarProductosDisponibles_deberiaRetornar200YProductosDisponibles() throws Exception {
        //given
        ProductoDTO producto = crearProductoDTO(1L, true);

        Mockito.when(productoService.listarProductosDisponibles()).thenReturn(List.of(producto));

        //when //then
        mockMvc.perform(get("/api/v1/productos/disponibles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].disponible").value(true))
                .andExpect(jsonPath("$[0].nombre").value("SHAMPOO ECOLOGICO"));

        Mockito.verify(productoService).listarProductosDisponibles();
    }

    @Test
    void listarProductosPorCategoria_deberiaRetornar200YProductosDeUnaCategoria() throws Exception {
        //given
        String categoria = "CUIDADO PERSONAL";
        ProductoDTO producto = crearProductoDTO(1L, true);

        Mockito.when(productoService.listarProductosPorCategoria(categoria)).thenReturn(List.of(producto));

        //when //then
        mockMvc.perform(get("/api/v1/productos/categoria/{categoria}", categoria))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].categoria").value(categoria))
                .andExpect(jsonPath("$[0].nombre").value("SHAMPOO ECOLOGICO"));

        Mockito.verify(productoService).listarProductosPorCategoria(categoria);
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
}