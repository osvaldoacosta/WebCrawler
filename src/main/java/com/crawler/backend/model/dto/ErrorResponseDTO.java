package com.crawler.backend.model.dto;

public class ErrorResponseDTO {
    private final String error;

    public ErrorResponseDTO(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
