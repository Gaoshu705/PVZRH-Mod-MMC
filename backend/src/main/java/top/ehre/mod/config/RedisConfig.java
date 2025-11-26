package top.ehre.mod.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer()); // 所有的键序列化为字符串形式存储在 Redis 中
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer); // 所有的值序列化为 JSON 格式存储在 Redis 中
        redisTemplate.setHashKeySerializer(jackson2JsonRedisSerializer); // 哈希中的键序列化为 JSON 格式存储在 Redis 中
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer); // 哈希中的值序列化为 JSON 格式存储在 Redis 中
        return redisTemplate;
    }
}
