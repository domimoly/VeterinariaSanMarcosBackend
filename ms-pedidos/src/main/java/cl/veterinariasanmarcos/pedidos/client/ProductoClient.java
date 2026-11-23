package cl.veterinariasanmarcos.pedidos.client;

import cl.veterinariasanmarcos.pedidos.exception.ConflictoException;
import cl.veterinariasanmarcos.pedidos.exception.RecursoNoEncontradoException;
import cl.veterinariasanmarcos.pedidos.exception.ReglaNegocioException;
import cl.veterinariasanmarcos.pedidos.exception.ServicioNoDisponibleException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/** Consulta el catálogo y mueve el stock en ms-productos. */
@Component
public class ProductoClient {
  private static final Pattern CAMPO_ERROR = Pattern.compile("\"error\"\\s*:\\s*\"([^\"]*)\"");

  private final RestClient http = RestClient.create();

  @Value("${app.productos.url}") private String baseUrl;
  @Value("${app.interno.clave}") private String claveInterna;

  /** Línea de un movimiento de stock. */
  public record Linea(String codigo, int cantidad) {}

  /** El catálogo es público, por eso no se envía token. */
  public ProductoRemoto buscarPorCodigo(String codigo) {
    try {
      return http.get()
        .uri(baseUrl + "/api/productos/codigo/{codigo}", codigo)
        .retrieve()
        .body(ProductoRemoto.class);
    } catch (RestClientResponseException e) {
      if (e.getStatusCode().value() == 404) {
        throw new RecursoNoEncontradoException("Producto no encontrado: " + codigo);
      }
      throw new ReglaNegocioException("No se pudo consultar el producto " + codigo);
    } catch (ResourceAccessException e) {
      throw new ServicioNoDisponibleException("El servicio de productos no responde");
    }
  }

  public void descontarStock(List<Linea> lineas) {
    moverStock("DESCONTAR", lineas);
  }

  public void devolverStock(List<Linea> lineas) {
    moverStock("DEVOLVER", lineas);
  }

  private void moverStock(String operacion, List<Linea> lineas) {
    Map<String, Object> cuerpo = Map.of("operacion", operacion, "lineas", lineas);
    try {
      http.put()
        .uri(baseUrl + "/api/productos/stock")
        .header("X-Clave-Interna", claveInterna)
        .body(cuerpo)
        .retrieve()
        .toBodilessEntity();
    } catch (RestClientResponseException e) {
      int estado = e.getStatusCode().value();
      if (estado == 409) {
        throw new ConflictoException(mensajeDe(e, "No hay stock suficiente"));
      }
      if (estado == 404) {
        throw new RecursoNoEncontradoException(mensajeDe(e, "Producto no encontrado"));
      }
      throw new ReglaNegocioException("No se pudo actualizar el stock");
    } catch (ResourceAccessException e) {
      throw new ServicioNoDisponibleException("El servicio de productos no responde");
    }
  }

  private String mensajeDe(RestClientResponseException e, String porDefecto) {
    Matcher m = CAMPO_ERROR.matcher(e.getResponseBodyAsString());
    return m.find() ? m.group(1) : porDefecto;
  }
}
