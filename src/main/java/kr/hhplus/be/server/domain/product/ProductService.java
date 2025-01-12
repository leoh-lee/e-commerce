package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.order.dto.OrderProductDto;
import kr.hhplus.be.server.domain.product.dto.ProductSearchDto;
import kr.hhplus.be.server.domain.product.dto.ProductSearchResult;
import kr.hhplus.be.server.domain.product.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductSearchResult> searchProducts(ProductSearchDto searchDto, Pageable pageable) {
        return productRepository.findAll(searchDto, pageable).map(ProductSearchResult::fromEntity);
    }

    public ProductSearchResult searchProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);

        return ProductSearchResult.fromEntity(product);
    }

    public int getTotalPriceBy(List<OrderProductDto> orderProductsDtos) {

        Map<Long, Integer> productQuantityMap = orderProductsDtos.stream()
                .collect(Collectors.toMap(OrderProductDto::productId, OrderProductDto::quantity));

        List<Long> productIds = orderProductsDtos.stream()
                .map(OrderProductDto::productId).toList();

        List<Product> products = productRepository.findByIds(productIds);

        return products.stream()
                .mapToInt(product -> product.getProductPrice() * productQuantityMap.get(product.getId()))
                .sum();
    }

    public void decreaseProductStock(List<OrderProductDto> orderProductsDtos) {

        Map<Long, Integer> productQuantityMap = orderProductsDtos.stream()
                .collect(Collectors.toMap(OrderProductDto::productId, OrderProductDto::quantity));

        List<Long> productIds = orderProductsDtos.stream()
                .map(OrderProductDto::productId).toList();

        List<Product> products = productRepository.findByIds(productIds);

        products.forEach((product)-> {
            Integer quantity = productQuantityMap.get(product.getId());
            product.decreaseStock(quantity);
        });
    }
}
