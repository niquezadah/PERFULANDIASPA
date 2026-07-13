package com.perfulandia.logistica.client;

import com.perfulandia.logistica.client.dto.UsuarioResponse;
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
public class UsuarioClient {

    private final WebClient webClient;

    public UsuarioClient(
            WebClient.Builder webClientBuilder,
            @Value("${usuario.service.url}") String usuarioServiceUrl
    ) {
        this.webClient = webClientBuilder
                .baseUrl(usuarioServiceUrl)
                .build();
    }

    public UsuarioResponse obtenerUsuario(Long idUsuario) {
        try {
            log.info("Validando existencia del usuario {}", idUsuario);

            UsuarioResponse usuario = webClient.get()
                    .uri("/{idUsuario}", idUsuario)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            Mono.error(new ReglaNegocioException("No existe un usuario con id " + idUsuario))
                    )
                    .onStatus(HttpStatusCode::is5xxServerError, response ->
                            Mono.error(new ReglaNegocioException("usuario-service no pudo validar el usuario " + idUsuario))
                    )
                    .bodyToMono(UsuarioResponse.class)
                    .block();

            if (usuario == null) {
                throw new ReglaNegocioException("usuario-service no devolvió información del usuario " + idUsuario);
            }

            return usuario;

        } catch (ReglaNegocioException ex) {
            log.warn("Validación de usuario fallida: {}", ex.getMessage());
            throw ex;

        } catch (WebClientRequestException ex) {
            log.error("No se pudo conectar con usuario-service", ex);
            throw new ReglaNegocioException("No se pudo conectar con usuario-service");

        } catch (Exception ex) {
            log.error("Error inesperado al validar usuario {}", idUsuario, ex);
            throw new ReglaNegocioException("Error al validar el usuario con id " + idUsuario);
        }
    }

    public void validarUsuarioExiste(Long idUsuario) {
        obtenerUsuario(idUsuario);
    }
}