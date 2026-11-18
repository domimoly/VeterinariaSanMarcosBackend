package cl.veterinariasanmarcos.mascotas.exception;

public class AccesoDenegadoException extends RuntimeException {
  public AccesoDenegadoException(String mensaje) {
    super(mensaje);
  }
}
