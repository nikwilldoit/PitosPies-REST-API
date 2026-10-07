package com.pitospies.pitospieswebservice.webservices;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

@RestController
@RequestMapping(path="/api", produces="application/json")
@Validated
public class CacheRestController {

    @Autowired
    private CacheManager cacheManager; //bean CacheManager

    @GetMapping("/cache-data/{name}") //sto {name} vazw thn cache pou thelw na dw ta periexomena ths
    public ResponseEntity<ConcurrentMap<Object, Object>> handleRequest(@Valid @PathVariable String name) {
        Cache cache = cacheManager.getCache(name);
        if (cache instanceof ConcurrentMapCache) {
            ConcurrentMap<Object, Object> nativeCache = ((ConcurrentMapCache) cache).getNativeCache();
            for (Map.Entry<Object, Object> entry : nativeCache.entrySet()) {
                System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
            }
            return new ResponseEntity<>(nativeCache, HttpStatus.OK);
        }
        return null;
    }
}