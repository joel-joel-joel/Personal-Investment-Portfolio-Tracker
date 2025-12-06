package com.joelcode.personalinvestmentportfoliotracker.services.news;

import com.joelcode.personalinvestmentportfoliotracker.dto.news.NewsArticleDTO;

import java.util.List;

public interface MarketAuxApiClient {

    List<NewsArticleDTO> getNewsByIndustries(String[] industries, int limit);

    List<NewsArticleDTO> getAllNews(int limit);

    List<NewsArticleDTO> getNewsBySector(String sector, int limit);
}
