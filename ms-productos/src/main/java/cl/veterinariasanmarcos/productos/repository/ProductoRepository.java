package cl.veterinariasanmarcos.productos.repository;

import cl.veterinariasanmarcos.productos.model.Producto;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

  Optional<Producto> findByCodigo(String codigo);

  boolean existsByCodigo(String codigo);

  List<Producto> findByActivoTrueOrderByCategoriaAscNombreAsc();

  List<Producto> findAllByOrderByCategoriaAscNombreAsc();

  /**
   * Bloquea las filas mientras dura la transacción para que dos compras no descuenten el mismo stock a la vez.
   * Se usa SQL nativo ("for update" simple) porque Hibernate genera "for update of <alias>", sintaxis que
   * MariaDB no acepta.
   */
  @Query(value = "select p.* from productos p where p.codigo in (:codigos) for update", nativeQuery = true)
  List<Producto> bloquearPorCodigos(@Param("codigos") Collection<String> codigos);
}