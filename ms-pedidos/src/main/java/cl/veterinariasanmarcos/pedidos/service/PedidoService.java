package cl.veterinariasanmarcos.pedidos.service;

import cl.veterinariasanmarcos.pedidos.client.ProductoClient;
import cl.veterinariasanmarcos.pedidos.client.ProductoRemoto;
import cl.veterinariasanmarcos.pedidos.dto.PedidoRequest;
import cl.veterinariasanmarcos.pedidos.dto.PedidoResponse;
import cl.veterinariasanmarcos.pedidos.exception.AccesoDenegadoException;
import cl.veterinariasanmarcos.pedidos.exception.ConflictoException;
import cl.veterinariasanmarcos.pedidos.exception.RecursoNoEncontradoException;
import cl.veterinariasanmarcos.pedidos.exception.ReglaNegocioException;
import cl.veterinariasanmarcos.pedidos.model.LineaPedido;
import cl.veterinariasanmarcos.pedidos.model.Pedido;
import cl.veterinariasanmarcos.pedidos.repository.PedidoRepository;
import cl.veterinariasanmarcos.pedidos.security.UsuarioActual;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoService {
  private static final ZoneId ZONA = ZoneId.of("America/Santiago");
  private static final int MAX_POR_PRODUCTO = 99;

  private final PedidoRepository repository;
  private final ProductoClient productos;
  private final UsuarioActual actual;

  public PedidoService(PedidoRepository repository, ProductoClient productos, UsuarioActual actual) {
    this.repository = repository;
    this.productos = productos;
    this.actual = actual;
  }

  /**
   * Registra una compra: toma el precio real de cada producto desde el catálogo, descuenta el stock
   * (todo o nada) y guarda el pedido. Si no se puede guardar, devuelve el stock descontado.
   */
  public PedidoResponse crear(PedidoRequest r) {
    // Une los productos repetidos
    Map<String, Integer> cantidades = new LinkedHashMap<>();
    for (PedidoRequest.Linea l : r.lineas()) {
      cantidades.merge(l.codigo().trim().toUpperCase(), l.cantidad(), Integer::sum);
    }

    Pedido pedido = new Pedido();
    int total = 0;
    List<ProductoClient.Linea> aDescontar = new ArrayList<>();
    for (Map.Entry<String, Integer> e : cantidades.entrySet()) {
      if (e.getValue() > MAX_POR_PRODUCTO) {
        throw new ReglaNegocioException("La cantidad máxima por producto es " + MAX_POR_PRODUCTO);
      }
      ProductoRemoto p = productos.buscarPorCodigo(e.getKey());
      if (!p.activo()) {
        throw new ConflictoException("El producto " + p.nombre() + " ya no está disponible");
      }
      if (p.stock() < e.getValue()) {
        throw new ConflictoException("Stock insuficiente de " + p.nombre() + ": quedan " + p.stock());
      }
      LineaPedido linea = new LineaPedido();
      linea.setProductoCodigo(p.codigo());
      linea.setProductoNombre(p.nombre());
      linea.setPrecioUnitario(p.precio());
      linea.setCantidad(e.getValue());
      pedido.agregarLinea(linea);
      total += linea.subtotal();
      aDescontar.add(new ProductoClient.Linea(p.codigo(), e.getValue()));
    }

    pedido.setClienteCorreo(actual.correo());
    pedido.setNombre(r.nombre().trim());
    pedido.setApellidos(r.apellidos().trim());
    pedido.setCorreoContacto(r.correo().trim());
    pedido.setCalle(r.calle().trim());
    pedido.setDepartamento(vacioANulo(r.departamento()));
    pedido.setRegion(r.region().trim());
    pedido.setComuna(r.comuna().trim());
    pedido.setIndicaciones(vacioANulo(r.indicaciones()));
    pedido.setTotal(total);
    pedido.setEstado(Pedido.PAGADO);
    pedido.setCreadoEn(LocalDateTime.now(ZONA));

    // El descuento es la comprobación definitiva: si otra compra se llevó el stock, ms-productos responde 409
    productos.descontarStock(aDescontar);
    try {
      return PedidoResponse.de(repository.save(pedido));
    } catch (RuntimeException e) {
      try {
        productos.devolverStock(aDescontar);
      } catch (RuntimeException ignorada) {
        // Si tampoco se puede devolver, se reporta el error original
      }
      throw e;
    }
  }

  /** El cliente ve sus pedidos; el personal ve todos. */
  @Transactional(readOnly = true)
  public List<PedidoResponse> listar() {
    List<Pedido> pedidos = actual.esRecepcionista()
      ? repository.findAllByOrderByCreadoEnDesc()
      : repository.findByClienteCorreoOrderByCreadoEnDesc(actual.correo());
    return pedidos.stream().map(PedidoResponse::de).toList();
  }

  @Transactional(readOnly = true)
  public PedidoResponse buscar(Long id) {
    return PedidoResponse.de(buscarPermitido(id));
  }

  @Transactional
  public PedidoResponse entregar(Long id) {
    Pedido p = buscarPermitido(id);
    if (!Pedido.PAGADO.equals(p.getEstado())) {
      throw new ReglaNegocioException("Solo un pedido pagado se puede marcar como entregado");
    }
    p.setEstado(Pedido.ENTREGADO);
    return PedidoResponse.de(repository.save(p));
  }

  /** Cancela un pedido pagado y devuelve el stock. */
  @Transactional
  public PedidoResponse cancelar(Long id) {
    Pedido p = buscarPermitido(id);
    if (Pedido.CANCELADO.equals(p.getEstado())) {
      throw new ReglaNegocioException("El pedido ya está cancelado");
    }
    if (Pedido.ENTREGADO.equals(p.getEstado())) {
      throw new ReglaNegocioException("Un pedido entregado no se puede cancelar");
    }
    productos.devolverStock(p.getLineas().stream()
      .map(l -> new ProductoClient.Linea(l.getProductoCodigo(), l.getCantidad()))
      .toList());
    p.setEstado(Pedido.CANCELADO);
    return PedidoResponse.de(repository.save(p));
  }

  private Pedido buscarPermitido(Long id) {
    Pedido p = repository.findById(id)
      .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado"));
    if (!actual.esRecepcionista() && !p.getClienteCorreo().equalsIgnoreCase(actual.correo())) {
      throw new AccesoDenegadoException("Este pedido no es tuyo");
    }
    return p;
  }

  private String vacioANulo(String texto) {
    return texto == null || texto.isBlank() ? null : texto.trim();
  }
}
