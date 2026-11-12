package cl.veterinariasanmarcos.servicios.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "servicios")
@Getter
@Setter
public class Servicio {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String codigo;

  @Column(nullable = false)
  private String nombre;

  @Column(nullable = false)
  private Integer duracionMin;

  @Column(nullable = false)
  private Integer precio;

  @Column(nullable = false)
  private boolean agendableOnline;
}
