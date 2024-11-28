package com.axreng.backend.controller;

import com.axreng.backend.model.SearchTask;
import com.axreng.backend.model.dto.CrawlRequestDTO;
import com.axreng.backend.model.dto.CrawlResponseDTO;
import com.axreng.backend.service.CrawlerService;
import com.axreng.backend.model.dto.ErrorResponseDTO;
import com.axreng.backend.service.CrawlerServiceImpl;
import com.axreng.backend.service.TaskManagerServiceImpl;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;


import static spark.Spark.*;

public class SearchTaskController {
    private final CrawlerService crawlService;
    private final Gson gson;
    private final Logger logger;
    public SearchTaskController() {
        this.crawlService = new CrawlerServiceImpl();
        this.gson = new Gson();
        this.logger =  LoggerFactory.getLogger(SearchTaskController.class);
    }

    public void startSparkServer() {
        // I dont need to set the port since the docker file is doing this for me
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
        if (keyword == null || keyword.length() < 4 || keyword.length() > 32) {
            res.status(400);
            return gson.toJson(new ErrorResponseDTO("Invalid keyword length"));
        }

        String id = crawlService.startCrawling(keyword);
        if(id == null){
            res.status(500);
            return gson.toJson(new ErrorResponseDTO("Internal server error"));
        }
        res.status(200);
        res.type("application/json");
        return gson.toJson(new CrawlResponseDTO(id));
    }

    private String getCrawlResults(Request req, Response res) {
        String id = req.params(":id");
        SearchTask result = crawlService.getCrawlResults(id);

        if (result == null) {
            res.status(404);
            return gson.toJson(new ErrorResponseDTO("Task not found"));
        }




        res.status(200);
        res.type("application/json");
        return gson.toJson(result.toDTO());
    }
}
