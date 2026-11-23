package cl.veterinariasanmarcos.auth.exception;

public class CuentaDesactivadaException extends RuntimeException {
  public CuentaDesactivadaException(String mensaje) {
    super(mensaje);
  }
}
