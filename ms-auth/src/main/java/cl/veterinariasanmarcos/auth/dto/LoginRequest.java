package cl.veterinariasanmarcos.auth.dto;

import jakarta.validation.constraints.*;

public record LoginRequest(
  @NotBlank(message = "El correo es obligatorio")
  @Email(message = "El correo no tiene un formato válido")
  String correo,

  @NotBlank(message = "La contraseña es obligatoria")
  String password
) {}
