package cl.veterinariasanmarcos.fichas.service;

import cl.veterinariasanmarcos.fichas.client.MascotaClient;
import cl.veterinariasanmarcos.fichas.dto.FichaRequest;
import cl.veterinariasanmarcos.fichas.exception.RecursoNoEncontradoException;
import cl.veterinariasanmarcos.fichas.model.FichaClinica;
import cl.veterinariasanmarcos.fichas.repository.FichaClinicaRepository;
import cl.veterinariasanmarcos.fichas.security.UsuarioActual;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FichaClinicaService {
  private final FichaClinicaRepository repository;
  private final MascotaClient mascotas;
  private final UsuarioActual actual;

  public FichaClinicaService(FichaClinicaRepository repository, MascotaClient mascotas,
      UsuarioActual actual) {
    this.repository = repository;
    this.mascotas = mascotas;
    this.actual = actual;
  }

  /** Historial de una mascota; el servicio de mascotas valida que el tutor sea el dueño. */
  public List<FichaClinica> listarPorMascota(Long mascotaId) {
    mascotas.buscar(mascotaId);
    return repository.findByMascotaIdOrderByFechaDesc(mascotaId);
  }

  public FichaClinica crear(FichaRequest r) {
    mascotas.buscar(r.mascotaId());
    FichaClinica f = aplicar(new FichaClinica(), r);
    f.setRegistradoPor(actual.correo());
    return repository.save(f);
  }

  public FichaClinica actualizar(Long id, FichaRequest r) {
    FichaClinica f = buscar(id);
    mascotas.buscar(r.mascotaId());
    return repository.save(aplicar(f, r));
  }

  public void eliminar(Long id) {
    repository.delete(buscar(id));
  }

  private FichaClinica buscar(Long id) {
    return repository.findById(id).orElseThrow(() ->
      new RecursoNoEncontradoException("Ficha no encontrada: " + id));
  }

  private FichaClinica aplicar(FichaClinica f, FichaRequest r) {
    f.setMascotaId(r.mascotaId());
    f.setFecha(r.fecha());
    f.setMotivo(r.motivo().trim());
    f.setDiagnostico(r.diagnostico());
    f.setTratamiento(r.tratamiento());
    f.setProximaVacuna(r.proximaVacuna());
    return f;
  }
}
