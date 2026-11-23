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

  /**
   * Catálogo base: los mismos 28 servicios (mismos códigos) que muestra el frontend en /servicios.
   * Al iniciar crea los que falten y alinea nombre, duración, precio y disponibilidad en línea de los que
   * ya existen con ese código (así una base creada con un catálogo anterior queda igual al frontend).
   */
  @Bean
  CommandLineRunner serviciosIniciales(ServicioRepository repo) {
    return args -> {
      List<Servicio> catalogo = List.of(
      servicio("consulta-general", "Consulta General", 30, 15000, true),
      servicio("consulta-urgencia", "Consulta Urgencia", 30, 25000, false),
      servicio("segunda-opinion-medica", "Segunda Opinión Médica", 40, 20000, true),
      servicio("consulta-ave-conejo", "Consulta Ave/Conejo", 30, 18000, true),
      servicio("vacuna-sextuple-canina", "Vacuna Séxtuple Canina", 10, 18000, true),
      servicio("vacuna-antirrabica-canina", "Vacuna Antirrábica Canina", 10, 12000, true),
      servicio("vacuna-triple-felina", "Vacuna Triple Felina", 10, 17000, true),
      servicio("vacuna-bordetella-canina", "Vacuna Bordetella Canina", 10, 14000, true),
      servicio("esterilizacion-canina", "Esterilización Canina", 90, 80000, true),
      servicio("esterilizacion-macho-canino", "Esterilización Macho Canino", 60, 60000, true),
      servicio("esterilizacion-hembra-felina", "Esterilización Hembra Felina", 60, 65000, true),
      servicio("esterilizacion-macho-felino", "Esterilización Macho Felino", 45, 50000, true),
      servicio("extirpacion-de-tumor-cutaneo", "Extirpación de Tumor Cutáneo", 60, 120000, true),
      servicio("cesarea-de-urgencia", "Cesárea de Urgencia", 120, 180000, false),
      servicio("interna-pequenos-10-kg", "Interna Pequeños (< 10 kg)", 5, 8000, true),
      servicio("interna-medianos-10-25-kg", "Interna Medianos (10-25 kg)", 5, 9500, true),
      servicio("interna-grandes-25-kg", "Interna Grandes (> 25 kg)", 5, 11000, true),
      servicio("interna-felina", "Interna Felina", 5, 8000, true),
      servicio("antiparasitario-externo-pipeta", "Antiparasitario Externo (Pipeta)", 5, 7500, true),
      servicio("hemograma-completo", "Hemograma Completo", 30, 22000, true),
      servicio("perfil-bioquimico-completo", "Perfil Bioquímico Completo", 30, 35000, true),
      servicio("radiografia-1-proyeccion", "Radiografía (1 proyección)", 20, 28000, true),
      servicio("ecografia-abdominal", "Ecografía Abdominal", 30, 45000, true),
      servicio("test-de-leishmaniasis", "Test de Leishmaniasis", 20, 18000, true),
      servicio("corte-de-unas", "Corte de Uñas", 15, 5000, true),
      servicio("limpieza-dental", "Limpieza Dental", 45, 55000, true),
      servicio("microchip-de-identificacion", "Microchip de Identificación", 10, 15000, true),
      servicio("hospitalizacion-por-dia", "Hospitalización (por día)", 1440, 30000, false)
      );
      for (Servicio s : catalogo) {
        Servicio guardado = repo.findByCodigo(s.getCodigo()).orElse(s);
        guardado.setNombre(s.getNombre());
        guardado.setDuracionMin(s.getDuracionMin());
        guardado.setPrecio(s.getPrecio());
        guardado.setAgendableOnline(s.isAgendableOnline());
        repo.save(guardado);
      }
    };
  }
}
