package com.example.tiendas_service.service;

import com.example.tiendas_service.dto.TiendaDTO;
import com.example.tiendas_service.model.Tienda;
import com.example.tiendas_service.repository.TiendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TiendaService {

    private final TiendaRepository tiendaRepository;

    public TiendaService(TiendaRepository tiendaRepository) {
        this.tiendaRepository = tiendaRepository;
    }

    public List<TiendaDTO> listarTiendas() {
        return tiendaRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public Optional<TiendaDTO> buscarTiendaPorId(Long id) {
        return tiendaRepository.findById(id)
                .map(this::convertirADTO);
    }

    public TiendaDTO guardarTienda(TiendaDTO tiendaDTO) {
        Tienda tienda = convertirAEntidad(tiendaDTO);
        Tienda tiendaGuardada = tiendaRepository.save(tienda);
        return convertirADTO(tiendaGuardada);
    }

    public boolean existeTiendaPorId(Long id) {
        return tiendaRepository.existsById(id);
    }

    public void eliminarTienda(Long id) {
        tiendaRepository.deleteById(id);
    }

    public Optional<TiendaDTO> actualizarEstadoTienda(Long id, Boolean activa) {
    return tiendaRepository.findById(id)
            .map(tienda -> {
                tienda.setActiva(activa);
                Tienda tiendaActualizada = tiendaRepository.save(tienda);
                return convertirADTO(tiendaActualizada);
            });
    }

    private TiendaDTO convertirADTO(Tienda tienda) {
        return new TiendaDTO(
                tienda.getIdTienda(),
                tienda.getNombre(),
                tienda.getDireccion(),
                tienda.getComuna(),
                tienda.getCiudad(),
                tienda.getRegion(),
                tienda.getTelefono(),
                tienda.getPersonalAsignado(),
                tienda.getHorarioApertura(),
                tienda.getHorarioCierre(),
                tienda.getActiva(),
                tienda.getPoliticasLocales()
        );
    }

    private Tienda convertirAEntidad(TiendaDTO tiendaDTO) {
        return new Tienda(
                tiendaDTO.getIdTienda(),
                tiendaDTO.getNombre(),
                tiendaDTO.getDireccion(),
                tiendaDTO.getComuna(),
                tiendaDTO.getCiudad(),
                tiendaDTO.getRegion(),
                tiendaDTO.getTelefono(),
                tiendaDTO.getPersonalAsignado(),
                tiendaDTO.getHorarioApertura(),
                tiendaDTO.getHorarioCierre(),
                tiendaDTO.getActiva(),
                tiendaDTO.getPoliticasLocales()
        );
    }
}