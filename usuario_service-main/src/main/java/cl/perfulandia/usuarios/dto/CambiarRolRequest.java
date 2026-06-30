package cl.perfulandia.usuarios.dto;

import jakarta.validation.constraints.NotNull;

public class CambiarRolRequest {

    @NotNull(message = "El id del rol es obligatorio")
    private Long idRol;

    public CambiarRolRequest() {
    }

    public CambiarRolRequest(Long idRol) {
        this.idRol = idRol;
    }

    public Long getIdRol() {
        return idRol;
    }

    public void setIdRol(Long idRol) {
        this.idRol = idRol;
    }
}