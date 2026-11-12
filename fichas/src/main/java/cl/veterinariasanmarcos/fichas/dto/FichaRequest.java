package cl.veterinariasanmarcos.fichas.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record FichaRequest(
  @NotNull(message = "La mascota es obligatoria")
  Long mascotaId,

  @NotNull(message = "La fecha es obligatoria")
  @PastOrPresent(message = "La fecha no puede ser futura")
  LocalDate fecha,

  @NotBlank(message = "El motivo es obligatorio")
  @Size(max = 255, message = "El motivo no puede superar 255 caracteres")
  String motivo,

  @Size(max = 500, message = "El diagnóstico no puede superar 500 caracteres")
  String diagnostico,

  @Size(max = 500, message = "El tratamiento no puede superar 500 caracteres")
  String tratamiento,

  LocalDate proximaVacuna
) {}
