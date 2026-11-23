package cl.veterinariasanmarcos.productos.service;

import cl.veterinariasanmarcos.productos.dto.MovimientoStockRequest;
import cl.veterinariasanmarcos.productos.dto.ProductoRequest;
import cl.veterinariasanmarcos.productos.exception.AccesoDenegadoException;
import cl.veterinariasanmarcos.productos.exception.ConflictoException;
import cl.veterinariasanmarcos.productos.exception.RecursoNoEncontradoException;
import cl.veterinariasanmarcos.productos.exception.ReglaNegocioException;
import cl.veterinariasanmarcos.productos.model.Producto;
import cl.veterinariasanmarcos.productos.repository.ProductoRepository;
import cl.veterinariasanmarcos.productos.security.UsuarioActual;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService {
  private final ProductoRepository repository;
  private final UsuarioActual actual;

  @Value("${app.interno.clave}") private String claveInterna;

  public ProductoService(ProductoRepository repository, UsuarioActual actual) {
    this.repository = repository;
    this.actual = actual;
  }

  /** La tienda ve solo los activos; el administrador puede pedir también los inactivos. */
  public List<Producto> listar(boolean incluirInactivos) {
    if (incluirInactivos && actual.esAdministrador()) {
      return repository.findAllByOrderByCategoriaAscNombreAsc();
    }
    return repository.findByActivoTrueOrderByCategoriaAscNombreAsc();
  }

  public Producto buscar(Long id) {
    return visible(repository.findById(id)
      .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado")));
  }

  public Producto buscarPorCodigo(String codigo) {
    return visible(repository.findByCodigo(codigo.trim().toUpperCase())
      .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + codigo)));
  }

  /** Un producto inactivo "no existe" para quien no es administrador. */
  private Producto visible(Producto p) {
    if (!p.isActivo() && !actual.esAdministrador()) {
      throw new RecursoNoEncontradoException("Producto no encontrado");
    }
    return p;
  }

  public Producto crear(ProductoRequest r) {
    String codigo = r.codigo().trim().toUpperCase();
    if (repository.existsByCodigo(codigo)) {
      throw new ConflictoException("Ya existe un producto con el código " + codigo);
    }
    Producto p = new Producto();
    p.setCodigo(codigo);
    return repository.save(aplicar(p, r));
  }

  public Producto actualizar(Long id, ProductoRequest r) {
    Producto p = repository.findById(id)
      .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
    if (!p.getCodigo().equalsIgnoreCase(r.codigo().trim())) {
      throw new ReglaNegocioException("El código de un producto no se puede cambiar");
    }
    return repository.save(aplicar(p, r));
  }

  public void eliminar(Long id) {
    Producto p = repository.findById(id)
      .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
    repository.delete(p);
  }

  private Producto aplicar(Producto p, ProductoRequest r) {
    p.setNombre(r.nombre().trim());
    p.setCategoria(r.categoria().trim());
    p.setDescripcion(r.descripcion() == null ? null : r.descripcion().trim());
    p.setPrecio(r.precio());
    p.setStock(r.stock());
    p.setImagen(r.imagen() == null || r.imagen().isBlank() ? null : r.imagen().trim());
    p.setActivo(r.activo() == null || r.activo());
    return p;
  }

  /**
   * Descuenta o devuelve stock de varios productos en una sola transacción: si alguno no alcanza,
   * no se modifica ninguno. Solo lo puede llamar otro microservicio con la clave interna.
   */
  @Transactional
  public List<Producto> moverStock(MovimientoStockRequest r, String claveRecibida) {
    if (claveRecibida == null || !MessageDigest.isEqual(
        claveRecibida.getBytes(StandardCharsets.UTF_8), claveInterna.getBytes(StandardCharsets.UTF_8))) {
      throw new AccesoDenegadoException("Operación reservada a los servicios internos");
    }
    // Une las líneas repetidas del mismo código
    Map<String, Integer> pedido = new LinkedHashMap<>();
    for (MovimientoStockRequest.Linea l : r.lineas()) {
      pedido.merge(l.codigo().trim().toUpperCase(), l.cantidad(), Integer::sum);
    }
    List<Producto> productos = repository.bloquearPorCodigos(pedido.keySet());
    Map<String, Producto> porCodigo = new LinkedHashMap<>();
    productos.forEach(p -> porCodigo.put(p.getCodigo(), p));

    for (Map.Entry<String, Integer> e : pedido.entrySet()) {
      Producto p = porCodigo.get(e.getKey());
      if (p == null) {
        throw new RecursoNoEncontradoException("Producto no encontrado: " + e.getKey());
      }
      if (r.operacion() == MovimientoStockRequest.Operacion.DESCONTAR) {
        if (!p.isActivo()) {
          throw new ConflictoException("El producto " + p.getNombre() + " ya no está disponible");
        }
        if (p.getStock() < e.getValue()) {
          throw new ConflictoException("Stock insuficiente de " + p.getNombre() + ": quedan " + p.getStock());
        }
        p.setStock(p.getStock() - e.getValue());
      } else {
        p.setStock(p.getStock() + e.getValue());
      }
    }
    return repository.saveAll(productos);
  }
}
