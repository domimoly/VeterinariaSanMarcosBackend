package cl.veterinariasanmarcos.citas.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private ResponseEntity<Map<String, String>> responder(HttpStatus estado, String mensaje) {
    return ResponseEntity.status(estado).body(Map.of("error", mensaje));
  }

  @ExceptionHandler(RecursoNoEncontradoException.class)
  ResponseEntity<Map<String, String>> noEncontrado(RecursoNoEncontradoException e) {
    return responder(HttpStatus.NOT_FOUND, e.getMessage());
  }

  @ExceptionHandler(AccesoDenegadoException.class)
  ResponseEntity<Map<String, String>> prohibido(AccesoDenegadoException e) {
    return responder(HttpStatus.FORBIDDEN, e.getMessage());
  }

  @ExceptionHandler(ReglaNegocioException.class)
  ResponseEntity<Map<String, String>> regla(ReglaNegocioException e) {
    return responder(HttpStatus.BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(ConflictoException.class)
  ResponseEntity<Map<String, String>> conflicto(ConflictoException e) {
    return responder(HttpStatus.CONFLICT, e.getMessage());
  }

  @ExceptionHandler(ServicioNoDisponibleException.class)
  ResponseEntity<Map<String, String>> noDisponible(ServicioNoDisponibleException e) {
    return responder(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<Map<String, String>> datosInvalidos(MethodArgumentNotValidException e) {
    String mensaje = e.getBindingResult().getFieldErrors().stream()
      .findFirst().map(error -> error.getField() + ": " + error.getDefaultMessage())
      .orElse("Datos inválidos");
    return responder(HttpStatus.BAD_REQUEST, mensaje);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  ResponseEntity<Map<String, String>> cuerpoIlegible() {
    return responder(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud no es válido (revisa fechas AAAA-MM-DD y horas HH:MM)");
  }
}
