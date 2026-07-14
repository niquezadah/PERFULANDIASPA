package com.perfulandia.autenticacion.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.perfulandia.autenticacion.dto.LoginRequest;
import com.perfulandia.autenticacion.dto.UsuarioAuthResponse;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class UsuarioClientTest {

    private static final String RUTA_VALIDAR_CREDENCIALES =
            "/api/auth/validar-credenciales";

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private UsuarioClient usuarioClient;

    @BeforeEach
    void setUp() {
        usuarioClient = new UsuarioClient(
                webClient,
                Duration.ofMillis(100)
        );

        when(webClient.post())
                .thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri(RUTA_VALIDAR_CREDENCIALES))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON))
                .thenReturn(requestBodySpec);

        /*
         * Se utiliza doReturn para evitar el problema de captura
         * del comodín genérico RequestHeadersSpec<?>.
         */
        doReturn(requestHeadersSpec)
                .when(requestBodySpec)
                .bodyValue(any(LoginRequest.class));

        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
    }

    @Test
    void validarCredenciales_deberiaRetornarUsuarioCuandoServicioRespondeOk() {
        LoginRequest request = new LoginRequest(
                "admin@perfulandia.cl",
                "admin123"
        );

        UsuarioAuthResponse response = new UsuarioAuthResponse(
                1L,
                "admin@perfulandia.cl",
                "ADMINISTRADOR",
                List.of("GESTIONAR_USUARIOS")
        );

        when(responseSpec.bodyToMono(UsuarioAuthResponse.class))
                .thenReturn(Mono.just(response));

        UsuarioAuthResponse resultado =
                usuarioClient.validarCredenciales(request);

        assertEquals(1L, resultado.getIdUsuario());
        assertEquals(
                "admin@perfulandia.cl",
                resultado.getCorreo()
        );
        assertEquals(
                "ADMINISTRADOR",
                resultado.getRol()
        );
        assertEquals(
                List.of("GESTIONAR_USUARIOS"),
                resultado.getPermisos()
        );

        verify(webClient).post();
        verify(requestBodyUriSpec)
                .uri(RUTA_VALIDAR_CREDENCIALES);
        verify(requestBodySpec)
                .contentType(MediaType.APPLICATION_JSON);
        verify(requestBodySpec)
                .bodyValue(request);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec)
                .bodyToMono(UsuarioAuthResponse.class);
    }

    @Test
    void validarCredenciales_deberiaLanzarErrorCuandoRespuestaVieneVacia() {
        LoginRequest request = new LoginRequest(
                "admin@perfulandia.cl",
                "admin123"
        );

        when(responseSpec.bodyToMono(UsuarioAuthResponse.class))
                .thenReturn(Mono.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> usuarioClient.validarCredenciales(request)
        );

        assertEquals(
                "Credenciales inválidas",
                exception.getMessage()
        );
    }

    @Test
    void validarCredenciales_deberiaLanzarErrorCuandoCredencialesSonInvalidas() {
        LoginRequest request = new LoginRequest(
                "admin@perfulandia.cl",
                "password-incorrecta"
        );

        WebClientResponseException unauthorized =
                WebClientResponseException.create(
                        HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        new HttpHeaders(),
                        new byte[0],
                        StandardCharsets.UTF_8
                );

        when(responseSpec.bodyToMono(UsuarioAuthResponse.class))
                .thenReturn(Mono.error(unauthorized));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> usuarioClient.validarCredenciales(request)
        );

        assertEquals(
                "Credenciales inválidas",
                exception.getMessage()
        );
    }

    @Test
    void validarCredenciales_deberiaLanzarErrorCuandoNoPuedeConectar() {
        LoginRequest request = new LoginRequest(
                "admin@perfulandia.cl",
                "admin123"
        );

        WebClientRequestException errorConexion =
                new WebClientRequestException(
                        new IOException("Sin conexión"),
                        HttpMethod.POST,
                        URI.create(
                                "http://localhost:8082"
                                        + RUTA_VALIDAR_CREDENCIALES
                        ),
                        new HttpHeaders()
                );

        when(responseSpec.bodyToMono(UsuarioAuthResponse.class))
                .thenReturn(Mono.error(errorConexion));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> usuarioClient.validarCredenciales(request)
        );

        assertEquals(
                "No se pudo conectar con usuario_service",
                exception.getMessage()
        );
    }

    @Test
    void validarCredenciales_deberiaLanzarErrorCuandoServicioRespondeError() {
        LoginRequest request = new LoginRequest(
                "admin@perfulandia.cl",
                "admin123"
        );

        WebClientResponseException errorServidor =
                WebClientResponseException.create(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        new HttpHeaders(),
                        new byte[0],
                        StandardCharsets.UTF_8
                );

        when(responseSpec.bodyToMono(UsuarioAuthResponse.class))
                .thenReturn(Mono.error(errorServidor));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> usuarioClient.validarCredenciales(request)
        );

        assertEquals(
                "Error al consultar usuario_service",
                exception.getMessage()
        );
    }

    @Test
    void validarCredenciales_deberiaLanzarErrorCuandoSuperaTiempoEspera() {
        LoginRequest request = new LoginRequest(
                "admin@perfulandia.cl",
                "admin123"
        );

        when(responseSpec.bodyToMono(UsuarioAuthResponse.class))
                .thenReturn(Mono.never());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> usuarioClient.validarCredenciales(request)
        );

        assertEquals(
                "No se pudo conectar con usuario_service",
                exception.getMessage()
        );
    }
}