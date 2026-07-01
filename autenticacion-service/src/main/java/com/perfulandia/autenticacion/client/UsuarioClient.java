package com.perfulandia.autenticacion.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.perfulandia.autenticacion.dto.LoginRequest;
import com.perfulandia.autenticacion.dto.UsuarioAuthResponse;

@Component
public class UsuarioClient {

    private final RestTemplate restTemplate;
    private final String usuarioServiceUrl;

    @Autowired
    public UsuarioClient(@Value("${app.usuario-service.url}") String usuarioServiceUrl) {
        this(new RestTemplate(), usuarioServiceUrl);
    }

    UsuarioClient(RestTemplate restTemplate, String usuarioServiceUrl) {
        this.restTemplate = restTemplate;
        this.usuarioServiceUrl = usuarioServiceUrl;
    }

    public UsuarioAuthResponse validarCredenciales(LoginRequest request) {
        String url = usuarioServiceUrl + "/api/auth/validar-credenciales";

        try {
            ResponseEntity<UsuarioAuthResponse> response = restTemplate.postForEntity(
                    url,
                    request,
                    UsuarioAuthResponse.class
            );

            UsuarioAuthResponse body = response.getBody();

            if (body == null) {
                throw new RuntimeException("Credenciales inválidas");
            }

            return body;

        } catch (HttpClientErrorException.Unauthorized ex) {
            throw new RuntimeException("Credenciales inválidas");

        } catch (ResourceAccessException ex) {
            throw new RuntimeException("No se pudo conectar con usuario_service");

        } catch (RestClientException ex) {
            throw new RuntimeException("Error al consultar usuario_service");
        }
    }
}