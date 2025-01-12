package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.exception.StockNotEnoughException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class ProductStockTest {

    @Test
    @DisplayName("상품 재고를 증가시킨다.")
    void increase() {
        // given
        int newStock = 10;
        int originalStock = 10;

        Product product = new Product("상품1", BigDecimal.valueOf(10_000));
        ProductStock stock = new ProductStock(product, originalStock);
        product.addStock(stock);

        // when
        product.increaseStock(newStock);

        // then
        assertThat(stock.getStock()).isEqualTo(newStock + originalStock);
    }

    @Test
    @DisplayName("상품 재고 감소 시 재고가 부족하면 예외가 발생한다.")
    void decrease_whenNotEnoughStock_throwsStockNotEnoughException() {
        // given
        int originalStock = 10;
        int decreaseStock = 11;

        Product product = new Product("상품1", BigDecimal.valueOf(10_000));

        ProductStock stock = new ProductStock(product, originalStock);
        product.addStock(stock);

        // when // then
        assertThatThrownBy(() -> product.decreaseStock(decreaseStock))
                .isInstanceOf(StockNotEnoughException.class);
    }

    @Test
    @DisplayName("상품 재고를 감소시킨다.")
    void decrease() {
        // given
        int originalStock = 10;
        int decreaseStock = 9;

        Product product = new Product("상품1", BigDecimal.valueOf(10_000));

        ProductStock stock = new ProductStock(product, originalStock);
        product.addStock(stock);

        // when
        product.decreaseStock(decreaseStock);

        // then
        assertThat(stock.getStock()).isEqualTo(originalStock - decreaseStock);
    }
}