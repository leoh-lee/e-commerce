package kr.hhplus.be.server.config.aop;

import kr.hhplus.be.server.config.annotation.DistributedLock;
import kr.hhplus.be.server.support.util.CustomSpringELParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DistributedLockAspect {

    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;

    @Around("@annotation(kr.hhplus.be.server.config.annotation.DistributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String key = REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());

        RLock rLock = redissonClient.getLock(key);
        try {
            log.info("{}#{} | Try to get DistributedLock. Key >>> {}", method.getDeclaringClass().getSimpleName(), method.getName(), key);
            boolean available = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
            if (!available) {
                log.info("{}#{} | Fail to get DistributedLock. Key >>> {}", method.getDeclaringClass().getSimpleName(), method.getName(), key);
                return false;
            }

            log.info("{}#{} | Success to get DistributedLock. Key >>> {}", method.getDeclaringClass().getSimpleName(), method.getName(), key);
            return joinPoint.proceed();
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } finally {
            try {
                rLock.unlock();
                log.info("DistributedLock is unlocked. Key is {}", key);
            } catch (IllegalMonitorStateException e) {
                log.info("Redisson Lock Already UnLock serviceName: {} key: {}", method.getDeclaringClass().getSimpleName(), key);
            }
        }
    }
}
