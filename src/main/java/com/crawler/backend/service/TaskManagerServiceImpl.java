package com.crawler.backend.service;

import com.crawler.backend.model.SearchStatus;
import com.crawler.backend.model.Task;
import com.crawler.backend.repository.TaskManagerRepository;
import com.crawler.backend.utils.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskManagerServiceImpl implements TaskManagerService {
    private static final Logger logger =LoggerFactory.getLogger(TaskManagerServiceImpl.class);
    private final TaskManagerRepository repository;

    public TaskManagerServiceImpl() {
        this.repository = new TaskManagerRepository();
    }

    @Override
    public Task createTask(String keyword) {
        String id = IdGenerator.generateId();
        Task task = new Task(id,keyword);
        repository.save(task);
        logger.info("Task created with ID: {} for keyword: {}", id, keyword);
        return task;
    }

    @Override
    public Task getTask(String id) {
        return repository.find(id);
    }

    @Override
    public void markTaskDone(Task task) {
        logger.info("Task for keyword '{}' completed.", task.getKeyword());
        task.setStatus(SearchStatus.DONE);
        repository.save(task);
    }

}
