package cl.veterinariasanmarcos.productos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** Descuenta o devuelve stock de varios productos a la vez (todo o nada). Uso interno de ms-pedidos. */
public record MovimientoStockRequest(
  @NotNull(message = "La operación es obligatoria") Operacion operacion,
  @NotEmpty(message = "Debe indicar al menos un producto") List<@Valid Linea> lineas
) {
  public enum Operacion { DESCONTAR, DEVOLVER }

  public record Linea(
    @NotBlank(message = "El código es obligatorio") String codigo,
    @NotNull(message = "La cantidad es obligatoria") @Min(value = 1, message = "La cantidad debe ser al menos 1") Integer cantidad
  ) {}
}
