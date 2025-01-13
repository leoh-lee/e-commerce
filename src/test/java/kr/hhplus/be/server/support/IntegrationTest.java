package kr.hhplus.be.server.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@TestExecutionListeners(value = {
    DependencyInjectionTestExecutionListener.class,
    DynamicEntityCleanupListener.class
})
public abstract class IntegrationTest {
}
