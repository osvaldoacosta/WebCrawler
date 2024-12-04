package com.crawler.backend.service;

import com.crawler.backend.model.Task;

public interface CrawlerManagerService {
    String startCrawling(String keyword);
    Task getCrawlResults(String id);

}
