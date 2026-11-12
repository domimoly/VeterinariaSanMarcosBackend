package cl.veterinariasanmarcos.fichas.controller;

import cl.veterinariasanmarcos.fichas.dto.FichaRequest;
import cl.veterinariasanmarcos.fichas.model.FichaClinica;
import cl.veterinariasanmarcos.fichas.service.FichaClinicaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fichas")
@Tag(name = "Fichas clínicas", description = "El tutor consulta la de su mascota; recepción registra")
public class FichaClinicaController {
  private final FichaClinicaService service;

  public FichaClinicaController(FichaClinicaService service) {
    this.service = service;
  }

  @Operation(summary = "Historial clínico de una mascota")
  @GetMapping
  public List<FichaClinica> listar(@RequestParam Long mascotaId) {
    return service.listarPorMascota(mascotaId);
  }

  @Operation(summary = "Registrar una ficha (recepción)")
  @PostMapping
  public ResponseEntity<FichaClinica> crear(@Valid @RequestBody FichaRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
  }

  @Operation(summary = "Editar una ficha (recepción)")
  @PutMapping("/{id}")
  public FichaClinica actualizar(@PathVariable Long id, @Valid @RequestBody FichaRequest request) {
    return service.actualizar(id, request);
  }

  @Operation(summary = "Eliminar una ficha (recepción)")
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void eliminar(@PathVariable Long id) {
    service.eliminar(id);
  }
}
