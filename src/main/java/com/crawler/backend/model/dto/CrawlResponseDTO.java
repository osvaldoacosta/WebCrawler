package com.crawler.backend.model.dto;

public class CrawlResponseDTO {
    private final String id;

    public CrawlResponseDTO(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
