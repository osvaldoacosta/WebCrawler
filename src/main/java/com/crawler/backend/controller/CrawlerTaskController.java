package com.crawler.backend.controller;

import com.crawler.backend.model.Task;
import com.crawler.backend.model.dto.CrawlRequestDTO;
import com.crawler.backend.model.dto.CrawlResponseDTO;
import com.crawler.backend.model.dto.ErrorResponseDTO;
import com.crawler.backend.service.CrawlerManagerService;
import com.crawler.backend.service.CrawlerManagerServiceImpl;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;


import static com.crawler.backend.crawler.CrawlerConfiguration.*;
import static com.crawler.backend.crawler.CrawlerConfiguration.BASE_URL;
import static spark.Spark.*;

public class CrawlerTaskController {
    private final CrawlerManagerService crawlService;
    private final Gson gson;
    private static final Logger logger = LoggerFactory.getLogger(CrawlerTaskController.class);
    public CrawlerTaskController() {
        this.crawlService = new CrawlerManagerServiceImpl();
        this.gson = new Gson();
    }

    public void startSparkServer() {
        post("/crawl", this::startCrawl);
        get("/crawl/:id", this::getCrawlResults);
        exception(Exception.class, (e, req, res) -> {
            logger.error("Unhandled exception occurred: {}", e.getMessage());
            res.status(500);
            res.type("application/json");
            res.body(gson.toJson(new ErrorResponseDTO("Internal server error")));
        });
    }

    private String startCrawl(Request req, Response res) {
        CrawlRequestDTO request = gson.fromJson(req.body(), CrawlRequestDTO.class);
        String keyword = request.getKeyword();

        // Constraints
        if (keyword == null || keyword.length() < MIN_KEYWORD_LENGTH || keyword.length() > MAX_KEYWORD_LENGTH) {
            res.status(400);
            return gson.toJson(new ErrorResponseDTO("Invalid keyword length"));
        }

        if (BASE_URL == null || BASE_URL.isEmpty()) {
            res.status(500);
            logger.error("No base URL provided.");
            return gson.toJson(new ErrorResponseDTO("Internal server error"));
        }

        String id = crawlService.startCrawling(keyword);
        res.status(200);
        res.type("application/json");
        return gson.toJson(new CrawlResponseDTO(id));
    }

    private String getCrawlResults(Request req, Response res) {
        String id = req.params(":id");
        Task result = crawlService.getCrawlResults(id);

        if (result == null) {
            res.status(404);
            return gson.toJson(new ErrorResponseDTO("Task not found"));
        }




        res.status(200);
        res.type("application/json");
        return gson.toJson(result.toDTO());
    }
}
