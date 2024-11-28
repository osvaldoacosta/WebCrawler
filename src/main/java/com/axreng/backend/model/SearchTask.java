package com.axreng.backend.model;

import com.axreng.backend.model.dto.SearchTaskResponseDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchTask {
    private String id;
    private String keyword;
    private Set<String> visitedUrls;
    private List<String> matchingUrls;
    private boolean done;

    public SearchTaskResponseDTO toDTO(){
        return new SearchTaskResponseDTO(
                id, done ? SearchStatus.DONE : SearchStatus.ACTIVE, matchingUrls
        );
    }


    public SearchTask(String id, String keyword) {
        this.visitedUrls = new HashSet<>();
        this.matchingUrls = new ArrayList<>();
        this.id = id;
        this.keyword = keyword;
    }


    // Getters and setters

    public String getKeyword() {
        return keyword;
    }


    public Set<String> getVisitedUrls() {
        return visitedUrls;
    }


    public List<String> getMatchingUrls() {
        return matchingUrls;
    }


    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
