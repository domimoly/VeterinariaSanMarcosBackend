package cl.veterinariasanmarcos.mascotas.exception;

public class RecursoNoEncontradoException extends RuntimeException {
  public RecursoNoEncontradoException(String mensaje) {
    super(mensaje);
  }
}
