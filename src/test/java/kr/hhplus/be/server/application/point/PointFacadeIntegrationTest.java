package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.point.Point;

import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.point.PointTransactionType;
import kr.hhplus.be.server.domain.point.dto.PointHistorySearchResult;
import kr.hhplus.be.server.domain.point.dto.PointSearchResult;
import kr.hhplus.be.server.domain.point.exception.PointLimitExceededException;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.exception.UserNotFoundException;
import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatform;
import kr.hhplus.be.server.interfaces.api.point.request.PointChargeRequest;
import kr.hhplus.be.server.interfaces.api.point.response.PointSearchResponse;
import kr.hhplus.be.server.support.IntegrationTest;
import kr.hhplus.be.server.support.TestDataBuilder;
import kr.hhplus.be.server.support.TestDataPlatform;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class PointFacadeIntegrationTest extends IntegrationTest {

    @Autowired
    private PointFacade pointFacade;

    @Autowired
    private PointService pointService;

    @Autowired
    private DataPlatform dataPlatform;

    @Autowired
    private TestDataBuilder testDataBuilder;

    @Nested
    @DisplayName("포인트 충전")
    class ChartPoint {

        @Test
        @DisplayName("포인트 충전 시 사용자가 존재하지 않으면 실패한다.")
        void chargePoint_whenUserNotExists_throwsUserNotFoundException() {
            // given
            long nonExistsId = 1L;
            PointChargeRequest pointChargeRequest = new PointChargeRequest(nonExistsId, BigDecimal.valueOf(10_000));
            // when
            // then
            assertThatThrownBy(() -> pointFacade.chargePoint(pointChargeRequest))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @ParameterizedTest
        @CsvSource({"1990000, 11000", "0, 2000100", "1000000, 2000000"})
        @DisplayName("포인트 충전 시 충전 후 금액이 2,000,000원이 넘는 경우 실패한다.")
        void chargePoint_whenExceedPointLimit_throwsPointLimitExceededException(int balance, int amount) {
            // given
            User user = testDataBuilder.createUser("test1");
            Point point = testDataBuilder.createPoint(user.getId(), BigDecimal.valueOf(balance));
            PointChargeRequest pointChargeRequest = new PointChargeRequest(user.getId(), BigDecimal.valueOf(amount));

            // when
            // then
            assertThatThrownBy(() -> pointFacade.chargePoint(pointChargeRequest))
                    .isInstanceOf(PointLimitExceededException.class);
        }

        @Test
        @DisplayName("포인트 충전 시 충전 내역이 저장된다.")
        void chargePoint_whenSuccess_thenPointHistorySaved() {
            // given
            User user = testDataBuilder.createUser("test1");
            Point point = testDataBuilder.createPoint(user.getId(), BigDecimal.valueOf(10_000));

            PointChargeRequest pointChargeRequest = new PointChargeRequest(user.getId(), BigDecimal.valueOf(20_000));

            // when
            pointFacade.chargePoint(pointChargeRequest);

            // then
            Page<PointHistorySearchResult> pointHistories = pointService.getPointHistoriesByUserId(user.getId(), PageRequest.of(0, 10));
            PointHistorySearchResult pointHistory = pointHistories.getContent().getFirst();

            assertThat(pointHistory).extracting("pointId", "transactionType", "amount")
                    .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                    .containsExactly(point.getId(), PointTransactionType.CHARGE, BigDecimal.valueOf(20_000));
        }

        @ParameterizedTest
        @CsvSource({"10000, 20000, 30000", "500000, 10000, 510000", "700000, 100000, 800000", "1900000, 100000"})
        @DisplayName("포인트 충전 시 정상적으로 포인트 잔액이 증가한다.")
        void chargePoint_whenSuccess_thenPointIncreased() {
            // given
            User user = testDataBuilder.createUser("test1");
            Point point = testDataBuilder.createPoint(user.getId(), BigDecimal.valueOf(10_000));

            PointChargeRequest pointChargeRequest = new PointChargeRequest(user.getId(), BigDecimal.valueOf(20_000));

            // when
            pointFacade.chargePoint(pointChargeRequest);

            // then
            PointSearchResult pointSearchResult = pointService.getPointByUserId(user.getId());

            assertThat(pointSearchResult).extracting("id", "userId", "balance")
                    .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                    .containsExactly(point.getId(), user.getId(), BigDecimal.valueOf(30_000));
        }

        @Test
        @DisplayName("포인트 충전 시 데이터 플랫폼에 이력을 전송한다.")
        void chargePoint_whenSuccess_thenSendDataPlatform() {
            // given
            User user = testDataBuilder.createUser("test");
            pointService.createDefaultPoint(user.getId());

            PointChargeRequest pointChargeRequest = new PointChargeRequest(user.getId(), BigDecimal.valueOf(10_000));

            TestDataPlatform testDataPlatform = (TestDataPlatform) dataPlatform;
            Integer platformSentCount = testDataPlatform.getSentCount();

            // when
            pointFacade.chargePoint(pointChargeRequest);

            // then
            assertThat(testDataPlatform.getSentCount()).isEqualTo(platformSentCount + 1);
        }
    }

    @Nested
    @DisplayName("포인트 조회")
    class SearchPoint {
        @Test
        @DisplayName("포인트 조회 시 사용자가 존재하지 않으면 실패한다.")
        void searchPoint_whenUserNotExists_throwsUserNotFoundException() {
            // given
            Long nonExistsUserId = 99999L;

            // when
            // then
            assertThatThrownBy(() -> pointFacade.searchPoint(nonExistsUserId))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("포인트 조회에 성공한다.")
        void searchPoint_success() {
            // given
            User user = testDataBuilder.createUser("test");
            testDataBuilder.createPoint(user.getId(), BigDecimal.valueOf(10_000));

            // when
            PointSearchResponse pointSearchResponse = pointFacade.searchPoint(user.getId());

            // then
            assertThat(pointSearchResponse.userId()).isEqualTo(user.getId());
            assertThat(pointSearchResponse.balance()).isEqualByComparingTo(BigDecimal.valueOf(10_000));
        }
    }

}
