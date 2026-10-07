package com.pitospies.pitospieswebservice.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public ConcurrentMapCacheManager cacheManager(){
        ConcurrentMapCacheManager cache = new ConcurrentMapCacheManager("cache");
        return cache;
    }

}
