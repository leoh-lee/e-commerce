package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.point.dto.PointChargeResult;
import kr.hhplus.be.server.domain.point.dto.PointSearchResult;
import kr.hhplus.be.server.domain.user.UserSearchResult;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatform;
import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatformSendRequest;
import kr.hhplus.be.server.infrastructures.external.dataplatform.RequestType;
import kr.hhplus.be.server.interfaces.api.point.request.PointChargeRequest;
import kr.hhplus.be.server.interfaces.api.point.response.PointChargeResponse;
import kr.hhplus.be.server.interfaces.api.point.response.PointSearchResponse;
import kr.hhplus.be.server.support.util.DateTimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointFacade {

    private final PointService pointService;
    private final UserService userService;
    private final DataPlatform dataPlatform;
    private final DateTimeProvider dateTimeProvider;

    @Transactional
    public PointChargeResponse chargePoint(PointChargeRequest pointChargeRequest) {
        Long userId = pointChargeRequest.userId();
        UserSearchResult userResult = userService.getUserById(pointChargeRequest.userId());

        PointChargeResult pointChargeResult = pointService.chargePoint(userId, pointChargeRequest.amount());

        dataPlatform.send(new DataPlatformSendRequest<>(userId, RequestType.POINT_CHARGE, dateTimeProvider.getLocalDateTimeNow(), pointChargeResult));

        return PointChargeResponse.from(pointChargeResult);
    }

    public PointSearchResponse searchPoint(Long userId) {
        UserSearchResult userResult = userService.getUserById(userId);

        PointSearchResult pointSearchResult = pointService.getPointByUserId(userId);

        return PointSearchResponse.from(pointSearchResult);
    }

}
