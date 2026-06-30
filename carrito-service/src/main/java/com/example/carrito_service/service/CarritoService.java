package com.example.carrito_service.service;

import com.example.carrito_service.dto.CarritoDTO;
import com.example.carrito_service.dto.ProductoDTO;
import com.example.carrito_service.model.Carrito;
import com.example.carrito_service.repository.CarritoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final RestTemplate restTemplate;

    public CarritoService(CarritoRepository carritoRepository, RestTemplate restTemplate) {
        this.carritoRepository = carritoRepository;
        this.restTemplate = restTemplate;
    }

    public List<CarritoDTO> listarCarritos() {
        return carritoRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public Optional<CarritoDTO> buscarCarritoPorId(Long id) {
        return carritoRepository.findById(id)
                .map(this::convertirADTO);
    }

    public CarritoDTO guardarCarrito(CarritoDTO carritoDTO) {
        ProductoDTO productoDTO = validarProductoParaCarrito(carritoDTO.getIdProducto(), carritoDTO.getCantidad());
        Carrito carrito = convertirAEntidad(carritoDTO, productoDTO);
        Carrito carritoGuardado = carritoRepository.save(carrito);
        return convertirADTO(carritoGuardado);
    }

    public CarritoDTO actualizarCarrito(CarritoDTO carritoDTO) {
        return guardarCarrito(carritoDTO);
    }

    public boolean existeCarritoPorId(Long id) {
        return carritoRepository.existsById(id);
    }

    public void eliminarCarrito(Long id) {
        carritoRepository.deleteById(id);
    }

    public List<CarritoDTO> listarCarritosPorCliente(Long idCliente) {
        return carritoRepository.findByIdCliente(idCliente)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<CarritoDTO> listarCarritosPorProducto(Long idProducto) {
        return carritoRepository.findByIdProducto(idProducto)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<CarritoDTO> listarCarritosActivosPorCliente(Long idCliente) {
        return carritoRepository.findByIdClienteAndActivo(idCliente, true)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public Double calcularTotalCarritoPorCliente(Long idCliente) {
        return carritoRepository.findByIdClienteAndActivo(idCliente, true)
                .stream()
                .mapToDouble(Carrito::getSubtotal)
                .sum();
    }

    public void vaciarCarritoPorCliente(Long idCliente) {
        carritoRepository.deleteByIdCliente(idCliente);
    }

    private CarritoDTO convertirADTO(Carrito carrito) {
        return new CarritoDTO(
                carrito.getIdCarrito(),
                carrito.getIdCliente(),
                carrito.getIdProducto(),
                carrito.getNombreProducto(),
                carrito.getCantidad(),
                carrito.getPrecioUnitario(),
                carrito.getSubtotal(),
                carrito.getActivo()
        );
    }

    private Carrito convertirAEntidad(CarritoDTO carritoDTO, ProductoDTO productoDTO) {
        Double subtotal = productoDTO.getPrecio() * carritoDTO.getCantidad();

        return new Carrito(
                carritoDTO.getIdCarrito(),
                carritoDTO.getIdCliente(),
                carritoDTO.getIdProducto(),
                productoDTO.getNombre(),
                carritoDTO.getCantidad(),
                productoDTO.getPrecio(),
                subtotal,
                carritoDTO.getActivo()
        );
    }

    private ProductoDTO validarProductoParaCarrito(Long idProducto, Integer cantidad) {
        ProductoDTO productoDTO;

        try {
            String url = "http://localhost:8092/api/v1/productos/" + idProducto;
            productoDTO = restTemplate.getForObject(url, ProductoDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RuntimeException("El producto con ID " + idProducto + " no existe");
        }

        if (productoDTO == null) {
            throw new RuntimeException("El producto con ID " + idProducto + " no existe");
        }

        if (!Boolean.TRUE.equals(productoDTO.getDisponible())) {
            throw new RuntimeException("El producto con ID " + idProducto + " no está disponible");
        }

        if (productoDTO.getStock() < cantidad) {
            throw new RuntimeException("No hay stock suficiente para el producto con ID " + idProducto);
        }

        return productoDTO;
    }
}
