package cl.veterinariasanmarcos.pedidos.repository;

import cl.veterinariasanmarcos.pedidos.model.Pedido;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

  List<Pedido> findAllByOrderByCreadoEnDesc();

  List<Pedido> findByClienteCorreoOrderByCreadoEnDesc(String clienteCorreo);
}
