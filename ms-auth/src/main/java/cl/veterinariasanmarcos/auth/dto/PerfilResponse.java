package cl.veterinariasanmarcos.auth.dto;

import java.time.LocalDate;

public record PerfilResponse(
  String nombre,
  String apellidos,
  String correo,
  String rol,
  String run,
  LocalDate fechaNacimiento,
  String telefono,
  String region,
  String comuna,
  String direccion
) {}
