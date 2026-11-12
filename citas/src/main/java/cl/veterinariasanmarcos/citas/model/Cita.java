package cl.veterinariasanmarcos.citas.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "citas")
@Getter
@Setter
public class Cita {
  public static final String PENDIENTE = "Pendiente";
  public static final String CONFIRMADA = "Confirmada";
  public static final String REAGENDADA = "Reagendada";
  public static final String CANCELADA = "Cancelada";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Ej: SM-000012 */
  @Column(unique = true)
  private String codigo;

  @Column(nullable = false)
  private String tutorCorreo;

  @Column(nullable = false)
  private Long mascotaId;
  private String mascotaNombre;

  @Column(nullable = false)
  private String servicioCodigo;
  private String servicioNombre;
  private Integer duracionMin;
  private Integer precio;

  @Column(nullable = false)
  private LocalDate fecha;

  @Column(nullable = false)
  private LocalTime hora;

  private String telefono;

  @Column(length = 300)
  private String observaciones;

  @Column(nullable = false)
  private String estado;

  @Column(length = 300)
  private String nota;

  private LocalDateTime creadaEn;
}
