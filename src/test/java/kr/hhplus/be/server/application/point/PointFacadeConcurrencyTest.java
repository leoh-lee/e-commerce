package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.user.UserCreateDto;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.dto.UserCreateResult;
import kr.hhplus.be.server.interfaces.api.point.request.PointChargeRequest;
import kr.hhplus.be.server.interfaces.api.point.response.PointSearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class PointFacadeConcurrencyTest {

    @Autowired
    private PointFacade pointFacade;

    @Autowired
    private UserService userService;

    @Autowired
    private PointService pointService;

    private Long userId;

    @BeforeEach
    void setUp() {
        UserCreateResult userCreateResult = userService.createUser(new UserCreateDto("testUser"));
        userId = userCreateResult.userId();
        pointService.createDefaultPoint(userId);
    }

    @Test
    void chargePoint_concurrentRequests_shouldMaintainDataIntegrity() throws InterruptedException {
        int numberOfThreads = 5;
        int chargeAmount = 50;

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        CyclicBarrier barrier = new CyclicBarrier(numberOfThreads);
        List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    barrier.await(); // 동시 시작
                    PointChargeRequest request = new PointChargeRequest(userId, chargeAmount);
                    pointFacade.chargePoint(request);
                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();
        if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }

        // 검증
        assertThat(exceptions).isEmpty();
        PointSearchResponse response = pointFacade.searchPoint(userId);
        assertThat(response.balance()).isEqualTo(250);

    }

}

