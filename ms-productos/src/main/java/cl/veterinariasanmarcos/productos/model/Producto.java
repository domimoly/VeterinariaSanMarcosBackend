package cl.veterinariasanmarcos.productos.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "productos")
@Getter
@Setter
public class Producto {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Código único del producto, por ejemplo MED-001. No se modifica después de creado. */
  @Column(nullable = false, unique = true, length = 20)
  private String codigo;

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(nullable = false, length = 40)
  private String categoria;

  @Column(length = 300)
  private String descripcion;

  /** Precio en pesos chilenos. */
  @Column(nullable = false)
  private Integer precio;

  @Column(nullable = false)
  private Integer stock;

  /** Ruta de la imagen dentro del frontend, por ejemplo /img/nexgard.jpg. */
  @Column(length = 200)
  private String imagen;

  /** Un producto inactivo no se muestra en la tienda pero conserva su historial. */
  @Column(nullable = false)
  private boolean activo = true;
}
