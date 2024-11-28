package com.axreng.backend.model;

public enum SearchStatus {
    ACTIVE("active"),
    DONE("done");

    private String status;

    SearchStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
