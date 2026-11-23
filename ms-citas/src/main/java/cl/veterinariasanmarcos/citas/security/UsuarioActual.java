package cl.veterinariasanmarcos.citas.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** Datos del usuario que hizo la solicitud, tomados del token ya validado. */
@Component
public class UsuarioActual {

  public String correo() {
    Authentication a = SecurityContextHolder.getContext().getAuthentication();
    return a == null ? null : String.valueOf(a.getPrincipal());
  }

  /** Personal de la clínica con acceso de gestión: la recepcionista y el administrador (acceso total). */
  public boolean esRecepcionista() {
    return tieneRol("ROLE_RECEPCIONISTA") || tieneRol("ROLE_ADMINISTRADOR");
  }

  public boolean esAdministrador() {
    return tieneRol("ROLE_ADMINISTRADOR");
  }

  private boolean tieneRol(String rol) {
    Authentication a = SecurityContextHolder.getContext().getAuthentication();
    return a != null && a.getAuthorities().stream().anyMatch(g -> g.getAuthority().equals(rol));
  }
}
