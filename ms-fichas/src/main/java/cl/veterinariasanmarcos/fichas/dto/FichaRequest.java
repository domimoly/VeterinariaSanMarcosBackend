package cl.veterinariasanmarcos.fichas.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * Datos de un registro de la ficha. Según el tipo, el servicio exige unos campos u otros:
 * VACUNA y DESPARASITACION piden nombre y proximaDosis; CONSULTA pide motivo, diagnostico y veterinario.
 */
public record FichaRequest(
  @NotNull(message = "La mascota es obligatoria")
  Long mascotaId,

  @NotBlank(message = "El tipo de registro es obligatorio")
  @Pattern(regexp = "VACUNA|DESPARASITACION|CONSULTA",
    message = "El tipo debe ser VACUNA, DESPARASITACION o CONSULTA")
  String tipo,

  @NotNull(message = "La fecha es obligatoria")
  @PastOrPresent(message = "La fecha no puede ser futura")
  LocalDate fecha,

  @Size(max = 80, message = "El nombre no puede superar 80 caracteres")
  String nombre,

  LocalDate proximaDosis,

  @Size(max = 100, message = "El motivo no puede superar 100 caracteres")
  String motivo,

  @Size(max = 300, message = "El diagnóstico no puede superar 300 caracteres")
  String diagnostico,

  @Size(max = 300, message = "Los medicamentos no pueden superar 300 caracteres")
  String medicamentos,

  @Size(max = 80, message = "El veterinario no puede superar 80 caracteres")
  String veterinario,

  LocalDate proximoControl
) {}
