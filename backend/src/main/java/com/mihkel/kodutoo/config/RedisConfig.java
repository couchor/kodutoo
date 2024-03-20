package com.mihkel.kodutoo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableScheduling
@EnableRedisRepositories
public class RedisConfig {

  // Get values from application.properties
  @Value("${spring.redis.host}")
  private String host;
  @Value("${spring.redis.port}")
  private Integer port;

  private JedisConnectionFactory connectionFactory;

  // Reconnect to Redis if connection is lost. Check every 5 seconds
  @Scheduled(fixedDelay = 5000)
  public void reconnect() {
    try {
      connectionFactory.getConnection();
    } catch (RedisConnectionFailureException e) {
      connectionFactory = redisConnectionFactory();
    }
  }

  // Set up Redis connection
  @Bean
  public JedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
    connectionFactory = new JedisConnectionFactory(config);
    return connectionFactory;
  }

  // Set up Redis template
  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory());

    // Use GenericJackson2JsonRedisSerializer to serialize and deserialize the
    // values stored in Redis
    ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
        .visibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
        .build();

    GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

    redisTemplate.setValueSerializer(serializer);
    redisTemplate.setHashValueSerializer(serializer);
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setHashKeySerializer(new StringRedisSerializer());

    redisTemplate.afterPropertiesSet();

    return redisTemplate;
  }
}
