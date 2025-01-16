package kr.hhplus.be.server.interfaces.api.point;

import jakarta.validation.Valid;
import kr.hhplus.be.server.application.point.PointFacade;
import kr.hhplus.be.server.interfaces.api.point.request.PointChargeRequest;
import kr.hhplus.be.server.interfaces.api.point.response.PointChargeResponse;
import kr.hhplus.be.server.interfaces.api.point.response.PointSearchResponse;
import kr.hhplus.be.server.support.http.response.ApiResponse;
import kr.hhplus.be.server.support.http.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/points")
@RequiredArgsConstructor
public class PointController implements PointApi {

    private final PointFacade pointFacade;

    @Override
    @PostMapping
    public ApiResponse<PointChargeResponse> chargePoint(@Valid @RequestBody PointChargeRequest pointChargeRequest) {
        return ApiResponse.ok(pointFacade.chargePoint(pointChargeRequest), ResponseCode.SUCCESS_CHARGE_POINT);
    }

    @Override
    @GetMapping("/{userId}")
    public ApiResponse<PointSearchResponse> searchPoint(@PathVariable Long userId) {
        return ApiResponse.ok(pointFacade.searchPoint(userId), ResponseCode.SUCCESS_SEARCH_USER_POINT);
    }

}
