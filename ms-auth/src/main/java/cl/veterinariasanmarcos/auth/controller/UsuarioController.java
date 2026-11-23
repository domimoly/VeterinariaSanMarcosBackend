package cl.veterinariasanmarcos.auth.controller;

import cl.veterinariasanmarcos.auth.dto.UsuarioEdicionRequest;
import cl.veterinariasanmarcos.auth.dto.UsuarioRequest;
import cl.veterinariasanmarcos.auth.dto.UsuarioResponse;
import cl.veterinariasanmarcos.auth.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestión de cuentas: solo el administrador")
public class UsuarioController {
  private final UsuarioService service;

  public UsuarioController(UsuarioService service) {
    this.service = service;
  }

  @Operation(summary = "Listar todas las cuentas")
  @GetMapping
  public List<UsuarioResponse> listar() {
    return service.listar();
  }

  @Operation(summary = "Buscar una cuenta por id")
  @GetMapping("/{id}")
  public UsuarioResponse buscar(@PathVariable Long id) {
    return service.buscar(id);
  }

  @Operation(summary = "Crear una cuenta con el rol indicado")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UsuarioResponse crear(@Valid @RequestBody UsuarioRequest request) {
    return service.crear(request);
  }

  @Operation(summary = "Editar nombre, rol y (opcional) contraseña")
  @PutMapping("/{id}")
  public UsuarioResponse actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioEdicionRequest request) {
    return service.actualizar(id, request);
  }

  @Operation(summary = "Desactivar una cuenta (no podrá iniciar sesión)")
  @PutMapping("/{id}/desactivar")
  public UsuarioResponse desactivar(@PathVariable Long id) {
    return service.cambiarEstado(id, false);
  }

  @Operation(summary = "Reactivar una cuenta")
  @PutMapping("/{id}/activar")
  public UsuarioResponse activar(@PathVariable Long id) {
    return service.cambiarEstado(id, true);
  }
}
