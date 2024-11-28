package com.axreng.backend.service;

import com.axreng.backend.model.SearchTask;

public interface CrawlerService {
    String startCrawling(String keyword);
    SearchTask getCrawlResults(String id);

}
