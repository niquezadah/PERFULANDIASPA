package cl.perfulandia.usuarios.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cl.perfulandia.usuarios.dto.UsuarioAuthResponse;
import cl.perfulandia.usuarios.dto.UsuarioResponse;
import cl.perfulandia.usuarios.model.Rol;
import cl.perfulandia.usuarios.model.Usuario;
import cl.perfulandia.usuarios.repository.RolRepository;
import cl.perfulandia.usuarios.repository.UsuarioRepository;
import cl.perfulandia.usuarios.security.PasswordUtil;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordUtil passwordUtil;

    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository, PasswordUtil passwordUtil) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordUtil = passwordUtil;
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarUsuariosPorEstado(Boolean estado) {
        return usuarioRepository.findByEstado(estado);
    }

    public Optional<Usuario> buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario guardarUsuario(Usuario usuario) {
        String correo = limpiarCorreo(usuario.getCorreo());

        if (usuarioRepository.existsByCorreo(correo)) {
            throw new RuntimeException("Ya existe un usuario registrado con el correo: " + correo);
        }

        Rol rol = obtenerRolValido(usuario);

        usuario.setNombre(limpiarTexto(usuario.getNombre()));
        usuario.setApellido(limpiarTexto(usuario.getApellido()));
        usuario.setCorreo(correo);
        usuario.setDireccionEnvio(limpiarTextoOpcional(usuario.getDireccionEnvio()));
        usuario.setPasswordHash(passwordUtil.generarHash(usuario.getPasswordHash()));
        usuario.setEstado(usuario.getEstado() != null ? usuario.getEstado() : true);
        usuario.setRol(rol);

        return usuarioRepository.save(usuario);
    }

    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        String nuevoCorreo = limpiarCorreo(usuarioActualizado.getCorreo());

        if (!usuarioExistente.getCorreo().equalsIgnoreCase(nuevoCorreo)
                && usuarioRepository.existsByCorreo(nuevoCorreo)) {
            throw new RuntimeException("Ya existe un usuario registrado con el correo: " + nuevoCorreo);
        }

        Rol rol = obtenerRolValido(usuarioActualizado);

        usuarioExistente.setNombre(limpiarTexto(usuarioActualizado.getNombre()));
        usuarioExistente.setApellido(limpiarTexto(usuarioActualizado.getApellido()));
        usuarioExistente.setCorreo(nuevoCorreo);
        usuarioExistente.setDireccionEnvio(limpiarTextoOpcional(usuarioActualizado.getDireccionEnvio()));
        usuarioExistente.setEstado(usuarioActualizado.getEstado() != null ? usuarioActualizado.getEstado() : true);
        usuarioExistente.setRol(rol);

        if (usuarioActualizado.getPasswordHash() != null && !usuarioActualizado.getPasswordHash().isBlank()) {
            usuarioExistente.setPasswordHash(passwordUtil.generarHash(usuarioActualizado.getPasswordHash()));
        }

        return usuarioRepository.save(usuarioExistente);
    }

    public Usuario cambiarEstadoUsuario(Long idUsuario, Boolean nuevoEstado) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));

        if (nuevoEstado == null) {
            throw new RuntimeException("El estado del usuario es obligatorio");
        }

        usuario.setEstado(nuevoEstado);

        return usuarioRepository.save(usuario);
    }

    public Usuario cambiarRolUsuario(Long idUsuario, Long idRol) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));

        Rol rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + idRol));

        usuario.setRol(rol);

        return usuarioRepository.save(usuario);
    }

    public Usuario cambiarPassword(Long idUsuario, String passwordActual, String passwordNueva) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));

        if (!Boolean.TRUE.equals(usuario.getEstado())) {
            throw new RuntimeException("No se puede cambiar la contraseña de un usuario inactivo");
        }

        boolean passwordCorrecta = passwordUtil.verificarPassword(passwordActual, usuario.getPasswordHash());

        if (!passwordCorrecta) {
            throw new RuntimeException("La contraseña actual no es correcta");
        }

        usuario.setPasswordHash(passwordUtil.generarHash(passwordNueva));

        return usuarioRepository.save(usuario);
    }

    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        usuario.setEstado(false);

        usuarioRepository.save(usuario);
    }

    public Optional<Usuario> validarLogin(String correo, String password) {
        if (correo == null || correo.isBlank() || password == null || password.isBlank()) {
            return Optional.empty();
        }

        String correoLimpio = limpiarCorreo(correo);

        return usuarioRepository.findByCorreo(correoLimpio)
                .filter(usuario -> passwordUtil.verificarPassword(password, usuario.getPasswordHash()))
                .filter(usuario -> Boolean.TRUE.equals(usuario.getEstado()));
    }

    public Optional<UsuarioAuthResponse> validarCredencialesParaAuth(String correo, String password) {
        return validarLogin(correo, password)
                .map(usuario -> {
                    String nombreRol = usuario.getRol() != null
                            ? usuario.getRol().getNombreRol()
                            : "SIN_ROL";

                    List<String> permisos = usuario.getRol() != null && usuario.getRol().getPermisos() != null
                            ? usuario.getRol().getPermisos()
                                    .stream()
                                    .sorted(Comparator.comparing(permiso -> permiso.getNombrePermiso()))
                                    .map(permiso -> permiso.getNombrePermiso())
                                    .toList()
                            : List.of();

                    return new UsuarioAuthResponse(
                            usuario.getIdUsuario(),
                            usuario.getCorreo(),
                            nombreRol,
                            permisos
                    );
                });
    }

    public UsuarioResponse convertirAUsuarioResponse(Usuario usuario) {
        Long idRol = null;
        String nombreRol = null;

        if (usuario.getRol() != null) {
            idRol = usuario.getRol().getIdRol();
            nombreRol = usuario.getRol().getNombreRol();
        }

        return new UsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getCorreo(),
                usuario.getDireccionEnvio(),
                usuario.getEstado(),
                idRol,
                nombreRol
        );
    }

    private Rol obtenerRolValido(Usuario usuario) {
        if (usuario.getRol() == null || usuario.getRol().getIdRol() == null) {
            throw new RuntimeException("El rol del usuario es obligatorio");
        }

        Long idRol = usuario.getRol().getIdRol();

        return rolRepository.findById(idRol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + idRol));
    }

    private String limpiarCorreo(String correo) {
        if (correo == null) {
            return null;
        }

        return correo.trim().toLowerCase();
    }

    private String limpiarTexto(String texto) {
        if (texto == null) {
            return null;
        }

        return texto.trim();
    }

    private String limpiarTextoOpcional(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }

        return texto.trim();
    }
}