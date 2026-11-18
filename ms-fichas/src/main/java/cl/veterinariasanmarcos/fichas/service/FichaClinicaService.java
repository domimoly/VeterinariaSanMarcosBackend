package cl.veterinariasanmarcos.fichas.service;

import cl.veterinariasanmarcos.fichas.client.MascotaClient;
import cl.veterinariasanmarcos.fichas.dto.FichaRequest;
import cl.veterinariasanmarcos.fichas.exception.RecursoNoEncontradoException;
import cl.veterinariasanmarcos.fichas.exception.ReglaNegocioException;
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

  /** Copia los datos que corresponden al tipo de registro y valida lo que ese tipo exige. */
  private FichaClinica aplicar(FichaClinica f, FichaRequest r) {
    f.setMascotaId(r.mascotaId());
    f.setTipo(r.tipo());
    f.setFecha(r.fecha());

    // Se parte limpio: un registro solo guarda los campos de su tipo
    f.setNombre(null);
    f.setProximaDosis(null);
    f.setMotivo(null);
    f.setDiagnostico(null);
    f.setMedicamentos(null);
    f.setVeterinario(null);
    f.setProximoControl(null);

    if (FichaClinica.CONSULTA.equals(r.tipo())) {
      String motivo = textoObligatorio(r.motivo(), "El motivo es obligatorio");
      String diagnostico = textoObligatorio(r.diagnostico(), "El diagnóstico es obligatorio");
      String veterinario = textoObligatorio(r.veterinario(), "El veterinario es obligatorio");
      if (r.proximoControl() != null && !r.proximoControl().isAfter(r.fecha())) {
        throw new ReglaNegocioException("El próximo control debe ser posterior a la fecha de la consulta");
      }
      f.setMotivo(motivo);
      f.setDiagnostico(diagnostico);
      f.setVeterinario(veterinario);
      f.setMedicamentos(r.medicamentos() == null || r.medicamentos().isBlank()
        ? "Ninguno" : r.medicamentos().trim());
      f.setProximoControl(r.proximoControl());
    } else {
      String nombre = textoObligatorio(r.nombre(), "El nombre es obligatorio");
      if (nombre.length() < 2) {
        throw new ReglaNegocioException("El nombre debe tener al menos 2 caracteres");
      }
      if (r.proximaDosis() == null) {
        throw new ReglaNegocioException("La próxima dosis es obligatoria");
      }
      if (!r.proximaDosis().isAfter(r.fecha())) {
        throw new ReglaNegocioException("La próxima dosis debe ser posterior a la fecha de aplicación");
      }
      f.setNombre(nombre);
      f.setProximaDosis(r.proximaDosis());
    }
    return f;
  }

  private String textoObligatorio(String valor, String mensaje) {
    if (valor == null || valor.isBlank()) {
      throw new ReglaNegocioException(mensaje);
    }
    return valor.trim();
  }
}
