package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.payment.dto.PaymentSearchResult;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    @DisplayName("결제를 저장한다.")
    void save_success() {
        // given
        doNothing().when(paymentRepository).save(any());

        // when
        paymentService.save(1L, BigDecimal.valueOf(10_000));

        // then
        verify(paymentRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("결제 상태를 변경한다.")
    void changePaymentStatus_success() {
        // given
        long paymentId = 1L;
        Payment payment = new Payment(paymentId, 1L, BigDecimal.valueOf(10_000), PaymentStatus.PENDING);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        // when
        paymentService.changePaymentStatus(paymentId, PaymentStatus.COMPLETED);

        // then
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    @DisplayName("사용자 ID로 결제를 조회한다.")
    void getPaymentsByUserId_success() {
        // given
        long userId = 1L;
        List<Payment> payments = List.of(
                new Payment(1L, BigDecimal.valueOf(10_000), PaymentStatus.PENDING),
                new Payment(2L, BigDecimal.valueOf(20_000), PaymentStatus.COMPLETED)
        );

        when(paymentRepository.findByUserId(userId)).thenReturn(payments);

        // when
        List<PaymentSearchResult> result = paymentService.getPaymentsByUserId(userId);

        // then
        assertThat(result).extracting("orderId", "paymentPrice", "paymentStatus")
                .containsExactly(
                        Tuple.tuple(1L, BigDecimal.valueOf(10_000), PaymentStatus.PENDING),
                        Tuple.tuple(2L, BigDecimal.valueOf(20_000), PaymentStatus.COMPLETED)
                );
    }

}
