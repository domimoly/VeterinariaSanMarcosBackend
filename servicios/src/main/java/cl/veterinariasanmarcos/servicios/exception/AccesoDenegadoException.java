package cl.veterinariasanmarcos.servicios.exception;

public class AccesoDenegadoException extends RuntimeException {
  public AccesoDenegadoException(String mensaje) {
    super(mensaje);
  }
}
