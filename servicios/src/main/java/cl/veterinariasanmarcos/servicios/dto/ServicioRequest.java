package cl.veterinariasanmarcos.servicios.dto;

import jakarta.validation.constraints.*;

public record ServicioRequest(
  @NotBlank(message = "El código es obligatorio")
  @Pattern(regexp = "[a-z0-9-]+", message = "Usa minúsculas, números y guiones")
  String codigo,

  @NotBlank(message = "El nombre es obligatorio")
  @Size(max = 80, message = "El nombre no puede superar 80 caracteres")
  String nombre,

  @NotNull(message = "La duración es obligatoria")
  @Min(value = 30, message = "La duración mínima es 30 minutos")
  @Max(value = 240, message = "La duración máxima es 240 minutos")
  Integer duracionMin,

  @NotNull(message = "El precio es obligatorio")
  @Min(value = 0, message = "El precio no puede ser negativo")
  Integer precio,

  boolean agendableOnline
) {}
