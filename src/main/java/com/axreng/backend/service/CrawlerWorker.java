package com.axreng.backend.service;

import com.axreng.backend.model.SearchTask;
import com.axreng.backend.utils.ExecutionTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrawlerWorker {
    private final String baseUrl;
    private final SearchTask task;
    private final TaskManagerService taskManagerService;
    private final Logger logger;
    private final ExecutorService executor;
    private final ConcurrentHashMap<String, String> contentCache; // Cache for page content
    private final BlockingQueue<String> urlQueue;
    public CrawlerWorker(String baseUrl, SearchTask task, TaskManagerService taskManagerService) {
        this.baseUrl = baseUrl;
        this.task = task;
        this.taskManagerService = taskManagerService;
        this.logger =  LoggerFactory.getLogger(CrawlerWorker.class);
        this.executor = Executors.newFixedThreadPool(10);
        this.contentCache = new ConcurrentHashMap<>();
        this.urlQueue = new LinkedBlockingQueue<>();
    }

    public void start() {
        ExecutionTime executionTime = new ExecutionTime();
        logger.info("STARTING CRAWLER FOR THE KEYWORD {} IN THE URL {}", task.getKeyword().toUpperCase(), baseUrl.toUpperCase());
        crawl(baseUrl);
        logger.info("CRAWLER EXECUTION TIME FOR KEYWORD {} TOOK {}", task.getKeyword().toUpperCase(), executionTime.getTimeDurationSecAndMs());

        taskManagerService.markTaskDone(task);
    }

    private void crawl(String url) {
        if (task.getVisitedUrls().contains(url) || task.isDone()) {
            return;
        }

        taskManagerService.addVisited(task, url);
        try {
            String content = fetchCachedPageContent(url);
            if (content.toLowerCase().contains(task.getKeyword())) {
                logger.info("Keyword {} found at url: {}", task.getKeyword(), url);
                taskManagerService.addMatchingUrl(task, url);
            }

            for (String link : extractLinks(content, url)) {
                if (link.startsWith(baseUrl)) {
                    crawl(link); // Recursive crawl for matching base URLs
                }
            }
        } catch (Exception e) {
            logger.error("Error crawling {}", url, e);
        }

    }
    private String fetchCachedPageContent(String urlStr) throws Exception {
        // Check if the content is already cached
        if (contentCache.containsKey(urlStr)) {
            logger.debug("Cache hit for URL: {}", urlStr);
            return contentCache.get(urlStr);
        }

        logger.debug("Cache miss for URL: {}", urlStr);
        String content = fetchPageContent(urlStr);
        contentCache.put(urlStr, content);
        return content;
    }

    private String fetchPageContent(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (Scanner scanner = new Scanner(connection.getInputStream())) {
            StringBuilder content = new StringBuilder();
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine()).append("\n");
            }
            return content.toString();
        }
    }

    private List<String> extractLinks(String content, String baseUrl) {
        List<String> links = new ArrayList<>();
        String regex = "href\\s*=\\s*\"([^\"]+)\"";
        Matcher matcher = Pattern.compile(regex).matcher(content);

        try {
            URL base = new URL(baseUrl);

            while (matcher.find()) {
                String link = matcher.group(1);

                URL resolvedUrl = new URL(base, link);
                links.add(resolvedUrl.toString());
            }
        } catch (MalformedURLException e) {
            logger.error("MALFORMED BASE URL: {}", e.getMessage());
        }

        return links;
    }
}

