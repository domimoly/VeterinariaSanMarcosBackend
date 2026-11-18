package cl.veterinariasanmarcos.auth.dto;

import jakarta.validation.constraints.*;

/**
 * Datos que el administrador puede cambiar. El correo no se edita: es la identidad de la cuenta
 * y los demás servicios (mascotas, citas) guardan a sus dueños por correo.
 * Si "password" viene vacío (null), la contraseña no cambia.
 */
public record UsuarioEdicionRequest(
  @NotBlank(message = "El nombre es obligatorio")
  @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres")
  String nombre,

  @NotBlank(message = "El rol es obligatorio")
  @Pattern(regexp = "TUTOR|RECEPCIONISTA|ADMINISTRADOR",
    message = "El rol debe ser TUTOR, RECEPCIONISTA o ADMINISTRADOR")
  String rol,

  @Size(min = 6, max = 64, message = "La contraseña debe tener entre 6 y 64 caracteres")
  String password
) {}
