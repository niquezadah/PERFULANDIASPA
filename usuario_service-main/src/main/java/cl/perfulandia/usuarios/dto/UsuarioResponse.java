package cl.perfulandia.usuarios.dto;

public class UsuarioResponse {

    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String correo;
    private String direccionEnvio;
    private Boolean estado;
    private Long idRol;
    private String nombreRol;

    public UsuarioResponse() {
    }

    public UsuarioResponse(Long idUsuario, String nombre, String apellido, String correo,
                           String direccionEnvio, Boolean estado, Long idRol, String nombreRol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.direccionEnvio = direccionEnvio;
        this.estado = estado;
        this.idRol = idRol;
        this.nombreRol = nombreRol;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Long getIdRol() {
        return idRol;
    }

    public void setIdRol(Long idRol) {
        this.idRol = idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }
}