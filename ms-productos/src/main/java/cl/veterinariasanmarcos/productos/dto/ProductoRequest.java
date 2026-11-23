package cl.veterinariasanmarcos.productos.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProductoRequest(
  @NotBlank(message = "El código es obligatorio")
  @Size(max = 20, message = "El código no puede superar 20 caracteres")
  @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "El código solo admite letras, números y guiones")
  String codigo,

  @NotBlank(message = "El nombre es obligatorio")
  @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
  String nombre,

  @NotBlank(message = "La categoría es obligatoria")
  @Size(max = 40, message = "La categoría no puede superar 40 caracteres")
  String categoria,

  @Size(max = 300, message = "La descripción no puede superar 300 caracteres")
  String descripcion,

  @NotNull(message = "El precio es obligatorio")
  @Min(value = 1, message = "El precio debe ser mayor a 0")
  @Max(value = 10_000_000, message = "El precio es demasiado alto")
  Integer precio,

  @NotNull(message = "El stock es obligatorio")
  @Min(value = 0, message = "El stock no puede ser negativo")
  @Max(value = 100_000, message = "El stock es demasiado alto")
  Integer stock,

  @Size(max = 200, message = "La ruta de la imagen no puede superar 200 caracteres")
  String imagen,

  /** Si no se envía, el producto queda activo. */
  Boolean activo
) {}
