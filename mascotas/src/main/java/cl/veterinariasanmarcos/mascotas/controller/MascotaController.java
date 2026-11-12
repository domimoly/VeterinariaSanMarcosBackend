package cl.veterinariasanmarcos.mascotas.controller;

import cl.veterinariasanmarcos.mascotas.dto.MascotaRequest;
import cl.veterinariasanmarcos.mascotas.model.Mascota;
import cl.veterinariasanmarcos.mascotas.service.MascotaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mascotas")
@Tag(name = "Mascotas", description = "El tutor gestiona las suyas; recepción las consulta")
public class MascotaController {
  private final MascotaService service;

  public MascotaController(MascotaService service) {
    this.service = service;
  }

  @Operation(summary = "Listar mascotas (las propias si eres tutor, todas si eres recepción)")
  @GetMapping
  public List<Mascota> listar() {
    return service.listar();
  }

  @Operation(summary = "Buscar una mascota por id")
  @GetMapping("/{id}")
  public Mascota buscar(@PathVariable Long id) {
    return service.buscar(id);
  }

  @Operation(summary = "Registrar una mascota (tutor)")
  @PostMapping
  public ResponseEntity<Mascota> crear(@Valid @RequestBody MascotaRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
  }

  @Operation(summary = "Actualizar una mascota")
  @PutMapping("/{id}")
  public Mascota actualizar(@PathVariable Long id, @Valid @RequestBody MascotaRequest request) {
    return service.actualizar(id, request);
  }

  @Operation(summary = "Eliminar una mascota (tutor)")
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void eliminar(@PathVariable Long id) {
    service.eliminar(id);
  }
}
