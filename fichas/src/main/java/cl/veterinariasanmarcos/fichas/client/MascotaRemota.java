package cl.veterinariasanmarcos.fichas.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Datos de una mascota tal como los entrega el microservicio mascotas. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MascotaRemota(
  Long id,
  String tutorCorreo,
  String nombre,
  String especie,
  boolean esterilizada
) {}
