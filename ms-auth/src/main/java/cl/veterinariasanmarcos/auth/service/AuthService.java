package cl.veterinariasanmarcos.auth.service;

import cl.veterinariasanmarcos.auth.dto.*;
import cl.veterinariasanmarcos.auth.exception.CorreoDuplicadoException;
import cl.veterinariasanmarcos.auth.exception.CuentaDesactivadaException;
import cl.veterinariasanmarcos.auth.exception.ReglaNegocioException;
import cl.veterinariasanmarcos.auth.exception.RunDuplicadoException;
import cl.veterinariasanmarcos.auth.model.Usuario;
import cl.veterinariasanmarcos.auth.repository.UsuarioRepository;
import cl.veterinariasanmarcos.auth.security.JwtService;
import cl.veterinariasanmarcos.auth.util.RunChileno;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import java.time.LocalDateTime;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  public static final String ROL_TUTOR = "TUTOR";
  public static final String ROL_RECEPCIONISTA = "RECEPCIONISTA";
  public static final String ROL_ADMINISTRADOR = "ADMINISTRADOR";

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
    if (!usuario.isActivo()) {
      throw new CuentaDesactivadaException("Tu cuenta está desactivada. Contacta a la clínica.");
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
    usuario.setCreadoEn(LocalDateTime.now());
    return respuesta(usuarios.save(usuario));
  }

  /** Entrega un token nuevo a partir de uno todavía válido. */
  public LoginResponse renovar(String encabezado) {
    Usuario usuario = usuarioDesdeToken(encabezado);
    return respuesta(usuario);
  }

  public PerfilResponse perfil(String encabezado) {
    return aPerfil(usuarioDesdeToken(encabezado));
  }

  /** El tutor completa o corrige sus datos personales. El correo y el rol no cambian. */
  public PerfilResponse actualizarPerfil(String encabezado, PerfilRequest r) {
    Usuario u = usuarioDesdeToken(encabezado);

    String run = null;
    if (r.run() != null && !r.run().isBlank()) {
      run = RunChileno.normalizar(r.run());
      if (run == null) {
        throw new ReglaNegocioException("El RUN no es válido");
      }
      final String runNormalizado = run;
      usuarios.findByRun(runNormalizado)
        .filter(otro -> !otro.getId().equals(u.getId()))
        .ifPresent(otro -> {
          throw new RunDuplicadoException("Ya existe una cuenta con ese RUN");
        });
    }

    u.setNombre(r.nombre().trim());
    u.setApellidos(r.apellidos().trim());
    u.setRun(run);
    u.setFechaNacimiento(r.fechaNacimiento());
    u.setTelefono(r.telefono() == null ? null : r.telefono().trim());
    u.setRegion(r.region().trim());
    u.setComuna(r.comuna().trim());
    u.setDireccion(r.direccion().trim());
    return aPerfil(usuarios.save(u));
  }

  private PerfilResponse aPerfil(Usuario u) {
    String nombre = u.getNombre();
    String apellidos = u.getApellidos();
    // Quien se registró con el nombre completo en un solo campo: la primera palabra es el nombre y el resto los apellidos
    if (apellidos == null || apellidos.isBlank()) {
      String[] partes = nombre.trim().split("\\s+", 2);
      nombre = partes[0];
      apellidos = partes.length > 1 ? partes[1] : "";
    }
    return new PerfilResponse(nombre, apellidos, u.getCorreo(), u.getRol(), u.getRun(),
      u.getFechaNacimiento(), u.getTelefono(), u.getRegion(), u.getComuna(), u.getDireccion());
  }

  private Usuario usuarioDesdeToken(String encabezado) {
    if (encabezado == null || !encabezado.startsWith("Bearer ")) {
      throw new JwtException("Falta el token");
    }
    Claims claims = jwt.validar(encabezado.substring(7).trim());
    Usuario usuario = usuarios.findByCorreoIgnoreCase(claims.getSubject())
      .orElseThrow(() -> new JwtException("Usuario no existe"));
    if (!usuario.isActivo()) {
      throw new JwtException("Cuenta desactivada");
    }
    return usuario;
  }

  private LoginResponse respuesta(Usuario u) {
    return new LoginResponse(jwt.generar(u), u.getNombre(), u.getCorreo(), u.getRol());
  }
}
