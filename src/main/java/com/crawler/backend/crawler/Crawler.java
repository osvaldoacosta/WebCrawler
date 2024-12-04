package com.crawler.backend.crawler;

import com.crawler.backend.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.*;

import static com.crawler.backend.crawler.CrawlerConfiguration.BASE_URL;
import static com.crawler.backend.crawler.CrawlerConfiguration.DEFAULT_THREAD_POOL_SIZE;

public class Crawler {
    private final Task task;
    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);;

    public Crawler(Task task) {
        this.task = task;
    }

    public void start() {
        logger.info("Crawler started");
        long startTime = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);
        BlockingQueue<String> urlQueue = new LinkedBlockingQueue<>();
        Set<String> processedUrls = ConcurrentHashMap.newKeySet();
        CountDownLatch latch = new CountDownLatch(DEFAULT_THREAD_POOL_SIZE);

        urlQueue.add(BASE_URL);

        for (int i = 0; i < DEFAULT_THREAD_POOL_SIZE; i++) {
            logger.debug("Crawler worker-{} started", i);
            executorService.submit(new CrawlerWorker(urlQueue, task, processedUrls, latch));
        }

        executorService.shutdown();

        try {
            latch.await(); // Wait for all workers to finish
        } catch (InterruptedException e) {
            logger.error("Crawler interrupted: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
        long endTime = System.currentTimeMillis();
        logger.info("CRAWLER EXECUTION TIME FOR KEYWORD {} TOOK {} ms", task.getKeyword().toUpperCase(), endTime - startTime);
    }
}