package com.example.common_cache.configuration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@AutoConfiguration
@ConditionalOnClass(RedisTemplate.class)
@ConditionalOnProperty(prefix = "cache.redis", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(RedisProperties.class)
@EnableCaching
public class RedisCacheAutoConfiguration {

        @Bean
        public RedisTemplate<String, Object> redisTemplate(
                        RedisConnectionFactory connectionFactory) {

                RedisSerializer<String> keySerializer = new StringRedisSerializer();
                RedisSerializer<Object> valueSerializer = RedisSerializer.json();

                RedisTemplate<String, Object> template = new RedisTemplate<>();
                template.setConnectionFactory(connectionFactory);

                template.setKeySerializer(keySerializer);
                template.setValueSerializer(valueSerializer);
                template.setHashKeySerializer(keySerializer);
                template.setHashValueSerializer(valueSerializer);

                template.afterPropertiesSet();
                return template;
        }

        @Bean
        public CacheManager cacheManager(
                        RedisConnectionFactory connectionFactory, RedisProperties properties) {

                RedisSerializer<Object> valueSerializer = RedisSerializer.json();

                RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                                .serializeValuesWith(
                                                RedisSerializationContext.SerializationPair
                                                                .fromSerializer(valueSerializer))
                                .entryTtl(properties.getDefaultTtl())
                                .disableCachingNullValues();

                return RedisCacheManager.builder(connectionFactory)
                                .cacheDefaults(config)
                                .build();
        }
}
