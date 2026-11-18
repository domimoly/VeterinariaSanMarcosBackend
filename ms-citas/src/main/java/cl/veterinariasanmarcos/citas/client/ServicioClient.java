package cl.veterinariasanmarcos.citas.client;

import cl.veterinariasanmarcos.citas.exception.AccesoDenegadoException;
import cl.veterinariasanmarcos.citas.exception.RecursoNoEncontradoException;
import cl.veterinariasanmarcos.citas.exception.ReglaNegocioException;
import cl.veterinariasanmarcos.citas.exception.ServicioNoDisponibleException;
import cl.veterinariasanmarcos.citas.security.TokenActual;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/** Consulta al microservicio servicios reenviando el token del usuario. */
@Component
public class ServicioClient {
  private final RestClient http = RestClient.create();
  private final TokenActual token;

  @Value("${app.servicios.url}") private String baseUrl;

  public ServicioClient(TokenActual token) {
    this.token = token;
  }

  public ServicioRemoto buscarPorCodigo(String codigo) {
    try {
      return http.get()
        .uri(baseUrl + "/api/servicios/codigo/{codigo}", codigo)
        .header("Authorization", token.encabezado())
        .retrieve()
        .body(ServicioRemoto.class);
    } catch (RestClientResponseException e) {
      int estado = e.getStatusCode().value();
      if (estado == 404) {
        throw new RecursoNoEncontradoException("Servicio no encontrado: " + codigo);
      }
      if (estado == 401 || estado == 403) {
        throw new AccesoDenegadoException("No tienes acceso al catálogo de servicios");
      }
      throw new ReglaNegocioException("No se pudo consultar el servicio");
    } catch (ResourceAccessException e) {
      throw new ServicioNoDisponibleException("El servicio de catálogo no responde");
    }
  }
}
