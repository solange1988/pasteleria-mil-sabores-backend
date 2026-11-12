package cl.milsabores.backend.repository;

import cl.milsabores.backend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByCategoriaIdOrderByIdAsc(Long categoriaId);

    // Stock crítico: productos con stock menor o igual al umbral, del más urgente al menos urgente
    List<Producto> findByStockLessThanEqualOrderByStockAsc(Integer umbral);

    boolean existsByCategoriaId(Long categoriaId);
}
