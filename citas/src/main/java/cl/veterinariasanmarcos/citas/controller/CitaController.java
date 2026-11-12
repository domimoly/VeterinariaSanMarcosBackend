package cl.veterinariasanmarcos.citas.controller;

import cl.veterinariasanmarcos.citas.dto.CitaRequest;
import cl.veterinariasanmarcos.citas.dto.ReagendarRequest;
import cl.veterinariasanmarcos.citas.model.Cita;
import cl.veterinariasanmarcos.citas.service.CitaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/citas")
@Tag(name = "Citas", description = "El tutor solicita y cancela; recepción confirma y reagenda")
public class CitaController {
  private final CitaService service;

  public CitaController(CitaService service) {
    this.service = service;
  }

  @Operation(summary = "Listar citas (las propias si eres tutor; la agenda completa si eres recepción)")
  @GetMapping
  public List<Cita> listar(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
    return service.listar(fecha);
  }

  @Operation(summary = "Horas con cupo libre para un servicio en una fecha")
  @GetMapping("/disponibilidad")
  public List<String> disponibilidad(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
      @RequestParam String servicio) {
    return service.disponibilidad(fecha, servicio);
  }

  @Operation(summary = "Buscar una cita por id")
  @GetMapping("/{id}")
  public Cita buscar(@PathVariable Long id) {
    return service.buscar(id);
  }

  @Operation(summary = "Solicitar una cita (tutor). Nace como Pendiente")
  @PostMapping
  public ResponseEntity<Cita> crear(@Valid @RequestBody CitaRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
  }

  @Operation(summary = "Confirmar una cita (recepción)")
  @PutMapping("/{id}/confirmar")
  public Cita confirmar(@PathVariable Long id) {
    return service.confirmar(id);
  }

  @Operation(summary = "Proponer otro horario (recepción). Queda como Reagendada")
  @PutMapping("/{id}/reagendar")
  public Cita reagendar(@PathVariable Long id, @Valid @RequestBody ReagendarRequest request) {
    return service.reagendar(id, request);
  }

  @Operation(summary = "Cancelar una cita (su tutor o recepción)")
  @PutMapping("/{id}/cancelar")
  public Cita cancelar(@PathVariable Long id) {
    return service.cancelar(id);
  }
}
