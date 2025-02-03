package kr.hhplus.be.server.infrastructures.core.product;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.product.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductStockJpaRepository extends JpaRepository<ProductStock, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ps from ProductStock ps where ps.product.id in :productIds")
    List<ProductStock> findByProductIdsWithLock(@Param("productIds") List<Long> productIds);

}
