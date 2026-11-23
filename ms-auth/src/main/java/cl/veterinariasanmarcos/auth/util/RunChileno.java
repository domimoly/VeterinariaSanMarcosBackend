package cl.veterinariasanmarcos.auth.util;

/** Validación del RUN chileno (módulo 11). */
public final class RunChileno {
  private RunChileno() {}

  /** Devuelve el RUN con el formato "12345678-5", o null si el formato o el dígito verificador no corresponden. */
  public static String normalizar(String texto) {
    if (texto == null) return null;
    String limpio = texto.replace(".", "").replace("-", "").replace(" ", "").toUpperCase();
    if (!limpio.matches("\\d{7,8}[0-9K]")) return null;

    String cuerpo = limpio.substring(0, limpio.length() - 1);
    char digito = limpio.charAt(limpio.length() - 1);
    int suma = 0;
    int factor = 2;
    for (int i = cuerpo.length() - 1; i >= 0; i--) {
      suma += (cuerpo.charAt(i) - '0') * factor;
      factor = factor == 7 ? 2 : factor + 1;
    }
    int resto = 11 - (suma % 11);
    char esperado = resto == 11 ? '0' : resto == 10 ? 'K' : (char) ('0' + resto);
    return digito == esperado ? cuerpo + "-" + digito : null;
  }
}
