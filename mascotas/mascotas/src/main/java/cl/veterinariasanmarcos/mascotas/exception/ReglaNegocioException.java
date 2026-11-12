package cl.veterinariasanmarcos.mascotas.exception;

public class ReglaNegocioException extends RuntimeException {
  public ReglaNegocioException(String mensaje) {
    super(mensaje);
  }
}
