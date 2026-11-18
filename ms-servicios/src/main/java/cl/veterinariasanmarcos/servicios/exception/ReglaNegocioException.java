package cl.veterinariasanmarcos.servicios.exception;

public class ReglaNegocioException extends RuntimeException {
  public ReglaNegocioException(String mensaje) {
    super(mensaje);
  }
}
