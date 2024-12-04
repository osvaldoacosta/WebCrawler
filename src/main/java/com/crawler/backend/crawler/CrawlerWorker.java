package com.crawler.backend.crawler;

import com.crawler.backend.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.*;

import static com.crawler.backend.crawler.CrawlerConfiguration.*;

//TODO: MAYBE OPTIMIZE THE PROCESSED URLS
public class CrawlerWorker implements Runnable {
    private final BlockingQueue<String> urlQueue;
    private final Set<String> processedUrls;
    private final Task task;
    private final PageProcessor pageProcessor;
    private final CountDownLatch latch;
    private final PageCacheSingleton cache;

    private static final Logger logger = LoggerFactory.getLogger(CrawlerWorker.class);

    public CrawlerWorker(BlockingQueue<String> urlQueue, Task task, Set<String> processedUrls, CountDownLatch latch) {
        this.urlQueue = urlQueue;
        this.task = task;
        this.processedUrls = processedUrls;
        this.pageProcessor = new PageProcessor();
        this.latch = latch;
        this.cache = PageCacheSingleton.getInstance();
    }


    @Override
    public void run() {
        logger.info("New crawler worker created!");
        try {
            while (!Thread.currentThread().isInterrupted()) {
                String url = urlQueue.poll(QUEUE_POLL_TIMEOUT_SEC, TimeUnit.SECONDS);
                if (url == null) {
                    logger.debug("No URLs in queue. Worker terminating.");
                    break;
                }

                if (!processedUrls.add(url)) {
                    logger.debug("URL '{}' already processed. Skipping.", url);
                    continue;
                }

                processUrl(url);
            }
        } catch (InterruptedException e) {
            logger.warn("Worker interrupted.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error("Error in worker: {}", e.getMessage());
        } finally {
            latch.countDown(); // Signal this worker is done
        }

    }
    private void processUrl(String url) {
        //logger.info("Processing URL: {}", url);
        try {
            String content;
            if(CACHE_ENABLED){
                content = cache.getPageContent(url, pageProcessor); //with cache
            }
            else{
                content = pageProcessor.fetchPageContent(url); //without cache
            }
            // Check for keyword
            if (content.toLowerCase().contains(task.getKeyword().toLowerCase())) {
                logger.info("Keyword '{}' found at URL: {}", task.getKeyword(), url);
                task.addNewMatchingUrl(url);
            }

            // Extract and queue new links
            for (String link : pageProcessor.extractLinks(content, url)) {
                if (link.startsWith(BASE_URL) && !processedUrls.contains(link)) {
                    urlQueue.put(link); // Will block if queue is full
                }
            }
        } catch (IOException e) {
            logger.error("Error processing URL '{}': {}", url, e.getMessage());
        } catch (InterruptedException e) {
            logger.warn("Worker interrupted while queuing new URL.");
            Thread.currentThread().interrupt();
        }
    }
}