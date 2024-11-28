package com.axreng.backend.model.dto;

import com.axreng.backend.model.SearchStatus;

import java.util.List;

public class SearchTaskResponseDTO {
    private String id;
    private String status;
    private List<String> urls;

    public SearchTaskResponseDTO(String id, SearchStatus status, List<String> urls) {
        this.id = id;
        this.status = status.getStatus();
        this.urls = urls;
    }

}
