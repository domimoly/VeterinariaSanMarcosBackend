package cl.veterinariasanmarcos.auth.dto;

public record LoginResponse(
  String token,
  String nombre,
  String correo,
  String rol
) {}
