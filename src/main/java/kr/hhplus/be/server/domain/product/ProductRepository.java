package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.dto.ProductSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductRepository {

    Page<Product> findAll(ProductSearchDto searchDto, Pageable pageable);

    Optional<Product> findById(Long productId);

}
