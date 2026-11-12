package cl.veterinariasanmarcos.citas.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Datos de un servicio tal como los entrega el microservicio servicios. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ServicioRemoto(
  String codigo,
  String nombre,
  Integer duracionMin,
  Integer precio,
  boolean agendableOnline
) {}
