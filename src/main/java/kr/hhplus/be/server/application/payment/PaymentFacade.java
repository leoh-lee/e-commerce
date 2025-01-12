package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.dto.OrderSearchResult;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.dto.PaymentResult;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.user.UserSearchResult;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatform;
import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatformSendRequest;
import kr.hhplus.be.server.infrastructures.external.dataplatform.RequestType;
import kr.hhplus.be.server.interfaces.api.payment.request.PaymentRequest;
import kr.hhplus.be.server.interfaces.api.payment.response.PaymentResponse;
import kr.hhplus.be.server.support.util.DateTimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentFacade {

    private final UserService userService;
    private final PaymentService paymentService;
    private final OrderService orderService;
    private final PointService pointService;
    private final DataPlatform dataPlatform;
    private final DateTimeProvider dateTimeProvider;

    @Transactional
    public PaymentResponse payment(PaymentRequest paymentRequest) {
        Long orderId = paymentRequest.orderId();

        OrderSearchResult order = orderService.getOrderById(orderId);
        Long userId = order.userId();
        BigDecimal finalPrice = order.finalPrice();

        UserSearchResult userById = userService.getUserById(userId);

        PaymentResult paymentResult = paymentService.save(orderId, finalPrice);
        pointService.usePoint(userId, finalPrice);

        dataPlatform.send(new DataPlatformSendRequest<>(userId, RequestType.PAYMENT, dateTimeProvider.getLocalDateTimeNow(), paymentRequest));

        return PaymentResponse.from(paymentResult);
    }

}
