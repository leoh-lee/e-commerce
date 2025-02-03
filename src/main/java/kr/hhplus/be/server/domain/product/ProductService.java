package kr.hhplus.be.server.domain.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import kr.hhplus.be.server.config.annotation.DistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.hhplus.be.server.domain.order.dto.OrderProductDto;
import kr.hhplus.be.server.domain.product.dto.ProductSearchDto;
import kr.hhplus.be.server.domain.product.dto.ProductSearchResult;
import kr.hhplus.be.server.domain.product.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductStockRepository productStockRepository;

    public Page<ProductSearchResult> searchProducts(ProductSearchDto searchDto, Pageable pageable) {
        return productRepository.findAll(searchDto, pageable).map(ProductSearchResult::fromEntity);
    }

    public ProductSearchResult searchProduct(Long productId) {
        Product product = productRepository.findById(productId);

        return ProductSearchResult.fromEntity(product);
    }

    public BigDecimal getTotalPriceBy(List<OrderProductDto> orderProductsDtos) {

        Map<Long, Integer> productQuantityMap = orderProductsDtos.stream()
                .collect(Collectors.toMap(OrderProductDto::productId, OrderProductDto::quantity));

        List<Long> productIds = orderProductsDtos.stream()
                .map(OrderProductDto::productId).toList();

        List<Product> products = productRepository.findByIds(productIds);

        // 하나라도 없으면? 어떻게 해야할까
        if (productIds.size() != products.size()) {
            throw new ProductNotFoundException();
        }

        return products.stream()
                .map(product -> product.getProductPrice().multiply(BigDecimal.valueOf(productQuantityMap.get(product.getId()))))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void decreaseProductStocks(List<OrderProductDto> orderProductsDtos) {
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

    @DistributedLock(key = "'order_product_'.concat(#orderProductsDto.productId())")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decreaseProductStock(OrderProductDto orderProductsDto) {
        Product product = productRepository.findById(orderProductsDto.productId());
        int originalStock = product.getStock().getStock();
        product.decreaseStock(orderProductsDto.quantity());
        log.info("Decrease stock >>> productId: {}, originalStock: {}, decreaseStock: {}, finalStock: {}", product.getId(), originalStock, orderProductsDto.quantity(), product.getStock().getStock());
    }
}
