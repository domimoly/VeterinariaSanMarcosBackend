package cl.veterinariasanmarcos.auth.dto;

import jakarta.validation.constraints.*;

public record RegistroRequest(
  @NotBlank(message = "El nombre es obligatorio")
  @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres")
  String nombre,

  @NotBlank(message = "El correo es obligatorio")
  @Email(message = "El correo no tiene un formato válido")
  String correo,

  @NotBlank(message = "La contraseña es obligatoria")
  @Size(min = 6, max = 64, message = "La contraseña debe tener entre 6 y 64 caracteres")
  String password
) {}
