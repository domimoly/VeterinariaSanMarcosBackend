package cl.veterinariasanmarcos.auth.security;

import cl.veterinariasanmarcos.auth.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  public static final String EMISOR = "veterinaria-san-marcos-auth";

  @Value("${app.jwt.secret}") private String secret;
  @Value("${app.jwt.expiration-ms}") private long expirationMs;

  private SecretKey clave() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
  }

  public String generar(Usuario usuario) {
    Date ahora = new Date();
    return Jwts.builder()
      .issuer(EMISOR)
      .subject(usuario.getCorreo())
      .claim("rol", usuario.getRol())
      .issuedAt(ahora)
      .expiration(new Date(ahora.getTime() + expirationMs))
      .signWith(clave())
      .compact();
  }

  /** Valida firma, emisor y expiración. Lanza JwtException si el token no sirve. */
  public Claims validar(String token) {
    try {
      return Jwts.parser()
        .verifyWith(clave())
        .requireIssuer(EMISOR)
        .build()
        .parseSignedClaims(token)
        .getPayload();
    } catch (IllegalArgumentException e) {
      throw new JwtException("Token inválido", e);
    }
  }
}
