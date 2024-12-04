package com.crawler.backend.service;

import com.crawler.backend.model.Task;

public interface TaskManagerService {

    public Task createTask(String keyword);
    public Task getTask(String id);
    public void markTaskDone(Task task);
}
