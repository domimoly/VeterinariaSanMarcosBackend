package cl.veterinariasanmarcos.fichas.security;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Entrega el encabezado Authorization de la solicitud en curso, para reenviarlo a otros servicios. */
@Component
public class TokenActual {

  public String encabezado() {
    if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes sra) {
      return sra.getRequest().getHeader("Authorization");
    }
    return null;
  }
}
