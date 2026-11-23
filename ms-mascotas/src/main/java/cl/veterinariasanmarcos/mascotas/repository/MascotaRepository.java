package cl.veterinariasanmarcos.mascotas.repository;

import cl.veterinariasanmarcos.mascotas.model.Mascota;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {
  List<Mascota> findByTutorCorreoIgnoreCase(String tutorCorreo);
}
