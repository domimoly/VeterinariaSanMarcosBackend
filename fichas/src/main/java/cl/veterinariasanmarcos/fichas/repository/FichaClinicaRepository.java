package cl.veterinariasanmarcos.fichas.repository;

import cl.veterinariasanmarcos.fichas.model.FichaClinica;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FichaClinicaRepository extends JpaRepository<FichaClinica, Long> {
  List<FichaClinica> findByMascotaIdOrderByFechaDesc(Long mascotaId);
}
