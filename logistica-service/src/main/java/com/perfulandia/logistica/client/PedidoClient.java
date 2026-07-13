package com.perfulandia.logistica.client;

import com.perfulandia.logistica.client.dto.PedidoResponse;
import com.perfulandia.logistica.exception.ReglaNegocioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class PedidoClient {

    private final WebClient webClient;

    public PedidoClient(
            WebClient.Builder webClientBuilder,
            @Value("${pedido.service.url}") String pedidoServiceUrl
    ) {
        this.webClient = webClientBuilder
                .baseUrl(pedidoServiceUrl)
                .build();
    }

    public PedidoResponse obtenerPedido(Long idPedido) {
        try {
            log.info("Validando existencia del pedido {}", idPedido);

            PedidoResponse pedido = webClient.get()
                    .uri("/{idPedido}", idPedido)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            Mono.error(new ReglaNegocioException("No existe un pedido con id " + idPedido))
                    )
                    .onStatus(HttpStatusCode::is5xxServerError, response ->
                            Mono.error(new ReglaNegocioException("pedido-service no pudo validar el pedido " + idPedido))
                    )
                    .bodyToMono(PedidoResponse.class)
                    .block();

            if (pedido == null) {
                throw new ReglaNegocioException("pedido-service no devolvió información del pedido " + idPedido);
            }

            return pedido;

        } catch (ReglaNegocioException ex) {
            log.warn("Validación de pedido fallida: {}", ex.getMessage());
            throw ex;

        } catch (WebClientRequestException ex) {
            log.error("No se pudo conectar con pedido-service", ex);
            throw new ReglaNegocioException("No se pudo conectar con pedido-service");

        } catch (Exception ex) {
            log.error("Error inesperado al validar pedido {}", idPedido, ex);
            throw new ReglaNegocioException("Error al validar el pedido con id " + idPedido);
        }
    }

    public void validarPedidoExiste(Long idPedido) {
        obtenerPedido(idPedido);
    }
}