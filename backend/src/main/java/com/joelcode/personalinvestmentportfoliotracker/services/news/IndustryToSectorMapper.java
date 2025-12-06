package com.joelcode.personalinvestmentportfoliotracker.services.news;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class IndustryToSectorMapper {

    private static final Map<String, String> INDUSTRY_TO_SECTOR_MAPPING = new HashMap<>();
    private static final String DEFAULT_SECTOR = "Other";

    static {
        // Technology sector
        INDUSTRY_TO_SECTOR_MAPPING.put("technology", "Technology");
        INDUSTRY_TO_SECTOR_MAPPING.put("communication services", "Technology");

        // FinTech sector
        INDUSTRY_TO_SECTOR_MAPPING.put("financial", "FinTech");
        INDUSTRY_TO_SECTOR_MAPPING.put("financial services", "FinTech");

        // Healthcare sector
        INDUSTRY_TO_SECTOR_MAPPING.put("healthcare", "Healthcare");

        // Retail sector
        INDUSTRY_TO_SECTOR_MAPPING.put("consumer goods", "Retail");
        INDUSTRY_TO_SECTOR_MAPPING.put("consumer cyclical", "Retail");
        INDUSTRY_TO_SECTOR_MAPPING.put("consumer defensive", "Retail");

        // Consumer/Tech sector
        INDUSTRY_TO_SECTOR_MAPPING.put("services", "Consumer/Tech");

        // Default/Other for unmapped industries
        // basic materials, energy, industrial goods, industrials, real estate, utilities
    }

    public String mapIndustryToSector(String industry) {
        if (industry == null || industry.isBlank()) {
            return DEFAULT_SECTOR;
        }

        String normalizedIndustry = industry.toLowerCase().trim();
        return INDUSTRY_TO_SECTOR_MAPPING.getOrDefault(normalizedIndustry, DEFAULT_SECTOR);
    }
}
