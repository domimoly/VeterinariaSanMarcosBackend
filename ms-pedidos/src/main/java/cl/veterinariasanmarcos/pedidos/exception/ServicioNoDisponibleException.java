package cl.veterinariasanmarcos.pedidos.exception;

public class ServicioNoDisponibleException extends RuntimeException {
  public ServicioNoDisponibleException(String mensaje) {
    super(mensaje);
  }
}
