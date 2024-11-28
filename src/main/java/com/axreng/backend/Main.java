package com.axreng.backend;


import com.axreng.backend.controller.SearchTaskController;

public class Main {
    public static void main(String[] args) {
        SearchTaskController crawlController = new SearchTaskController();
        crawlController.startSparkServer();
    }
}
