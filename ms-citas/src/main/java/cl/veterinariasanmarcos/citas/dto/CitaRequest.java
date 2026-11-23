package cl.veterinariasanmarcos.citas.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

public record CitaRequest(
  @NotNull(message = "Debes elegir una mascota")
  Long mascotaId,

  @NotBlank(message = "Debes elegir un servicio")
  String servicioCodigo,

  @NotNull(message = "La fecha es obligatoria")
  LocalDate fecha,

  @NotNull(message = "La hora es obligatoria")
  LocalTime hora,

  @NotBlank(message = "El teléfono es obligatorio")
  @Pattern(regexp = "\\+?[0-9 ]{8,15}", message = "El teléfono no tiene un formato válido")
  String telefono,

  @NotBlank(message = "El nombre del tutor es obligatorio")
  @Size(max = 100, message = "El nombre del tutor no puede superar 100 caracteres")
  String tutorNombre,

  @Email(message = "El correo de contacto no es válido")
  @Size(max = 100, message = "El correo de contacto no puede superar 100 caracteres")
  String correoContacto,

  @NotBlank(message = "Debes elegir un medio de recordatorio")
  @Pattern(regexp = "Correo|Teléfono", message = "El medio de recordatorio debe ser Correo o Teléfono")
  String medioRecordatorio,

  @Size(max = 300, message = "Las observaciones no pueden superar 300 caracteres")
  String observaciones
) {}
