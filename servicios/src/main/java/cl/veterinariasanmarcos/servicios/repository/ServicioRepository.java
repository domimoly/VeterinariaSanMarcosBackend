package cl.veterinariasanmarcos.servicios.repository;

import cl.veterinariasanmarcos.servicios.model.Servicio;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {
  Optional<Servicio> findByCodigo(String codigo);

  boolean existsByCodigo(String codigo);
}
