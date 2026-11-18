package cl.veterinariasanmarcos.fichas.exception;

public class ServicioNoDisponibleException extends RuntimeException {
  public ServicioNoDisponibleException(String mensaje) {
    super(mensaje);
  }
}
