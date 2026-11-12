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

  public boolean esRecepcionista() {
    Authentication a = SecurityContextHolder.getContext().getAuthentication();
    return a != null && a.getAuthorities().stream()
      .anyMatch(g -> g.getAuthority().equals("ROLE_RECEPCIONISTA"));
  }
}
