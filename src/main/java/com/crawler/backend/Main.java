package com.crawler.backend;


import com.crawler.backend.controller.CrawlerTaskController;

public class Main {
    public static void main(String[] args) {
        CrawlerTaskController crawlController = new CrawlerTaskController();
        crawlController.startSparkServer();
    }
}
