package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.user.UserCreateDto;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.dto.UserCreateResult;
import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatform;
import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatformSendRequest;
import kr.hhplus.be.server.interfaces.api.point.request.PointChargeRequest;
import kr.hhplus.be.server.interfaces.api.point.response.PointChargeResponse;
import kr.hhplus.be.server.interfaces.api.point.response.PointSearchResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
class PointFacadeIntegrationTest {

    @Autowired
    private PointFacade pointFacade;

    @Autowired
    private UserService userService;

    @Autowired
    private PointService pointService;

    @MockitoBean
    private DataPlatform dataPlatform;

    @Test
    void chargePoint_success() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(50);

        UserCreateDto testUser = new UserCreateDto("testUser");
        UserCreateResult userCreateResult = userService.createUser(testUser);

        Long userId = userCreateResult.userId();
        pointService.createDefaultPoint(userId);

        PointChargeRequest request = new PointChargeRequest(userId, amount);

        // When
        PointChargeResponse response = pointFacade.chargePoint(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.amount()).isEqualByComparingTo(BigDecimal.valueOf(50));

        // 데이터 플랫폼 호출 검증
        verify(dataPlatform, times(1)).send(any(DataPlatformSendRequest.class));
    }

    @Test
    void searchPoint_success() {
        // Given
        UserCreateDto userCreateDto = new UserCreateDto("testUser");
        UserCreateResult userCreateResult = userService.createUser(userCreateDto);
        Long userId = userCreateResult.userId();
        pointService.createDefaultPoint(userId);

        // When
        PointSearchResponse response = pointFacade.searchPoint(userId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.balance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

}
