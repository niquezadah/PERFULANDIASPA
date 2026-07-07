package com.perfulandia.logistica.client;

import com.perfulandia.logistica.exception.ReglaNegocioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
public class UsuarioClient {

    private final RestTemplate restTemplate;

    @Value("${usuario.service.url}")
    private String usuarioServiceUrl;

    public UsuarioClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void validarUsuarioExiste(Long idUsuario) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    usuarioServiceUrl + "/" + idUsuario,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ReglaNegocioException("No se pudo validar el usuario con id " + idUsuario);
            }

        } catch (HttpClientErrorException.NotFound ex) {
            throw new ReglaNegocioException("No existe un usuario con id " + idUsuario);

        } catch (HttpClientErrorException ex) {
            throw new ReglaNegocioException("Error al validar el usuario con id " + idUsuario);

        } catch (ResourceAccessException ex) {
            throw new ReglaNegocioException("No se pudo conectar con usuario-service");
        }
    }
}