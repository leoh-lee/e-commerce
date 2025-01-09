package kr.hhplus.be.server.infrastructures.core.product;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.dto.ProductSearchDto;
import kr.hhplus.be.server.domain.product.QProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Product> findAll(ProductSearchDto searchDto, Pageable pageable) {
        QProduct product = QProduct.product;

        List<Product> result = queryFactory.selectFrom(product)
                .where(
                        likeProductName(product, searchDto),
                        betweenPrice(product, searchDto)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(product.count())
                .from(product)
                .where(
                        likeProductName(product, searchDto),
                        betweenPrice(product, searchDto)
                );

        return PageableExecutionUtils.getPage(result, pageable, () -> countQuery.fetchOne());
    }

    @Override
    public Optional<Product> findById(Long productId) {
        return productJpaRepository.findById(productId);
    }

    public void save(Product product) {
        productJpaRepository.save(product);
    }

    private BooleanExpression likeProductName(QProduct product, ProductSearchDto searchDto) {
        String productName = searchDto.productName();

        if (!StringUtils.hasText(productName)) {
            return null;
        }
        return product.productName.contains(productName);
    }

    private BooleanExpression betweenPrice(QProduct product, ProductSearchDto searchDto) {
        Integer minPrice = searchDto.minPrice();
        Integer maxPrice = searchDto.maxPrice();

        if (ObjectUtils.isEmpty(minPrice) || ObjectUtils.isEmpty(maxPrice)) {
            return null;
        }
        return product.productPrice.between(minPrice, maxPrice);
    }
}
