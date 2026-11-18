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

/** Consulta al microservicio mascotas reenviando el token del usuario (así se valida el dueño). */
@Component
public class MascotaClient {
  private final RestClient http = RestClient.create();
  private final TokenActual token;

  @Value("${app.mascotas.url}") private String baseUrl;

  public MascotaClient(TokenActual token) {
    this.token = token;
  }

  public MascotaRemota buscar(Long id) {
    try {
      return http.get()
        .uri(baseUrl + "/api/mascotas/{id}", id)
        .header("Authorization", token.encabezado())
        .retrieve()
        .body(MascotaRemota.class);
    } catch (RestClientResponseException e) {
      int codigo = e.getStatusCode().value();
      if (codigo == 404) {
        throw new RecursoNoEncontradoException("Mascota no encontrada: " + id);
      }
      if (codigo == 401 || codigo == 403) {
        throw new AccesoDenegadoException("No tienes acceso a esta mascota");
      }
      throw new ReglaNegocioException("No se pudo consultar la mascota");
    } catch (ResourceAccessException e) {
      throw new ServicioNoDisponibleException("El servicio de mascotas no responde");
    }
  }
}
