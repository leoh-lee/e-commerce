package kr.hhplus.be.server.infrastructures.core.product;

import kr.hhplus.be.server.domain.product.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductStockJpaRepository extends JpaRepository<ProductStock, Long> {
}
