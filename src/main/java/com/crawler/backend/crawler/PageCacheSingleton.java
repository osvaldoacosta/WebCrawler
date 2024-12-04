package com.crawler.backend.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

import static com.crawler.backend.crawler.CrawlerConfiguration.MEMORY_CACHE_CONTENT_SIZE;
import static com.crawler.backend.crawler.CrawlerConfiguration.CACHE_TIMEOUT_MS;

public class PageCacheSingleton {
    private static final PageCacheSingleton uniqueInstance = new PageCacheSingleton();
    private static final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>(MEMORY_CACHE_CONTENT_SIZE);

    private static final Logger logger = LoggerFactory.getLogger(PageCacheSingleton.class);
    private static final Path DISK_CACHE_DIR = Paths.get(".cache");
    private long lastCacheResetTimestamp;

    static {
        try {
            Files.createDirectories(DISK_CACHE_DIR); // Ensure directory exists
        } catch (IOException e) {
            logger.error("Failed to create disk cache directory: {}", e.getMessage());
            throw new RuntimeException("Could not initialize disk cache", e);
        }
    }

    public PageCacheSingleton() {
        this.lastCacheResetTimestamp = System.currentTimeMillis();
    }

    public static PageCacheSingleton getInstance() {
        return uniqueInstance;
    }

    public String getPageContent(String url, PageProcessor pageProcessor) throws IOException {
        clearCacheIfTimedOut();

        // Check if the page is already cached in memory
        if (cache.containsKey(url)) {
            CacheEntry entry = cache.get(url);
            logger.debug("Cache hit in memory for URL: {}", url);
            return entry.getContent();
        }

        // Check if the page is cached on disk
        Path diskCachePath = getDiskCachePath(url);
        if (Files.exists(diskCachePath)) {
            logger.debug("Cache hit on disk for URL: {}", url);
            String content = Files.readString(diskCachePath);
            addToCache(url, content); // Bring it back to memory cache
            return content;
        }

        // Fetch new content
        logger.debug("Cache miss for URL: {}", url);
        String content = pageProcessor.fetchPageContent(url);

        // Add to cache (memory or disk as necessary)
        addToCache(url, content);

        return content;
    }

    private void addToCache(String url, String content) {
        clearCacheIfTimedOut();

        if (cache.size() >= MEMORY_CACHE_CONTENT_SIZE) {
            logger.debug("Memory cache full, evicting to disk...");
            String evictedUrl = cache.keySet().iterator().next(); // Evict the first entry (simple policy)
            CacheEntry evictedEntry = cache.remove(evictedUrl); // Remove from the cache

            if (evictedEntry != null) {
                writeToDisk(evictedUrl, evictedEntry.getContent());
            } else {
                logger.warn("Evicted URL {} not found in cache during eviction", evictedUrl);
            }
        }

        cache.put(url, new CacheEntry(content));
    }

    private void writeToDisk(String url, String content) {
        try {
            Path filePath = getDiskCachePath(url);
            Files.writeString(filePath, content);
            logger.debug("Cached URL {} to disk", url);
        } catch (IOException e) {
            logger.error("Failed to write cache to disk for URL {}: {}", url, e.getMessage());
        }
    }

    private Path getDiskCachePath(String url) {
        // Generate a filename-safe hash for the URL
        String fileName = Integer.toHexString(url.hashCode());
        return DISK_CACHE_DIR.resolve(fileName);
    }

    private void clearCacheIfTimedOut() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCacheResetTimestamp > CACHE_TIMEOUT_MS) {
            logger.info("Cache timeout exceeded. Clearing memory cache...");
            cache.clear();
            clearDiskCache();
            lastCacheResetTimestamp = currentTime;
        }
    }
    private void clearDiskCache() {
        try {
            Files.list(DISK_CACHE_DIR).forEach(filePath -> {
                try {
                    Files.delete(filePath);
                    logger.info("Deleted cached file: {}", filePath);
                } catch (IOException e) {
                    logger.error("Failed to delete cached file: {}", filePath, e);
                }
            });
        } catch (IOException e) {
            logger.error("Failed to clear disk cache: {}", e.getMessage());
        }
    }

    private static class CacheEntry {
        private final String content;

        public CacheEntry(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }
}
