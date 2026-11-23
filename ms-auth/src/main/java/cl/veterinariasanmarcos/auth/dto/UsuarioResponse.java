package cl.veterinariasanmarcos.auth.dto;

import cl.veterinariasanmarcos.auth.model.Usuario;
import java.time.LocalDateTime;

/** Cuenta tal como la ve el administrador (nunca incluye la contraseña). */
public record UsuarioResponse(
  Long id,
  String nombre,
  String correo,
  String rol,
  boolean activo,
  LocalDateTime creadoEn
) {
  public static UsuarioResponse de(Usuario u) {
    return new UsuarioResponse(u.getId(), u.getNombre(), u.getCorreo(), u.getRol(), u.isActivo(), u.getCreadoEn());
  }
}
