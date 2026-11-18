package cl.veterinariasanmarcos.fichas.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "fichas_clinicas")
@Getter
@Setter
public class FichaClinica {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long mascotaId;

  @Column(nullable = false)
  private LocalDate fecha;

  @Column(nullable = false)
  private String motivo;

  @Column(length = 500)
  private String diagnostico;

  @Column(length = 500)
  private String tratamiento;

  /** Para los avisos de vencimiento de vacunas. */
  private LocalDate proximaVacuna;

  /** Correo de la recepción que registró la ficha. */
  private String registradoPor;
}
