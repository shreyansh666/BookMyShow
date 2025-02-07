package com.BookMyShow.demo.service.serviceImpl;

import com.BookMyShow.demo.entities.Asset;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisService {


    @Autowired
    private RedisTemplate<String, Asset<?>> redisTemplate;


    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) return null;
            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue(value, clazz);
        } catch (Exception e) {
            log.error("Error retrieving key {}: ", key, e);
            return null;
        }
    }

    public void set(String key, Asset<?> value, long ttl) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Error setting key {}: ", key, e);
        }
    }
}
