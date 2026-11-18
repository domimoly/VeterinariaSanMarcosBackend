package cl.veterinariasanmarcos.auth.dto;

public record PerfilResponse(
  String nombre,
  String correo,
  String rol
) {}
