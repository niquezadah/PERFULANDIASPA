package com.example.soporte_resena_service.service;

import com.example.soporte_resena_service.dto.ProductoDTO;
import com.example.soporte_resena_service.dto.ResenaDTO;
import com.example.soporte_resena_service.dto.UsuarioDTO;
import com.example.soporte_resena_service.model.Resena;
import com.example.soporte_resena_service.repository.ResenaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ResenaService {

    private final RestTemplate restTemplate;
    private final ResenaRepository resenaRepository;

    public ResenaService(ResenaRepository resenaRepository, RestTemplate restTemplate) {
        this.resenaRepository = resenaRepository;
        this.restTemplate = restTemplate;
    }

    public List<ResenaDTO> listarResenas() {
        return resenaRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public Optional<ResenaDTO> buscarResenaPorId(Long id) {
        return resenaRepository.findById(id)
                .map(this::convertirADTO);
    }

    public ResenaDTO guardarResena(ResenaDTO resenaDTO) {
        validarClienteExiste(resenaDTO.getIdCliente());
        validarProductoExiste(resenaDTO.getIdProducto());

        Resena resena = convertirAEntidad(resenaDTO);
        Resena resenaGuardada = resenaRepository.save(resena);
        return convertirADTO(resenaGuardada);
    }

    public ResenaDTO actualizarResena(ResenaDTO resenaDTO) {
        validarClienteExiste(resenaDTO.getIdCliente());
        validarProductoExiste(resenaDTO.getIdProducto());

        Resena resena = convertirAEntidad(resenaDTO);
        Resena resenaActualizada = resenaRepository.save(resena);
        return convertirADTO(resenaActualizada);
    }

    public boolean existeResenaPorId(Long id) {
        return resenaRepository.existsById(id);
    }

    public void eliminarResena(Long id) {
        resenaRepository.deleteById(id);
    }

    public List<ResenaDTO> listarResenasPorProducto(Long idProducto) {
        return resenaRepository.findByIdProducto(idProducto)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<ResenaDTO> listarResenasActivas() {
        return resenaRepository.findByActiva(true)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<ResenaDTO> listarResenasPorCalificacion(Integer calificacion) {
        return resenaRepository.findByCalificacion(calificacion)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    private void validarClienteExiste(Long idCliente) {
        UsuarioDTO usuarioDTO;

        try {
            String url = "http://localhost:8082/api/usuarios/" + idCliente;
            usuarioDTO = restTemplate.getForObject(url, UsuarioDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RuntimeException("El cliente con ID " + idCliente + " no existe");
        } catch (RestClientException ex) {
            throw new RuntimeException("No se pudo validar el cliente con ID " + idCliente);
        }

        if (usuarioDTO == null) {
            throw new RuntimeException("El cliente con ID " + idCliente + " no existe");
        }

        if (!Boolean.TRUE.equals(usuarioDTO.getEstado())) {
            throw new RuntimeException("El cliente con ID " + idCliente + " está inactivo");
        }
    }

    private void validarProductoExiste(Long idProducto) {
        ProductoDTO productoDTO;

        try {
            String url = "http://localhost:8092/api/v1/productos/" + idProducto;
            productoDTO = restTemplate.getForObject(url, ProductoDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RuntimeException("El producto con ID " + idProducto + " no existe");
        } catch (RestClientException ex) {
            throw new RuntimeException("No se pudo validar el producto con ID " + idProducto);
        }

        if (productoDTO == null) {
            throw new RuntimeException("El producto con ID " + idProducto + " no existe");
        }
    }

    private ResenaDTO convertirADTO(Resena resena) {
        return new ResenaDTO(
                resena.getIdResena(),
                resena.getIdCliente(),
                resena.getIdProducto(),
                resena.getNombreCliente(),
                resena.getCalificacion(),
                resena.getComentario(),
                resena.getActiva()
        );
    }

    private Resena convertirAEntidad(ResenaDTO resenaDTO) {
        return new Resena(
                resenaDTO.getIdResena(),
                resenaDTO.getIdCliente(),
                resenaDTO.getIdProducto(),
                resenaDTO.getNombreCliente(),
                resenaDTO.getCalificacion(),
                resenaDTO.getComentario(),
                resenaDTO.getActiva()
        );
    }
}