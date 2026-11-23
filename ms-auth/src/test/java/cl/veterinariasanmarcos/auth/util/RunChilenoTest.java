package cl.veterinariasanmarcos.auth.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class RunChilenoTest {

  @Test
  void normalizaPuntosYGuion() {
    assertEquals("12345678-5", RunChileno.normalizar("12.345.678-5"));
    assertEquals("12345678-5", RunChileno.normalizar("123456785"));
  }

  @Test
  void aceptaRunDeSieteDigitosYDigitoK() {
    assertEquals("7654321-6", RunChileno.normalizar("7654321-6"));
    assertEquals("10000013-K", RunChileno.normalizar("10000013-k"));
  }

  @Test
  void rechazaDigitoVerificadorIncorrecto() {
    assertNull(RunChileno.normalizar("12345678-K"));
  }

  @Test
  void rechazaFormatosInvalidos() {
    assertNull(RunChileno.normalizar(null));
    assertNull(RunChileno.normalizar("abc"));
    assertNull(RunChileno.normalizar("1-9"));
  }
}
