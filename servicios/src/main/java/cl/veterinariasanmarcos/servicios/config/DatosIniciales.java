package cl.veterinariasanmarcos.servicios.config;

import cl.veterinariasanmarcos.servicios.model.Servicio;
import cl.veterinariasanmarcos.servicios.repository.ServicioRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatosIniciales {

  private Servicio servicio(String codigo, String nombre, int duracionMin, int precio, boolean online) {
    Servicio s = new Servicio();
    s.setCodigo(codigo);
    s.setNombre(nombre);
    s.setDuracionMin(duracionMin);
    s.setPrecio(precio);
    s.setAgendableOnline(online);
    return s;
  }

  /** Catálogo base; se puede editar después desde Swagger (solo recepción). */
  @Bean
  CommandLineRunner serviciosIniciales(ServicioRepository repo) {
    return args -> {
      if (repo.count() == 0) {
        repo.saveAll(List.of(
          servicio("consulta-general", "Consulta General", 30, 15000, true),
          servicio("control-sano", "Control Sano", 30, 12000, true),
          servicio("vacuna-sextuple", "Vacuna Séxtuple", 30, 20000, true),
          servicio("vacuna-triple-felina", "Vacuna Triple Felina", 30, 18000, true),
          servicio("desparasitacion", "Desparasitación", 30, 10000, true),
          servicio("esterilizacion", "Esterilización", 90, 60000, true),
          servicio("cirugia-menor", "Cirugía Menor", 60, 45000, true),
          servicio("urgencia", "Urgencia", 30, 30000, false),
          servicio("hospitalizacion", "Hospitalización", 60, 50000, false)
        ));
      }
    };
  }
}
