package cl.veterinariasanmarcos.productos.exception;

public class ConflictoException extends RuntimeException {
  public ConflictoException(String mensaje) {
    super(mensaje);
  }
}
