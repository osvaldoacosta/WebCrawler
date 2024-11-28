package com.axreng.backend.service;

import com.axreng.backend.model.SearchTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CrawlerServiceImpl implements CrawlerService {
    private final TaskManagerService taskManager;
    private final ExecutorService executor;
    private final String baseUrl;
    private final Logger logger;

    public CrawlerServiceImpl() {
        this.taskManager = new TaskManagerServiceImpl();
        this.executor = Executors.newCachedThreadPool();
        this.baseUrl = System.getenv("BASE_URL");
        this.logger = LoggerFactory.getLogger(CrawlerService.class);
    }

    @Override
    public String startCrawling(String keyword) {
        if (baseUrl == null || baseUrl.isEmpty()) {
            logger.error("No base URL provided.");
            return null;
        }

        String id = taskManager.createTask(keyword);
        SearchTask task = taskManager.getTask(id);

        executor.submit(() -> new CrawlerWorker(baseUrl, task, taskManager).start());
        return id;
    }

    @Override
    public SearchTask getCrawlResults(String id) {
        return taskManager.getTask(id);
    }

}
