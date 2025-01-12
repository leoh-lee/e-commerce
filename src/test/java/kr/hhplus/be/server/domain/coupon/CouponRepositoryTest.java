package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.domain.coupon.enums.CouponType;
import kr.hhplus.be.server.infrastructures.core.coupon.CouponRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import(CouponRepositoryImpl.class)
class CouponRepositoryTest {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("모든 쿠폰을 조회한다.")
    void findAll() {
        // given
        CouponInfo couponInfo = new CouponInfo("10% 할인쿠폰", CouponType.PERCENTAGE, 10);
        DiscountInfo discountInfo = new DiscountInfo(null, 10);
        LocalDateTime now = LocalDateTime.now();
        CouponUsableDate couponUsableDate = new CouponUsableDate(now.minusDays(1), now.plusDays(1));

        for (int i = 0; i < 10; i++) {
            Coupon coupon = new Coupon(couponInfo, discountInfo, couponUsableDate);
            em.persist(coupon);
        }

        em.flush();

        // when
        List<Coupon> coupons = couponRepository.findAll();

        // then
        assertThat(coupons).hasSize(10);
    }

}