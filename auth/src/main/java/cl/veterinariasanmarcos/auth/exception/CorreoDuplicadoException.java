package cl.veterinariasanmarcos.auth.exception;

public class CorreoDuplicadoException extends RuntimeException {
  public CorreoDuplicadoException(String mensaje) {
    super(mensaje);
  }
}
