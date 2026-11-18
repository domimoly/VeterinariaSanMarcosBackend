package cl.veterinariasanmarcos.mascotas.service;

import cl.veterinariasanmarcos.mascotas.dto.MascotaRequest;
import cl.veterinariasanmarcos.mascotas.exception.AccesoDenegadoException;
import cl.veterinariasanmarcos.mascotas.exception.RecursoNoEncontradoException;
import cl.veterinariasanmarcos.mascotas.model.Mascota;
import cl.veterinariasanmarcos.mascotas.repository.MascotaRepository;
import cl.veterinariasanmarcos.mascotas.security.UsuarioActual;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MascotaService {
  private final MascotaRepository repository;
  private final UsuarioActual actual;

  public MascotaService(MascotaRepository repository, UsuarioActual actual) {
    this.repository = repository;
    this.actual = actual;
  }

  /** El tutor ve solo las suyas; la recepción ve todas. */
  public List<Mascota> listar() {
    return actual.esRecepcionista()
      ? repository.findAll()
      : repository.findByTutorCorreoIgnoreCase(actual.correo());
  }

  public Mascota buscar(Long id) {
    Mascota m = repository.findById(id).orElseThrow(() ->
      new RecursoNoEncontradoException("Mascota no encontrada: " + id));
    if (!actual.esRecepcionista() && !m.getTutorCorreo().equalsIgnoreCase(actual.correo())) {
      throw new AccesoDenegadoException("Esta mascota pertenece a otro tutor");
    }
    return m;
  }

  public Mascota crear(MascotaRequest r) {
    Mascota m = aplicar(new Mascota(), r);
    m.setTutorCorreo(actual.correo());
    return repository.save(m);
  }

  public Mascota actualizar(Long id, MascotaRequest r) {
    return repository.save(aplicar(buscar(id), r));
  }

  public void eliminar(Long id) {
    Mascota m = buscar(id);
    if (actual.esRecepcionista()) {
      throw new AccesoDenegadoException("Solo el tutor puede eliminar a su mascota");
    }
    repository.delete(m);
  }

  private Mascota aplicar(Mascota m, MascotaRequest r) {
    m.setNombre(r.nombre().trim());
    m.setEspecie(r.especie());
    m.setRaza(r.raza() == null ? null : r.raza().trim());
    m.setSexo(r.sexo());
    m.setPesoKg(r.pesoKg());
    m.setFechaNacimiento(r.fechaNacimiento());
    m.setEsterilizada(r.esterilizada());
    return m;
  }
}
