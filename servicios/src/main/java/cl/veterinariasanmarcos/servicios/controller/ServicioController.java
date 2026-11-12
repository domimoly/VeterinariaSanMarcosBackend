package cl.veterinariasanmarcos.servicios.controller;

import cl.veterinariasanmarcos.servicios.dto.ServicioRequest;
import cl.veterinariasanmarcos.servicios.model.Servicio;
import cl.veterinariasanmarcos.servicios.service.ServicioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/servicios")
@Tag(name = "Servicios", description = "Catálogo de servicios (modifica solo recepción)")
public class ServicioController {
  private final ServicioService service;

  public ServicioController(ServicioService service) {
    this.service = service;
  }

  @Operation(summary = "Listar servicios")
  @GetMapping
  public List<Servicio> listar() {
    return service.listar();
  }

  @Operation(summary = "Buscar un servicio por id")
  @GetMapping("/{id}")
  public Servicio buscar(@PathVariable Long id) {
    return service.buscar(id);
  }

  @Operation(summary = "Buscar un servicio por su código (lo usa el servicio de citas)")
  @GetMapping("/codigo/{codigo}")
  public Servicio buscarPorCodigo(@PathVariable String codigo) {
    return service.buscarPorCodigo(codigo);
  }

  @Operation(summary = "Crear servicio (recepción)")
  @PostMapping
  public ResponseEntity<Servicio> crear(@Valid @RequestBody ServicioRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
  }

  @Operation(summary = "Actualizar servicio (recepción)")
  @PutMapping("/{id}")
  public Servicio actualizar(@PathVariable Long id, @Valid @RequestBody ServicioRequest request) {
    return service.actualizar(id, request);
  }

  @Operation(summary = "Eliminar servicio (recepción)")
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void eliminar(@PathVariable Long id) {
    service.eliminar(id);
  }
}
