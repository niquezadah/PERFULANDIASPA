package com.perfulandia.autenticacion.client;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.perfulandia.autenticacion.dto.LoginRequest;
import com.perfulandia.autenticacion.dto.UsuarioAuthResponse;

import io.netty.channel.ChannelOption;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Component
public class UsuarioClient {

    private static final String RUTA_VALIDAR_CREDENCIALES =
            "/api/auth/validar-credenciales";

    private final WebClient webClient;
    private final Duration requestTimeout;

    @Autowired
    public UsuarioClient(
            @Value("${app.usuario-service.url}") String usuarioServiceUrl,
            @Value("${app.usuario-service.connect-timeout-ms:3000}")
            int connectTimeoutMs,
            @Value("${app.usuario-service.response-timeout-seconds:5}")
            long responseTimeoutSeconds) {

        this(
                crearWebClient(
                        usuarioServiceUrl,
                        connectTimeoutMs,
                        responseTimeoutSeconds
                ),
                Duration.ofSeconds(responseTimeoutSeconds)
        );
    }

    UsuarioClient(WebClient webClient, Duration requestTimeout) {
        this.webClient = webClient;
        this.requestTimeout = requestTimeout;
    }

    public UsuarioAuthResponse validarCredenciales(LoginRequest request) {
        try {
            return webClient
                    .post()
                    .uri(RUTA_VALIDAR_CREDENCIALES)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(UsuarioAuthResponse.class)
                    .switchIfEmpty(Mono.error(
                            new RuntimeException("Credenciales inválidas")
                    ))
                    .timeout(requestTimeout)
                    .onErrorMap(
                            TimeoutException.class,
                            ex -> new RuntimeException(
                                    "No se pudo conectar con usuario_service"
                            )
                    )
                    .block();

        } catch (WebClientResponseException.Unauthorized ex) {
            throw new RuntimeException("Credenciales inválidas");

        } catch (WebClientRequestException ex) {
            throw new RuntimeException(
                    "No se pudo conectar con usuario_service"
            );

        } catch (WebClientResponseException ex) {
            throw new RuntimeException(
                    "Error al consultar usuario_service"
            );
        }
    }

    private static WebClient crearWebClient(
            String usuarioServiceUrl,
            int connectTimeoutMs,
            long responseTimeoutSeconds) {

        HttpClient httpClient = HttpClient
                .create()
                .option(
                        ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        connectTimeoutMs
                )
                .responseTimeout(
                        Duration.ofSeconds(responseTimeoutSeconds)
                );

        return WebClient
                .builder()
                .baseUrl(usuarioServiceUrl)
                .clientConnector(
                        new ReactorClientHttpConnector(httpClient)
                )
                .build();
    }
}