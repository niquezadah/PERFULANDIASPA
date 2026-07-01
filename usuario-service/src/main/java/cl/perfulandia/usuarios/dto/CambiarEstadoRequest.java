package cl.perfulandia.usuarios.dto;

import jakarta.validation.constraints.NotNull;

public class CambiarEstadoRequest {

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;

    public CambiarEstadoRequest() {
    }

    public CambiarEstadoRequest(Boolean estado) {
        this.estado = estado;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}