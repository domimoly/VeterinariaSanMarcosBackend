package cl.veterinariasanmarcos.auth.exception;

public class RunDuplicadoException extends RuntimeException {
  public RunDuplicadoException(String mensaje) {
    super(mensaje);
  }
}
