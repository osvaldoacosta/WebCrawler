package com.axreng.backend.service;

import com.axreng.backend.model.SearchTask;

public interface TaskManagerService {

    public String createTask(String keyword);
    public SearchTask getTask(String id);
    public void markTaskDone(SearchTask task);
    public void addVisited(SearchTask task, String url);
    public void addMatchingUrl(SearchTask task, String url);
}
