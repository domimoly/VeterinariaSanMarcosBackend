package cl.veterinariasanmarcos.auth.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class Usuario {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String nombre;

  @Column(nullable = false, unique = true)
  private String correo;

  @Column(nullable = false)
  private String password;

  /** TUTOR, RECEPCIONISTA o ADMINISTRADOR */
  @Column(nullable = false)
  private String rol;

  /** Una cuenta desactivada no puede iniciar sesión (los datos se conservan). */
  @Column(nullable = false, columnDefinition = "boolean not null default true")
  private boolean activo = true;

  private LocalDateTime creadoEn;

  // Datos personales del tutor ("Mi perfil"). Quedan vacíos hasta que los completa.
  @Column(length = 100)
  private String apellidos;

  /** Formato 12345678-5. Único: dos cuentas no pueden tener el mismo RUN. */
  @Column(unique = true, length = 12)
  private String run;

  private LocalDate fechaNacimiento;

  @Column(length = 20)
  private String telefono;

  @Column(length = 40)
  private String region;

  @Column(length = 80)
  private String comuna;

  @Column(length = 300)
  private String direccion;
}
