package cl.veterinariasanmarcos.pedidos.controller;

import cl.veterinariasanmarcos.pedidos.dto.PedidoRequest;
import cl.veterinariasanmarcos.pedidos.dto.PedidoResponse;
import cl.veterinariasanmarcos.pedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Compras de la tienda: el cliente ve las suyas; el personal las gestiona")
public class PedidoController {
  private final PedidoService service;

  public PedidoController(PedidoService service) {
    this.service = service;
  }

  @Operation(summary = "Registrar una compra (usuario con sesión)")
  @PostMapping
  public ResponseEntity<PedidoResponse> crear(@Valid @RequestBody PedidoRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
  }

  @Operation(summary = "Listar pedidos (los propios, o todos si eres recepción o administrador)")
  @GetMapping
  public List<PedidoResponse> listar() {
    return service.listar();
  }

  @Operation(summary = "Ver un pedido por id")
  @GetMapping("/{id}")
  public PedidoResponse buscar(@PathVariable Long id) {
    return service.buscar(id);
  }

  @Operation(summary = "Marcar un pedido como entregado (recepción o administrador)")
  @PutMapping("/{id}/entregar")
  public PedidoResponse entregar(@PathVariable Long id) {
    return service.entregar(id);
  }

  @Operation(summary = "Cancelar un pedido y devolver el stock (recepción o administrador)")
  @PutMapping("/{id}/cancelar")
  public PedidoResponse cancelar(@PathVariable Long id) {
    return service.cancelar(id);
  }
}
