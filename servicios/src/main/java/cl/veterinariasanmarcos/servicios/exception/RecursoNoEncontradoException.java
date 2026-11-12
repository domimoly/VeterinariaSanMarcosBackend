package cl.veterinariasanmarcos.servicios.exception;

public class RecursoNoEncontradoException extends RuntimeException {
  public RecursoNoEncontradoException(String mensaje) {
    super(mensaje);
  }
}
