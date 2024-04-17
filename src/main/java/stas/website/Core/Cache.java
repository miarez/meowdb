package stas.website.Core;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class Cache {
    
    private LoadingCache<String, String> cache;

    public Cache() {
        // Initialize the cache
        cache = CacheBuilder.newBuilder()
        .maximumSize(100)                  // maximum 100 records can be cached
        .expireAfterAccess(30, TimeUnit.MINUTES)  // cache will expire after 30 minutes of access
        .build(new CacheLoader<String, String>() {
            @Override
            public String load(String key) throws Exception {
                System.out.println("Fetching JSON data from file: " + key);
                return new String(Files.readAllBytes(Paths.get(key)));
            }
        });
    }

    public String getJson(String filePath) {
        try {
            String jsonData = cache.get(filePath);
            System.out.println("Fetching JSON data from cache");
            return jsonData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
