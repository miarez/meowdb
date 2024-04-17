package stas.website.unit;


import org.junit.Test;

import stas.website.Core.Cache;

// This class is used to test the JsonCache class functionality
public class CacheTest {
  
    @Test
    public void test() {
        Cache jsonCache = new Cache();
        
        // Simulate first request
        String filePath = "query/_test_0.json";
        System.out.println("First request: " + jsonCache.getJson(filePath));

        // Simulate second request (should load from cache)
        System.out.println("Second request: " + jsonCache.getJson(filePath));
    }
}
