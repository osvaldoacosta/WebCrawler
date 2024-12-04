package com.crawler.backend.repository;

import com.crawler.backend.model.Task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TaskManagerRepository implements DefaultRepository<Task, String>{
    // Variable simulating a db
    private static final ConcurrentMap<String, Task> tasks = new ConcurrentHashMap<>();


    @Override
    public void saveAll(Map<String,Task> elements) {
        tasks.putAll(elements);
    }


    @Override
    public void save(Task element) {
        tasks.put(element.getId(), element);

    }

    @Override
    public Task find(String id) {
        return tasks.get(id);
    }

    @Override
    public Map<String,Task> findAll() {
        return tasks;
    }

    @Override
    public void remove(String id) {
        tasks.remove(id);
    }

}
