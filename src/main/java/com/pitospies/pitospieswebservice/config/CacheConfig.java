package com.pitospies.pitospieswebservice.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public ConcurrentMapCacheManager cacheManager(){ //h ConcurrentMapCacheManager einai kalh gia xrhsh multi-threaded
        //ftiaxnei mia cache mnhmh
        return new ConcurrentMapCacheManager("my-cache");
    }

}
