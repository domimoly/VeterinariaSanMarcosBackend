package cl.veterinariasanmarcos.productos.exception;

public class AccesoDenegadoException extends RuntimeException {
  public AccesoDenegadoException(String mensaje) {
    super(mensaje);
  }
}
