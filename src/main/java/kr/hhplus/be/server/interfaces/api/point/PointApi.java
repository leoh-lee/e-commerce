package kr.hhplus.be.server.interfaces.api.point;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.interfaces.api.point.request.PointChargeRequest;
import kr.hhplus.be.server.interfaces.api.point.response.PointChargeResponse;
import kr.hhplus.be.server.interfaces.api.point.response.PointSearchResponse;
import kr.hhplus.be.server.support.http.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "point", description = "포인트 API")
public interface PointApi {

    @Operation(summary = "포인트를 충전한다", description = "포인트를 충전한다")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "포인트 충전에 성공했습니다."),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "최대 충전 가능 포인트를 초과했습니다.", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.", content = @Content)
    })
    @PostMapping
    ApiResponse<PointChargeResponse> chargePoint(@Valid @RequestBody PointChargeRequest pointChargeRequest);

    @Operation(summary = "사용자의 포인트를 조회한다", description = "사용자의 포인트를 조회한다")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용자 포인트 조회에 성공했습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.", content = @Content)
    })
    @GetMapping("/{userId}")
    ApiResponse<PointSearchResponse> searchPoint(@PathVariable Long userId);

}
