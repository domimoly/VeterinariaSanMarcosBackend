package cl.veterinariasanmarcos.mascotas.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "mascotas")
@Getter
@Setter
public class Mascota {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Correo del tutor dueño (viene del token). */
  @Column(nullable = false)
  private String tutorCorreo;

  @Column(nullable = false)
  private String nombre;

  @Column(nullable = false)
  private String especie;

  private String raza;

  @Column(nullable = false)
  private String sexo;

  @Column(nullable = false)
  private Double pesoKg;

  private LocalDate fechaNacimiento;

  @Column(nullable = false)
  private boolean esterilizada;
}
