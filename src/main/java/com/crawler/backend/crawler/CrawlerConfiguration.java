package com.crawler.backend.crawler;

public final class CrawlerConfiguration {
    public static final String BASE_URL = System.getenv("BASE_URL");
    public static final int MIN_KEYWORD_LENGTH = 4;
    public static final int MAX_KEYWORD_LENGTH = 32;

    public static final int DEFAULT_THREAD_POOL_SIZE = 15;
    public static final int QUEUE_POLL_TIMEOUT_SEC = 1;

    public static final int CONNECTION_TIMEOUT_MS = 5000;
    public static final int READ_TIMEOUT_MS = 5000;

    public static final boolean CACHE_ENABLED = false;
    public static final int MEMORY_CACHE_CONTENT_SIZE = 500;
    public static final long CACHE_TIMEOUT_MS = 60 * 60 * 1000; // 1 hour
}
