package cl.veterinariasanmarcos.auth.service;

import cl.veterinariasanmarcos.auth.dto.UsuarioEdicionRequest;
import cl.veterinariasanmarcos.auth.dto.UsuarioRequest;
import cl.veterinariasanmarcos.auth.dto.UsuarioResponse;
import cl.veterinariasanmarcos.auth.exception.CorreoDuplicadoException;
import cl.veterinariasanmarcos.auth.exception.RecursoNoEncontradoException;
import cl.veterinariasanmarcos.auth.exception.ReglaNegocioException;
import cl.veterinariasanmarcos.auth.model.Usuario;
import cl.veterinariasanmarcos.auth.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** Gestión de cuentas: solo la usa el rol ADMINISTRADOR (lo exige la configuración de seguridad). */
@Service
public class UsuarioService {
  private final UsuarioRepository usuarios;
  private final PasswordEncoder encoder;

  public UsuarioService(UsuarioRepository usuarios, PasswordEncoder encoder) {
    this.usuarios = usuarios;
    this.encoder = encoder;
  }

  public List<UsuarioResponse> listar() {
    return usuarios.findAll().stream().map(UsuarioResponse::de).toList();
  }

  public UsuarioResponse buscar(Long id) {
    return UsuarioResponse.de(porId(id));
  }

  public UsuarioResponse crear(UsuarioRequest r) {
    String correo = r.correo().trim();
    if (usuarios.existsByCorreoIgnoreCase(correo)) {
      throw new CorreoDuplicadoException("Ya existe una cuenta con ese correo");
    }
    Usuario u = new Usuario();
    u.setNombre(r.nombre().trim());
    u.setCorreo(correo);
    u.setPassword(encoder.encode(r.password()));
    u.setRol(r.rol());
    u.setActivo(true);
    u.setCreadoEn(LocalDateTime.now());
    return UsuarioResponse.de(usuarios.save(u));
  }

  /** Cambia nombre, rol y (opcionalmente) contraseña. */
  public UsuarioResponse actualizar(Long id, UsuarioEdicionRequest r) {
    Usuario u = porId(id);
    if (esLaPropiaCuenta(u) && !u.getRol().equals(r.rol())) {
      throw new ReglaNegocioException("No puedes cambiar tu propio rol");
    }
    u.setNombre(r.nombre().trim());
    u.setRol(r.rol());
    if (r.password() != null) {
      u.setPassword(encoder.encode(r.password()));
    }
    return UsuarioResponse.de(usuarios.save(u));
  }

  /** Desactiva (activo = false) o reactiva una cuenta. Los datos del usuario se conservan. */
  public UsuarioResponse cambiarEstado(Long id, boolean activo) {
    Usuario u = porId(id);
    if (!activo && esLaPropiaCuenta(u)) {
      throw new ReglaNegocioException("No puedes desactivar tu propia cuenta");
    }
    u.setActivo(activo);
    return UsuarioResponse.de(usuarios.save(u));
  }

  private Usuario porId(Long id) {
    return usuarios.findById(id).orElseThrow(() ->
      new RecursoNoEncontradoException("Usuario no encontrado: " + id));
  }

  private boolean esLaPropiaCuenta(Usuario u) {
    Authentication a = SecurityContextHolder.getContext().getAuthentication();
    return a != null && u.getCorreo().equalsIgnoreCase(String.valueOf(a.getPrincipal()));
  }
}
