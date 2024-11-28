package com.axreng.backend.service;

import com.axreng.backend.model.SearchTask;
import com.axreng.backend.utils.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TaskManagerServiceImpl implements TaskManagerService {
    private final ConcurrentMap<String, SearchTask> tasks;
    private final Logger logger;
    public TaskManagerServiceImpl() {
        this.tasks = new ConcurrentHashMap<>();
        this.logger =  LoggerFactory.getLogger(TaskManagerServiceImpl.class);
    }

    @Override
    public String createTask(String keyword) {
        String id = IdGenerator.generateId();
        SearchTask task = new SearchTask(id, keyword);
        tasks.put(id, task);
        logger.info("Task created with ID: {} for keyword: {}", id, keyword);
        return id;
    }

    @Override
    public SearchTask getTask(String id) {
        return tasks.get(id);
    }

    @Override
    public void markTaskDone(SearchTask task) {
        logger.info("Task for keyword '{}' completed.", task.getKeyword());
        task.setDone(true);
    }

    @Override
    public void addVisited(SearchTask task, String url) {
        task.getVisitedUrls().add(url);
    }

    @Override
    public void addMatchingUrl(SearchTask task, String url) {
        task.getMatchingUrls().add(url);
    }
}
