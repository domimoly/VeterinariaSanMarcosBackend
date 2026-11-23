package cl.veterinariasanmarcos.pedidos.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Un producto dentro de un pedido. Guarda el nombre y el precio del momento de la compra, así el historial
 * no cambia si después se edita o elimina el producto.
 */
@Entity
@Table(name = "lineas_pedido")
@Getter
@Setter
public class LineaPedido {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "pedido_id")
  private Pedido pedido;

  @Column(nullable = false, length = 20)
  private String productoCodigo;

  @Column(nullable = false, length = 100)
  private String productoNombre;

  @Column(nullable = false)
  private Integer precioUnitario;

  @Column(nullable = false)
  private Integer cantidad;

  public int subtotal() {
    return precioUnitario * cantidad;
  }
}
