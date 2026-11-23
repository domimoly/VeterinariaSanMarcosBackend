package cl.veterinariasanmarcos.productos.config;

import cl.veterinariasanmarcos.productos.model.Producto;
import cl.veterinariasanmarcos.productos.repository.ProductoRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatosIniciales {

  private Producto producto(String codigo, String nombre, String categoria, int precio, int stock,
      String descripcion, String imagen) {
    Producto p = new Producto();
    p.setCodigo(codigo);
    p.setNombre(nombre);
    p.setCategoria(categoria);
    p.setPrecio(precio);
    p.setStock(stock);
    p.setDescripcion(descripcion);
    p.setImagen(imagen);
    p.setActivo(true);
    return p;
  }

  /**
   * Catálogo inicial de la tienda (22 productos). Solo crea los que faltan: si el administrador ya
   * cambió el precio o el stock de un producto, el arranque no lo vuelve a modificar.
   */
  @Bean
  CommandLineRunner productosIniciales(ProductoRepository repo) {
    return args -> {
      List<Producto> catalogo = List.of(
      producto("MED-001", "Amoxibay 250mg", "Medicamento", 4200, 15, "Antibiótico de amplio espectro.", "/img/amoxibay.jpg"),
      producto("MED-002", "Enrox 50mg", "Medicamento", 6800, 12, "Antibacteriano para perros y gatos.", "/img/ENROX.jpg"),
      producto("MED-003", "Metrobay 250mg", "Medicamento", 3900, 20, "Antimicrobiano de uso veterinario.", "/img/METROBAY.jpg"),
      producto("ANT-001", "Nexgard", "Antiparasitario", 9500, 25, "Tableta masticable antiparasitaria.", "/img/NEXGARD.jpg"),
      producto("ANT-002", "Bravecto", "Antiparasitario", 18900, 10, "Protección de hasta 12 semanas contra pulgas y garrapatas.", "/img/BRAVECTO.png"),
      producto("ANT-003", "Revolution Plus", "Antiparasitario", 14500, 14, "Protección mensual tópica para gatos.", "/img/REVOLUTIONPLUS.jpg"),
      producto("ANT-004", "Drontal Plus", "Antiparasitario", 3200, 30, "Desparasitante interno de amplio espectro.", "/img/DONTRALPLUS.jpg"),
      producto("ANT-005", "Milbemax Gato", "Antiparasitario", 6800, 18, "Tratamiento contra nematodos y cestodos.", "/img/MILBEMAX.jpg"),
      producto("MED-004", "Meloxicam 1mg", "Medicamento", 4500, 22, "Antiinflamatorio no esteroideo.", "/img/MELOXICAM.jpg"),
      producto("MED-005", "Carprofen 50mg", "Medicamento", 9800, 15, "Alivio del dolor e inflamación.", "/img/CARPROFEN.jpg"),
      producto("ACC-001", "Clorhexidina shampoo", "Accesorio", 8900, 8, "Shampoo antiséptico para mascotas.", "/img/Clorhexidina shampoo.jpg"),
      producto("ACC-002", "Malaseb shampoo", "Accesorio", 12500, 6, "Shampoo medicado antibacteriano y antifúngico.", "/img/Malaseb shampoo.jpg"),
      producto("MED-006", "Apoquel 16mg", "Medicamento", 22000, 5, "Tratamiento del prurito asociado con dermatitis.", "/img/Apoquel 16mg.png"),
      producto("MED-007", "Probifor", "Medicamento", 5600, 25, "Suplemento probiótico intestinal.", "/img/Probifor.jpg"),
      producto("MED-008", "Omeprazol 10mg vet", "Medicamento", 3800, 30, "Protector de la mucosa gástrica.", "/img/Omeprazol 10mg vet.jpg"),
      producto("MED-009", "Vetmedin 2.5mg", "Medicamento", 28000, 4, "Tratamiento para la insuficiencia cardíaca.", "/img/Vetmedin 2.5mg.jpg"),
      producto("MED-010", "Tramadol 50mg vet", "Medicamento", 5200, 15, "Analgésico de acción central.", "/img/Tramadol 50mg vet.jpg"),
      producto("MED-011", "Nobivac DHPPi", "Medicamento", 8500, 12, "Vacuna canina múltiple.", "/img/Nobivac DHPPi.jpg"),
      producto("MED-012", "Nobivac Rabies", "Medicamento", 5800, 10, "Vacuna antirrábica.", "/img/Nobivac Rabies.jpg"),
      producto("MED-013", "Felocell CVR", "Medicamento", 7200, 8, "Vacuna para la inmunización felina.", "/img/Felocell CVR.jpg"),
      producto("MED-014", "Omega vet 3-6-9", "Medicamento", 9900, 10, "Suplemento de ácidos grasos.", "/img/omega.jpg"),
      producto("MED-015", "Condrovet forte", "Medicamento", 14500, 12, "Condroprotector para el soporte articular.", "/img/Condrovet forte.jpg"));
      catalogo.stream()
        .filter(p -> !repo.existsByCodigo(p.getCodigo()))
        .forEach(repo::save);
    };
  }
}
