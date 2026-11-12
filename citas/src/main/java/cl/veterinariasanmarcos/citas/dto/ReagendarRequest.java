package cl.veterinariasanmarcos.citas.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReagendarRequest(
  @NotNull(message = "La fecha es obligatoria")
  LocalDate fecha,

  @NotNull(message = "La hora es obligatoria")
  LocalTime hora,

  @NotBlank(message = "Debes explicar el motivo del cambio")
  @Size(max = 300, message = "La nota no puede superar 300 caracteres")
  String nota
) {}
