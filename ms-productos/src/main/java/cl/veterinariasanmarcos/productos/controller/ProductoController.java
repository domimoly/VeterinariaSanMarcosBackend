package cl.veterinariasanmarcos.productos.controller;

import cl.veterinariasanmarcos.productos.dto.MovimientoStockRequest;
import cl.veterinariasanmarcos.productos.dto.ProductoRequest;
import cl.veterinariasanmarcos.productos.model.Producto;
import cl.veterinariasanmarcos.productos.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Catálogo público; el administrador lo mantiene")
public class ProductoController {
  private final ProductoService service;

  public ProductoController(ProductoService service) {
    this.service = service;
  }

  @Operation(summary = "Listar productos activos (público). El administrador puede incluir los inactivos")
  @SecurityRequirements
  @GetMapping
  public List<Producto> listar(@RequestParam(defaultValue = "false") boolean incluirInactivos) {
    return service.listar(incluirInactivos);
  }

  @Operation(summary = "Buscar un producto por id (público)")
  @SecurityRequirements
  @GetMapping("/{id}")
  public Producto buscar(@PathVariable Long id) {
    return service.buscar(id);
  }

  @Operation(summary = "Buscar un producto por código (público)")
  @SecurityRequirements
  @GetMapping("/codigo/{codigo}")
  public Producto buscarPorCodigo(@PathVariable String codigo) {
    return service.buscarPorCodigo(codigo);
  }

  @Operation(summary = "Crear un producto (administrador)")
  @PostMapping
  public ResponseEntity<Producto> crear(@Valid @RequestBody ProductoRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
  }

  @Operation(summary = "Editar un producto (administrador)")
  @PutMapping("/{id}")
  public Producto actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
    return service.actualizar(id, request);
  }

  @Operation(summary = "Eliminar un producto (administrador)")
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void eliminar(@PathVariable Long id) {
    service.eliminar(id);
  }

  @Operation(summary = "Descontar o devolver stock (uso interno de ms-pedidos, requiere la clave interna)")
  @PutMapping("/stock")
  public List<Producto> moverStock(@Valid @RequestBody MovimientoStockRequest request,
      @RequestHeader(value = "X-Clave-Interna", required = false) String claveInterna) {
    return service.moverStock(request, claveInterna);
  }
}
