package cl.veterinariasanmarcos.mascotas.exception;

public class ConflictoException extends RuntimeException {
  public ConflictoException(String mensaje) {
    super(mensaje);
  }
}
