package com.crawler.backend.model;

import com.crawler.backend.model.dto.TaskResponseDTO;

import java.util.HashSet;
import java.util.Set;

public class Task {
    private final String id;
    private final String keyword;
    private final Set<String> matchingUrls;
    private SearchStatus status;

    public Task(String id, String keyword) {
        this.matchingUrls = new HashSet<>();
        this.id = id;
        this.keyword = keyword;
        this.status = SearchStatus.ACTIVE;
    }

    public TaskResponseDTO toDTO() {
        return new TaskResponseDTO(
                id, status, matchingUrls
        );
    }




    public void addNewMatchingUrl(String url){
        this.matchingUrls.add(url);
    }

    public String getId(){
        return this.id;
    }
    public String getKeyword() {
        return keyword;
    }


    public void setStatus(SearchStatus status) {
        this.status = status;
    }

    public boolean isDone() {
        return this.status.equals(SearchStatus.DONE);
    }
}
