package com.joelcode.personalinvestmentportfoliotracker.controllers.utilitycontrollers;

import com.joelcode.personalinvestmentportfoliotracker.dto.news.NewsArticleDTO;
import com.joelcode.personalinvestmentportfoliotracker.services.news.MarketAuxApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "*")
@Profile("!test")
public class NewsController {

    @Autowired
    private MarketAuxApiClient marketAuxApiClient;

    // Get all news with optional limit
    @GetMapping
    public ResponseEntity<List<NewsArticleDTO>> getAllNews(
            @RequestParam(value = "limit", defaultValue = "50") int limit
    ) {
        try {
            List<NewsArticleDTO> news = marketAuxApiClient.getAllNews(limit);
            return ResponseEntity.ok(news);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get news by sector
    @GetMapping("/sector/{sector}")
    public ResponseEntity<List<NewsArticleDTO>> getNewsBySector(
            @PathVariable String sector,
            @RequestParam(value = "limit", defaultValue = "50") int limit
    ) {
        try {
            List<NewsArticleDTO> news = marketAuxApiClient.getNewsBySector(sector, limit);
            return ResponseEntity.ok(news);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(List.of());
        }
    }

    // Get news by multiple sectors
    @GetMapping("/sectors")
    public ResponseEntity<List<NewsArticleDTO>> getNewsByMultipleSectors(
            @RequestParam(value = "sectors") String[] sectors,
            @RequestParam(value = "limit", defaultValue = "50") int limit
    ) {
        try {
            // Convert sectors to industries for API call
            List<String> industries = new ArrayList<>();
            for (String sector : sectors) {
                String[] sectorIndustries = getSectorIndustries(sector);
                for (String industry : sectorIndustries) {
                    if (!industries.contains(industry)) {
                        industries.add(industry);
                    }
                }
            }

            List<NewsArticleDTO> news = marketAuxApiClient.getNewsByIndustries(
                    industries.toArray(new String[0]),
                    limit
            );
            return ResponseEntity.ok(news);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(List.of());
        }
    }

    private String[] getSectorIndustries(String sector) {
        return switch (sector) {
            case "Technology" -> new String[]{"technology", "communication services"};
            case "Semiconductors" -> new String[]{"technology"};
            case "FinTech" -> new String[]{"financial", "financial services"};
            case "Consumer/Tech" -> new String[]{"services", "consumer cyclical", "consumer defensive"};
            case "Healthcare" -> new String[]{"healthcare"};
            case "Retail" -> new String[]{"consumer goods", "consumer cyclical", "consumer defensive"};
            default -> new String[]{};
        };
    }
}
