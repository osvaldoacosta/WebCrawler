package com.crawler.backend.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.crawler.backend.crawler.CrawlerConfiguration.CONNECTION_TIMEOUT_MS;
import static com.crawler.backend.crawler.CrawlerConfiguration.READ_TIMEOUT_MS;

public class PageProcessor {
    private static final Logger logger = LoggerFactory.getLogger(PageProcessor.class);;
    private static final Pattern HREF_PATTERN = Pattern.compile("href\\s*=\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);


    public String fetchPageContent(String urlStr) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(CONNECTION_TIMEOUT_MS); // Timeout for connection
            connection.setReadTimeout(READ_TIMEOUT_MS); // Timeout for reading data

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP request failed with status: " + responseCode);
            }

            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }

            return content.toString();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public List<String> extractLinks(String content, String baseUrl) {
        List<String> links = new ArrayList<>();
        Matcher matcher = HREF_PATTERN.matcher(content);

        try {
            URL base = new URL(baseUrl);

            while (matcher.find()) {
                String link = matcher.group(1);
                URL resolvedUrl = new URL(base, link);
                links.add(resolvedUrl.toString());
            }
        } catch (MalformedURLException e) {
            logger.error("MALFORMED URL GIVEN: {}", e.getMessage());
        }

        return links;
    }
}
