package cl.veterinariasanmarcos.auth.model;

import jakarta.persistence.*;
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
}
