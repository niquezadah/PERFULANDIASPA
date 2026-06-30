package com.example.tiendas_service.service;

import com.example.tiendas_service.dto.TiendaDTO;
import com.example.tiendas_service.model.Tienda;
import com.example.tiendas_service.repository.TiendaRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TiendaServiceTest {

    @Mock
    private TiendaRepository tiendaRepository;

    @InjectMocks
    private TiendaService tiendaService;

    @Test
    void listarTiendas_deberiaRetornarListaDeTiendas() {
        //given
        Tienda tienda = crearTienda(1L, true);
        when(tiendaRepository.findAll()).thenReturn(List.of(tienda));

        //when
        List<TiendaDTO> resultado = tiendaService.listarTiendas();

        //then
        assertAll(
                () -> assertEquals(1, resultado.size()),
                () -> assertEquals(1L, resultado.get(0).getIdTienda()),
                () -> assertEquals("PERFULANDIA CONCEPCION", resultado.get(0).getNombre()),
                () -> assertEquals("BIO BIO", resultado.get(0).getRegion()),
                () -> assertTrue(resultado.get(0).getActiva())
        );

        verify(tiendaRepository).findAll();
    }

    @Test
    void buscarTiendaPorId_cuandoExiste_deberiaRetornarTienda() {
        //given
        Long id = 1L;
        Tienda tienda = crearTienda(id, true);
        when(tiendaRepository.findById(id)).thenReturn(Optional.of(tienda));

        //when
        Optional<TiendaDTO> resultado = tiendaService.buscarTiendaPorId(id);

        //then
        assertTrue(resultado.isPresent());
        assertAll(
                () -> assertEquals(id, resultado.get().getIdTienda()),
                () -> assertEquals("PERFULANDIA CONCEPCION", resultado.get().getNombre()),
                () -> assertEquals("CONCEPCION", resultado.get().getCiudad()),
                () -> assertTrue(resultado.get().getActiva())
        );

        verify(tiendaRepository).findById(id);
    }

    @Test
    void buscarTiendaPorId_cuandoNoExiste_deberiaRetornarOptionalVacio() {
        //given
        Long id = 99L;
        when(tiendaRepository.findById(id)).thenReturn(Optional.empty());

        //when
        Optional<TiendaDTO> resultado = tiendaService.buscarTiendaPorId(id);

        //then
        assertTrue(resultado.isEmpty());

        verify(tiendaRepository).findById(id);
    }

    @Test
    void guardarTienda_deberiaGuardarYRetornarTiendaDTO() {
        //given
        TiendaDTO tiendaDTO = crearTiendaDTO(null, true);
        Tienda tiendaGuardada = crearTienda(1L, true);

        when(tiendaRepository.save(any(Tienda.class))).thenReturn(tiendaGuardada);

        //when
        TiendaDTO resultado = tiendaService.guardarTienda(tiendaDTO);

        //then
        assertAll(
                () -> assertEquals(1L, resultado.getIdTienda()),
                () -> assertEquals("PERFULANDIA CONCEPCION", resultado.getNombre()),
                () -> assertEquals("PEDRO DE VALDIVIA 123", resultado.getDireccion()),
                () -> assertEquals("BIO BIO", resultado.getRegion()),
                () -> assertTrue(resultado.getActiva())
        );

        verify(tiendaRepository).save(any(Tienda.class));
    }

    @Test
    void existeTiendaPorId_cuandoExiste_deberiaRetornarTrue() {
        //given
        Long id = 1L;
        when(tiendaRepository.existsById(id)).thenReturn(true);

        //when
        boolean resultado = tiendaService.existeTiendaPorId(id);

        //then
        assertTrue(resultado);

        verify(tiendaRepository).existsById(id);
    }

    @Test
    void existeTiendaPorId_cuandoNoExiste_deberiaRetornarFalse() {
        //given
        Long id = 99L;
        when(tiendaRepository.existsById(id)).thenReturn(false);

        //when
        boolean resultado = tiendaService.existeTiendaPorId(id);

        //then
        assertFalse(resultado);

        verify(tiendaRepository).existsById(id);
    }

    @Test
    void eliminarTienda_deberiaEliminarTiendaPorId() {
        //given
        Long id = 1L;

        //when
        tiendaService.eliminarTienda(id);

        //then
        verify(tiendaRepository).deleteById(id);
    }

    @Test
    void actualizarEstadoTienda_cuandoExiste_deberiaActualizarEstadoYRetornarTienda() {
        //given
        Long id = 1L;
        Tienda tienda = crearTienda(id, true);

        when(tiendaRepository.findById(id)).thenReturn(Optional.of(tienda));
        when(tiendaRepository.save(any(Tienda.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        //when
        Optional<TiendaDTO> resultado = tiendaService.actualizarEstadoTienda(id, false);

        //then
        assertTrue(resultado.isPresent());
        assertAll(
                () -> assertEquals(id, resultado.get().getIdTienda()),
                () -> assertEquals("PERFULANDIA CONCEPCION", resultado.get().getNombre()),
                () -> assertFalse(resultado.get().getActiva())
        );

        verify(tiendaRepository).findById(id);
        verify(tiendaRepository).save(any(Tienda.class));
    }

    @Test
    void actualizarEstadoTienda_cuandoNoExiste_deberiaRetornarOptionalVacio() {
        //given
        Long id = 99L;
        when(tiendaRepository.findById(id)).thenReturn(Optional.empty());

        //when
        Optional<TiendaDTO> resultado = tiendaService.actualizarEstadoTienda(id, false);

        //then
        assertTrue(resultado.isEmpty());

        verify(tiendaRepository).findById(id);
        verify(tiendaRepository, never()).save(any(Tienda.class));
    }

    private Tienda crearTienda(Long id, Boolean activa) {
        return new Tienda(
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