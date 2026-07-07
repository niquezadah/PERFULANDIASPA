package com.perfulandia.logistica.client;

import com.perfulandia.logistica.exception.ReglaNegocioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
public class PedidoClient {

    private final RestTemplate restTemplate;

    @Value("${pedido.service.url}")
    private String pedidoServiceUrl;

    public PedidoClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void validarPedidoExiste(Long idPedido) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    pedidoServiceUrl + "/" + idPedido,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ReglaNegocioException("No se pudo validar el pedido con id " + idPedido);
            }

        } catch (HttpClientErrorException.NotFound ex) {
            throw new ReglaNegocioException("No existe un pedido con id " + idPedido);

        } catch (HttpClientErrorException ex) {
            throw new ReglaNegocioException("Error al validar el pedido con id " + idPedido);

        } catch (ResourceAccessException ex) {
            throw new ReglaNegocioException("No se pudo conectar con pedido-service");
        }
    }
}