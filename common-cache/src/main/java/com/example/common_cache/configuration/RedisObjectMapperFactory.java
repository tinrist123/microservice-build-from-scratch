package com.example.common_cache.configuration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

public final class RedisObjectMapperFactory {

    private RedisObjectMapperFactory() {
    }

    public static ObjectMapper create() {

        BasicPolymorphicTypeValidator ptv =
                BasicPolymorphicTypeValidator.builder()
                        // 🔐 STRICT allow-list — change to your base package
                        .allowIfSubType("com.example.")
                        .allowIfSubType("java.util.")
                        .build();

        ObjectMapper mapper = new ObjectMapper();

        mapper.activateDefaultTyping(
                ptv,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        return mapper;
    }
}
