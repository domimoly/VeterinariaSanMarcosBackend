package cl.veterinariasanmarcos.pedidos.exception;

public class ReglaNegocioException extends RuntimeException {
  public ReglaNegocioException(String mensaje) {
    super(mensaje);
  }
}
