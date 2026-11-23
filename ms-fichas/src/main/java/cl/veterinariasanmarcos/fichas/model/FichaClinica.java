package cl.veterinariasanmarcos.fichas.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * Un registro de la ficha clínica de una mascota. El campo "tipo" indica cuál es:
 * una vacuna, una desparasitación o una consulta. Según el tipo se usan unos campos u otros.
 */
@Entity
@Table(name = "registros_clinicos")
@Getter
@Setter
public class FichaClinica {
  public static final String VACUNA = "VACUNA";
  public static final String DESPARASITACION = "DESPARASITACION";
  public static final String CONSULTA = "CONSULTA";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long mascotaId;

  @Column(nullable = false, length = 20)
  private String tipo;

  /** Fecha de aplicación (vacuna, desparasitación) o de la consulta. */
  @Column(nullable = false)
  private LocalDate fecha;

  // ----- vacunas y desparasitaciones
  @Column(length = 80)
  private String nombre;

  /** Para los avisos de vencimiento. */
  private LocalDate proximaDosis;

  // ----- consultas
  @Column(length = 100)
  private String motivo;

  @Column(length = 300)
  private String diagnostico;

  @Column(length = 300)
  private String medicamentos;

  @Column(length = 80)
  private String veterinario;

  private LocalDate proximoControl;

  /** Correo de la recepción que registró el dato. */
  private String registradoPor;
}
