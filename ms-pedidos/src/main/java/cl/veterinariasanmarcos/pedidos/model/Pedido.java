package cl.veterinariasanmarcos.pedidos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
public class Pedido {
  public static final String PAGADO = "PAGADO";
  public static final String ENTREGADO = "ENTREGADO";
  public static final String CANCELADO = "CANCELADO";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Correo de la cuenta que compró (viene del token). */
  @Column(nullable = false)
  private String clienteCorreo;

  // Datos de contacto y despacho que el cliente escribió al comprar
  @Column(nullable = false, length = 60)
  private String nombre;

  @Column(nullable = false, length = 80)
  private String apellidos;

  @Column(nullable = false, length = 100)
  private String correoContacto;

  @Column(nullable = false, length = 120)
  private String calle;

  @Column(length = 30)
  private String departamento;

  @Column(nullable = false, length = 60)
  private String region;

  @Column(nullable = false, length = 60)
  private String comuna;

  @Column(length = 300)
  private String indicaciones;

  @Column(nullable = false)
  private Integer total;

  @Column(nullable = false, length = 20)
  private String estado;

  @Column(nullable = false)
  private LocalDateTime creadoEn;

  @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("id ASC")
  private List<LineaPedido> lineas = new ArrayList<>();

  public void agregarLinea(LineaPedido linea) {
    linea.setPedido(this);
    lineas.add(linea);
  }
}
