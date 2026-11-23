package cl.veterinariasanmarcos.pedidos.dto;

import cl.veterinariasanmarcos.pedidos.model.LineaPedido;
import cl.veterinariasanmarcos.pedidos.model.Pedido;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
  Long id,
  String numero,
  String estado,
  Integer total,
  LocalDateTime creadoEn,
  String clienteCorreo,
  String nombre,
  String apellidos,
  String correo,
  String calle,
  String departamento,
  String region,
  String comuna,
  String indicaciones,
  List<Linea> lineas
) {
  public record Linea(String codigo, String nombre, Integer precioUnitario, Integer cantidad, Integer subtotal) {}

  public static PedidoResponse de(Pedido p) {
    List<Linea> lineas = p.getLineas().stream()
      .map((LineaPedido l) -> new Linea(l.getProductoCodigo(), l.getProductoNombre(), l.getPrecioUnitario(),
        l.getCantidad(), l.subtotal()))
      .toList();
    return new PedidoResponse(p.getId(), String.format("%08d", p.getId()), p.getEstado(), p.getTotal(),
      p.getCreadoEn(), p.getClienteCorreo(), p.getNombre(), p.getApellidos(), p.getCorreoContacto(),
      p.getCalle(), p.getDepartamento(), p.getRegion(), p.getComuna(), p.getIndicaciones(), lineas);
  }
}
