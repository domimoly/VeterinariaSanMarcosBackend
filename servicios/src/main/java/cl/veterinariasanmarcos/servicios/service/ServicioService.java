package cl.veterinariasanmarcos.servicios.service;

import cl.veterinariasanmarcos.servicios.dto.ServicioRequest;
import cl.veterinariasanmarcos.servicios.exception.ConflictoException;
import cl.veterinariasanmarcos.servicios.exception.RecursoNoEncontradoException;
import cl.veterinariasanmarcos.servicios.model.Servicio;
import cl.veterinariasanmarcos.servicios.repository.ServicioRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ServicioService {
  private final ServicioRepository repository;

  public ServicioService(ServicioRepository repository) {
    this.repository = repository;
  }

  public List<Servicio> listar() {
    return repository.findAll();
  }

  public Servicio buscar(Long id) {
    return repository.findById(id).orElseThrow(() ->
      new RecursoNoEncontradoException("Servicio no encontrado: " + id));
  }

  public Servicio buscarPorCodigo(String codigo) {
    return repository.findByCodigo(codigo).orElseThrow(() ->
      new RecursoNoEncontradoException("Servicio no encontrado: " + codigo));
  }

  public Servicio crear(ServicioRequest r) {
    if (repository.existsByCodigo(r.codigo())) {
      throw new ConflictoException("Ya existe un servicio con el código " + r.codigo());
    }
    return repository.save(aplicar(new Servicio(), r));
  }

  public Servicio actualizar(Long id, ServicioRequest r) {
    Servicio actual = buscar(id);
    if (!actual.getCodigo().equals(r.codigo()) && repository.existsByCodigo(r.codigo())) {
      throw new ConflictoException("Ya existe un servicio con el código " + r.codigo());
    }
    return repository.save(aplicar(actual, r));
  }

  public void eliminar(Long id) {
    repository.delete(buscar(id));
  }

  private Servicio aplicar(Servicio s, ServicioRequest r) {
    s.setCodigo(r.codigo());
    s.setNombre(r.nombre().trim());
    s.setDuracionMin(r.duracionMin());
    s.setPrecio(r.precio());
    s.setAgendableOnline(r.agendableOnline());
    return s;
  }
}
