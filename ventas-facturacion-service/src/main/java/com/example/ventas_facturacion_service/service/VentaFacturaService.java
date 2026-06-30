package com.example.ventas_facturacion_service.service;

import com.example.ventas_facturacion_service.dto.EstadoVentaDTO;
import com.example.ventas_facturacion_service.dto.VentaFacturaDTO;
import com.example.ventas_facturacion_service.model.VentaFactura;
import com.example.ventas_facturacion_service.repository.VentaFacturaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class VentaFacturaService {

    private final VentaFacturaRepository ventaFacturaRepository;
    private final RestTemplate restTemplate;

    public VentaFacturaService(VentaFacturaRepository ventaFacturaRepository, RestTemplate restTemplate) {
        this.ventaFacturaRepository = ventaFacturaRepository;
        this.restTemplate = restTemplate;
    }

    public List<VentaFacturaDTO> listarVentasFacturas() {
        return ventaFacturaRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public Optional<VentaFacturaDTO> buscarVentaFacturaPorId(Long id) {
        return ventaFacturaRepository.findById(id)
                .map(this::convertirADTO);
    }

    public VentaFacturaDTO guardarVentaFactura(VentaFacturaDTO ventaFacturaDTO) {
        Double totalCarrito = obtenerTotalCarrito(ventaFacturaDTO.getIdCliente());
        VentaFactura ventaFactura = convertirAEntidad(ventaFacturaDTO, totalCarrito);
        VentaFactura ventaGuardada = ventaFacturaRepository.save(ventaFactura);
        return convertirADTO(ventaGuardada);
    }

    public VentaFacturaDTO actualizarVentaFactura(VentaFacturaDTO ventaFacturaDTO) {
        return guardarVentaFactura(ventaFacturaDTO);
    }

    public boolean existeVentaFacturaPorId(Long id) {
        return ventaFacturaRepository.existsById(id);
    }

    public void eliminarVentaFactura(Long id) {
        ventaFacturaRepository.deleteById(id);
    }

    public List<VentaFacturaDTO> listarVentasPorCliente(Long idCliente) {
        return ventaFacturaRepository.findByIdCliente(idCliente)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<VentaFacturaDTO> listarVentasPorEstado(String estadoVenta) {
        return ventaFacturaRepository.findByEstadoVenta(estadoVenta)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<VentaFacturaDTO> listarVentasFacturadas() {
        return ventaFacturaRepository.findByFacturada(true)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public VentaFacturaDTO actualizarEstadoVenta(Long id, EstadoVentaDTO estadoVentaDTO) {
        VentaFactura ventaFactura = ventaFacturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("La venta con ID " + id + " no existe"));

        ventaFactura.setEstadoVenta(estadoVentaDTO.getEstadoVenta());
        VentaFactura ventaActualizada = ventaFacturaRepository.save(ventaFactura);
        return convertirADTO(ventaActualizada);
    }

    private VentaFacturaDTO convertirADTO(VentaFactura ventaFactura) {
        return new VentaFacturaDTO(
                ventaFactura.getIdVenta(),
                ventaFactura.getIdCliente(),
                ventaFactura.getFechaVenta(),
                ventaFactura.getTotalVenta(),
                ventaFactura.getMetodoPago(),
                ventaFactura.getEstadoVenta(),
                ventaFactura.getNumeroFactura(),
                ventaFactura.getCorreoCliente(),
                ventaFactura.getObservacion(),
                ventaFactura.getFacturada()
        );
    }

    private VentaFactura convertirAEntidad(VentaFacturaDTO ventaFacturaDTO, Double totalCarrito) {
        return new VentaFactura(
                ventaFacturaDTO.getIdVenta(),
                ventaFacturaDTO.getIdCliente(),
                LocalDateTime.now(),
                totalCarrito,
                ventaFacturaDTO.getMetodoPago(),
                obtenerEstadoVenta(ventaFacturaDTO.getEstadoVenta()),
                generarNumeroFactura(ventaFacturaDTO.getIdCliente()),
                ventaFacturaDTO.getCorreoCliente(),
                ventaFacturaDTO.getObservacion(),
                obtenerFacturada(ventaFacturaDTO.getFacturada())
        );
    }

    private String obtenerEstadoVenta(String estadoVenta) {
        if (estadoVenta == null || estadoVenta.isBlank()) {
            return "PAGADA";
        }
        return estadoVenta;
    }

    private Boolean obtenerFacturada(Boolean facturada) {
        if (facturada == null) {
            return true;
        }
        return facturada;
    }

    private String generarNumeroFactura(Long idCliente) {
        String fecha = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        return "PF-" + idCliente + "-" + fecha;
    }

    private Double obtenerTotalCarrito(Long idCliente) {
        Map<?, ?> respuesta;

        try {
            String url = "http://localhost:8094/api/v1/carrito/cliente/" + idCliente + "/total";
            respuesta = restTemplate.getForObject(url, Map.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RuntimeException("El carrito del cliente con ID " + idCliente + " no existe");
        }

        if (respuesta == null || respuesta.get("total") == null) {
            throw new RuntimeException("No se pudo obtener el total del carrito del cliente con ID " + idCliente);
        }

        Double total = ((Number) respuesta.get("total")).doubleValue();

        if (total <= 0) {
            throw new RuntimeException("El carrito del cliente con ID " + idCliente + " está vacío");
        }

        return total;
    }
}
