package cl.veterinariasanmarcos.pedidos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/** Datos de la compra. El precio no se envía: lo toma el servidor del catálogo. */
public record PedidoRequest(
  @NotBlank(message = "El nombre es obligatorio")
  @Size(min = 2, max = 60, message = "El nombre debe tener entre 2 y 60 caracteres")
  String nombre,

  @NotBlank(message = "Los apellidos son obligatorios")
  @Size(min = 2, max = 80, message = "Los apellidos deben tener entre 2 y 80 caracteres")
  String apellidos,

  @NotBlank(message = "El correo es obligatorio")
  @Email(message = "El correo no es válido")
  @Size(max = 100, message = "El correo no puede superar 100 caracteres")
  String correo,

  @NotBlank(message = "La calle es obligatoria")
  @Size(max = 120, message = "La calle no puede superar 120 caracteres")
  String calle,

  @Size(max = 30, message = "El departamento no puede superar 30 caracteres")
  String departamento,

  @NotBlank(message = "La región es obligatoria")
  @Size(max = 60, message = "La región no puede superar 60 caracteres")
  String region,

  @NotBlank(message = "La comuna es obligatoria")
  @Size(max = 60, message = "La comuna no puede superar 60 caracteres")
  String comuna,

  @Size(max = 300, message = "Las indicaciones no pueden superar 300 caracteres")
  String indicaciones,

  @NotEmpty(message = "El pedido debe tener al menos un producto")
  @Size(max = 50, message = "El pedido tiene demasiados productos")
  List<@Valid Linea> lineas
) {
  public record Linea(
    @NotBlank(message = "El código del producto es obligatorio") String codigo,
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Max(value = 99, message = "La cantidad máxima por producto es 99")
    Integer cantidad
  ) {}
}
