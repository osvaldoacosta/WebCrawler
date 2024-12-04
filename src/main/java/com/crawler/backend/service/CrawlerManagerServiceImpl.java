package com.crawler.backend.service;

import com.crawler.backend.crawler.Crawler;
import com.crawler.backend.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CrawlerManagerServiceImpl implements CrawlerManagerService {
    private final TaskManagerService taskManager;
    private final ExecutorService executor;
    private final Logger logger = LoggerFactory.getLogger(CrawlerManagerServiceImpl.class);

    public CrawlerManagerServiceImpl() {
        this.taskManager = new TaskManagerServiceImpl();
        this.executor = Executors.newCachedThreadPool();
    }

    @Override
    public String startCrawling(String keyword) {
        Task task = taskManager.createTask(keyword);
        executor.submit(() -> {
            try {
                new Crawler(task).start();
            }
            finally {
                taskManager.markTaskDone(task);
            }

        });

        return task.getId();
    }

    @Override
    public Task getCrawlResults(String id) {
        return taskManager.getTask(id);
    }

}
