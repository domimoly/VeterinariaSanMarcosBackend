package cl.veterinariasanmarcos.mascotas.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record MascotaRequest(
  @NotBlank(message = "El nombre es obligatorio")
  @Size(min = 2, max = 40, message = "El nombre debe tener entre 2 y 40 caracteres")
  String nombre,

  @NotBlank(message = "La especie es obligatoria")
  @Pattern(regexp = "Perro|Gato|Ave|Conejo", message = "La especie debe ser Perro, Gato, Ave o Conejo")
  String especie,

  @Size(max = 40, message = "La raza no puede superar 40 caracteres")
  String raza,

  @NotBlank(message = "El sexo es obligatorio")
  @Pattern(regexp = "Macho|Hembra", message = "El sexo debe ser Macho o Hembra")
  String sexo,

  @NotNull(message = "El peso es obligatorio")
  @Positive(message = "El peso debe ser mayor que 0")
  @DecimalMax(value = "100", message = "El peso no puede superar 100 kg")
  Double pesoKg,

  @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
  LocalDate fechaNacimiento,

  boolean esterilizada
) {}
