package cl.veterinariasanmarcos.auth.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/** Datos personales que el tutor completa en "Mi perfil" (el correo y el rol no se cambian aquí). */
public record PerfilRequest(
  @NotBlank(message = "El nombre es obligatorio")
  @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
  String nombre,

  @NotBlank(message = "Los apellidos son obligatorios")
  @Size(min = 2, max = 100, message = "Los apellidos deben tener entre 2 y 100 caracteres")
  String apellidos,

  /** Opcional; si viene, se valida el dígito verificador. */
  @Size(max = 12, message = "El RUN es demasiado largo")
  String run,

  @NotNull(message = "La fecha de nacimiento es obligatoria")
  @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
  LocalDate fechaNacimiento,

  @Pattern(regexp = "^$|^\\+?\\d{8,12}$", message = "El teléfono debe tener entre 8 y 12 dígitos")
  String telefono,

  @NotBlank(message = "La región es obligatoria")
  @Size(max = 40, message = "La región es demasiado larga")
  String region,

  @NotBlank(message = "La comuna es obligatoria")
  @Size(max = 80, message = "La comuna es demasiado larga")
  String comuna,

  @NotBlank(message = "La dirección es obligatoria")
  @Size(max = 300, message = "La dirección debe tener máximo 300 caracteres")
  String direccion
) {}
