package cl.veterinariasanmarcos.auth.exception;

import io.jsonwebtoken.JwtException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<Map<String, String>> credencialesInvalidas() {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
      .body(Map.of("mensaje", "Credenciales inválidas"));
  }

  @ExceptionHandler(JwtException.class)
  public ResponseEntity<Map<String, String>> tokenInvalido() {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
      .body(Map.of("mensaje", "Token inválido o expirado"));
  }

  @ExceptionHandler(CorreoDuplicadoException.class)
  public ResponseEntity<Map<String, String>> correoDuplicado(CorreoDuplicadoException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
      .body(Map.of("mensaje", e.getMessage()));
  }

  @ExceptionHandler(RunDuplicadoException.class)
  public ResponseEntity<Map<String, String>> runDuplicado(RunDuplicadoException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
      .body(Map.of("mensaje", e.getMessage()));
  }

  @ExceptionHandler(CuentaDesactivadaException.class)
  public ResponseEntity<Map<String, String>> cuentaDesactivada(CuentaDesactivadaException e) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
      .body(Map.of("mensaje", e.getMessage()));
  }

  @ExceptionHandler(RecursoNoEncontradoException.class)
  public ResponseEntity<Map<String, String>> noEncontrado(RecursoNoEncontradoException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
      .body(Map.of("mensaje", e.getMessage()));
  }

  @ExceptionHandler(ReglaNegocioException.class)
  public ResponseEntity<Map<String, String>> reglaDeNegocio(ReglaNegocioException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
      .body(Map.of("mensaje", e.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> validacion(MethodArgumentNotValidException e) {
    Map<String, String> errores = new LinkedHashMap<>();
    e.getBindingResult().getFieldErrors()
      .forEach(f -> errores.putIfAbsent(f.getField(), f.getDefaultMessage()));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
      .body(Map.of("mensaje", "Datos inválidos", "errores", errores));
  }
}
