package com.crawler.backend.model.dto;

import com.crawler.backend.model.SearchStatus;

import java.util.Set;

public class TaskResponseDTO {
    private String id;
    private String status;
    private Set<String> urls;

    public TaskResponseDTO(String id, SearchStatus status, Set<String> urls) {
        this.id = id;
        this.status = status.getStatus();
        this.urls = urls;
    }

}
