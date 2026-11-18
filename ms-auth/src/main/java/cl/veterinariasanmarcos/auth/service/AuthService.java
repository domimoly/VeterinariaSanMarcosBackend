package cl.veterinariasanmarcos.auth.service;

import cl.veterinariasanmarcos.auth.dto.*;
import cl.veterinariasanmarcos.auth.exception.CorreoDuplicadoException;
import cl.veterinariasanmarcos.auth.model.Usuario;
import cl.veterinariasanmarcos.auth.repository.UsuarioRepository;
import cl.veterinariasanmarcos.auth.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  public static final String ROL_TUTOR = "TUTOR";
  public static final String ROL_RECEPCIONISTA = "RECEPCIONISTA";

  private final UsuarioRepository usuarios;
  private final PasswordEncoder encoder;
  private final JwtService jwt;

  public AuthService(UsuarioRepository usuarios, PasswordEncoder encoder, JwtService jwt) {
    this.usuarios = usuarios;
    this.encoder = encoder;
    this.jwt = jwt;
  }

  public LoginResponse login(LoginRequest request) {
    Usuario usuario = usuarios.findByCorreoIgnoreCase(request.correo())
      .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));
    if (!encoder.matches(request.password(), usuario.getPassword())) {
      throw new BadCredentialsException("Credenciales inválidas");
    }
    return respuesta(usuario);
  }

  /** Registro público: siempre crea un TUTOR (la recepción no se puede auto-registrar). */
  public LoginResponse registrarTutor(RegistroRequest request) {
    String correo = request.correo().trim();
    if (usuarios.existsByCorreoIgnoreCase(correo)) {
      throw new CorreoDuplicadoException("Ya existe una cuenta con ese correo");
    }
    Usuario usuario = new Usuario();
    usuario.setNombre(request.nombre().trim());
    usuario.setCorreo(correo);
    usuario.setPassword(encoder.encode(request.password()));
    usuario.setRol(ROL_TUTOR);
    return respuesta(usuarios.save(usuario));
  }

  /** Entrega un token nuevo a partir de uno todavía válido. */
  public LoginResponse renovar(String encabezado) {
    Usuario usuario = usuarioDesdeToken(encabezado);
    return respuesta(usuario);
  }

  public PerfilResponse perfil(String encabezado) {
    Usuario u = usuarioDesdeToken(encabezado);
    return new PerfilResponse(u.getNombre(), u.getCorreo(), u.getRol());
  }

  private Usuario usuarioDesdeToken(String encabezado) {
    if (encabezado == null || !encabezado.startsWith("Bearer ")) {
      throw new JwtException("Falta el token");
    }
    Claims claims = jwt.validar(encabezado.substring(7).trim());
    return usuarios.findByCorreoIgnoreCase(claims.getSubject())
      .orElseThrow(() -> new JwtException("Usuario no existe"));
  }

  private LoginResponse respuesta(Usuario u) {
    return new LoginResponse(jwt.generar(u), u.getNombre(), u.getCorreo(), u.getRol());
  }
}
