package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.dto.OrderSearchResult;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.dto.PaymentResult;
import kr.hhplus.be.server.domain.payment.dto.PaymentSearchResult;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.exception.UserNotFoundException;
import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatform;
import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatformSendRequest;
import kr.hhplus.be.server.infrastructures.external.dataplatform.RequestType;
import kr.hhplus.be.server.interfaces.api.payment.request.PaymentRequest;
import kr.hhplus.be.server.interfaces.api.payment.response.PaymentResponse;
import kr.hhplus.be.server.interfaces.api.payment.response.PaymentSearchResponse;
import kr.hhplus.be.server.support.util.DateTimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final UserService userService;
    private final PaymentService paymentService;
    private final OrderService orderService;
    private final PointService pointService;
    private final DataPlatform dataPlatform;
    private final DateTimeProvider dateTimeProvider;

    @Transactional
    public PaymentResponse payment(PaymentRequest paymentRequest) {

        Long userId = paymentRequest.userId();

        if (!userService.existsById(userId)) {
            throw new UserNotFoundException();
        }

        Long orderId = paymentRequest.orderId();

        OrderSearchResult order = orderService.getOrderById(orderId);

        BigDecimal finalPrice = order.finalPrice();

        orderService.updateOrderStatusPayed(order.id());

        PaymentResult paymentResult = paymentService.save(orderId, finalPrice);
        pointService.usePoint(userId, finalPrice);

        dataPlatform.send(new DataPlatformSendRequest<>(userId, RequestType.PAYMENT, dateTimeProvider.getLocalDateTimeNow(), paymentRequest));

        return PaymentResponse.from(paymentResult);
    }

    @Transactional(readOnly = true)
    public List<PaymentSearchResponse> searchPaymentsByUserId(Long userId) {
        List<PaymentSearchResult> payments = paymentService.getPaymentsByUserId(userId);

        return payments.stream()
                .map(PaymentSearchResponse::from)
                .toList();
    }

}
