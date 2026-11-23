package cl.veterinariasanmarcos.pedidos.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Datos de un producto tal como los entrega ms-productos. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductoRemoto(String codigo, String nombre, Integer precio, Integer stock, boolean activo) {}
