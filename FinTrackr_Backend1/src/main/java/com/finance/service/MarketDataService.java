package com.finance.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MarketDataService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

    public MarketDataService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Search for securities by query string using Yahoo Finance API
     */
    public List<Map<String, String>> searchSecurities(String query) {
        List<Map<String, String>> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return results;
        }

        try {
            String url = "https://query2.finance.yahoo.com/v1/finance/search?q=" + query.replace(" ", "%20") + "&quotesCount=10&newsCount=0";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", USER_AGENT)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode rootNode = objectMapper.readTree(response.body());
                JsonNode quotesNode = rootNode.path("quotes");
                
                if (quotesNode.isArray()) {
                    for (JsonNode quote : quotesNode) {
                        // We only want equity, etf, mutualfund
                        String quoteType = quote.path("quoteType").asText();
                        if ("EQUITY".equalsIgnoreCase(quoteType) || "ETF".equalsIgnoreCase(quoteType) || "MUTUALFUND".equalsIgnoreCase(quoteType)) {
                            Map<String, String> item = new HashMap<>();
                            item.put("symbol", quote.path("symbol").asText());
                            item.put("name", quote.path("shortname").asText());
                            item.put("type", quoteType);
                            item.put("exchange", quote.path("exchange").asText());
                            results.add(item);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching securities: " + e.getMessage());
        }
        return results;
    }

    /**
     * Get real-time price data for a ticker symbol using Yahoo Finance API
     * Returns a map with currentPrice and regularMarketChange (day change)
     */
    public Map<String, BigDecimal> getQuote(String tickerSymbol) {
        Map<String, BigDecimal> quoteData = new HashMap<>();
        if (tickerSymbol == null || tickerSymbol.trim().isEmpty()) {
            return quoteData;
        }

        try {
            String url = "https://query1.finance.yahoo.com/v8/finance/chart/" + tickerSymbol.trim().toUpperCase() + "?interval=1d&range=1d";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", USER_AGENT)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode rootNode = objectMapper.readTree(response.body());
                JsonNode resultNode = rootNode.path("chart").path("result").get(0);
                
                if (resultNode != null) {
                    JsonNode metaNode = resultNode.path("meta");
                    if (metaNode != null) {
                        double currentPrice = metaNode.path("regularMarketPrice").asDouble(0.0);
                        double previousClose = metaNode.path("chartPreviousClose").asDouble(currentPrice);
                        double dayChange = currentPrice - previousClose;
                        
                        quoteData.put("currentPrice", BigDecimal.valueOf(currentPrice));
                        quoteData.put("dayChange", BigDecimal.valueOf(dayChange));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching quote for " + tickerSymbol + ": " + e.getMessage());
        }
        return quoteData;
    }
}
