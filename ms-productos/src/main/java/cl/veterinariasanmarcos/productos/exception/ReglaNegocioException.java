package cl.veterinariasanmarcos.productos.exception;

public class ReglaNegocioException extends RuntimeException {
  public ReglaNegocioException(String mensaje) {
    super(mensaje);
  }
}
