package com.example.demo.service;

import com.example.demo.model.PlaceInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendService {

    private final OpenAiService openAiService;
    private final NaverCrawlerService naverCrawlerService;

    public RecommendService(OpenAiService openAiService, NaverCrawlerService naverCrawlerService) {
        this.openAiService = openAiService;
        this.naverCrawlerService = naverCrawlerService;
    }

    public List<PlaceInfo> recommendPlaces(String userInput) throws Exception {
        String keyword = openAiService.extractKeyword(userInput);
        System.out.println("▶ 생성된 키워드: " + keyword);
        return naverCrawlerService.crawlPlaces(keyword);
    }
}
