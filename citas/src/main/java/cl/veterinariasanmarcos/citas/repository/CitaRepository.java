package cl.veterinariasanmarcos.citas.repository;

import cl.veterinariasanmarcos.citas.model.Cita;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CitaRepository extends JpaRepository<Cita, Long> {
  List<Cita> findByTutorCorreoIgnoreCaseOrderByFechaAscHoraAsc(String tutorCorreo);

  List<Cita> findAllByOrderByFechaAscHoraAsc();

  List<Cita> findByFechaOrderByHoraAsc(LocalDate fecha);

  List<Cita> findByFechaAndEstadoNot(LocalDate fecha, String estado);
}
