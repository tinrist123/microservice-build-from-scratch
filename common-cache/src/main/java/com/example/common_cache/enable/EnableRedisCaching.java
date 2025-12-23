package com.example.common_cache.enable;

import com.example.common_cache.configuration.RedisCacheAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RedisCacheAutoConfiguration.class)
public @interface EnableRedisCaching {
}