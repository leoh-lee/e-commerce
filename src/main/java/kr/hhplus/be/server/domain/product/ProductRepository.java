package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.dto.ProductSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Page<Product> findAll(ProductSearchDto searchDto, Pageable pageable);

    Product findById(Long productId);

    void save(Product product);

    List<Product> findByIds(List<Long> productIds);
}
