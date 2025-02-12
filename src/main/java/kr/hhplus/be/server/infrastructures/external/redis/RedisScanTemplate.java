package kr.hhplus.be.server.infrastructures.external.redis;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.nio.charset.StandardCharsets;

public abstract class RedisScanTemplate {

    private final RedisTemplate<String, Object> redisTemplate;

    protected RedisScanTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void scanAndProcess(String keyPattern) {
        RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
        if (connectionFactory == null) {
            return;
        }

        try (RedisConnection connection = connectionFactory.getConnection()) {
            ScanOptions scanOptions = ScanOptions.scanOptions().match(keyPattern).build();
            Cursor<byte[]> cursor = connection.keyCommands().scan(scanOptions);

            while (cursor.hasNext()) {
                byte[] next = cursor.next();
                String matchedKey = new String(next, StandardCharsets.UTF_8);
                processMatchedKey(matchedKey);
            }
        }
    }

    protected abstract void processMatchedKey(String key);

}
