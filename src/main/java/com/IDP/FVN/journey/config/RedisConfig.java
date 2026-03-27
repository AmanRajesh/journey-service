package com.IDP.FVN.journey.config;

import com.IDP.FVN.journey.model.ActiveJourney;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    // ADD THIS BEAN HERE
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Helps with Date/Time fields
        return mapper;
    }

    @Bean
    public ReactiveRedisTemplate<String, ActiveJourney> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory factory,
            ObjectMapper objectMapper) {

        Jackson2JsonRedisSerializer<ActiveJourney> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, ActiveJourney.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, ActiveJourney> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, ActiveJourney> context = builder.value(serializer).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}