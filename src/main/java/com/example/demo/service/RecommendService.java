package com.example.demo.service;

import com.example.demo.model.PlaceInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        // 1. 키워드 추출
        List<String> keyword = openAiService.extractKeyword(userInput);
        System.out.println("🔍 올라마가 생성한 키워드: " + keyword);

        // 2. 특수문자 제거 및 정제
//        keyword = keyword.replaceAll("[^가-힣a-zA-Z0-9 ]", "").trim();
//        System.out.println("🧹 정제된 키워드: " + keyword);
//
//        // 3. 두 번째 단어 사용
//        String[] words = keyword.split("\\s+");
//        if (words.length >= 2) {
//            keyword = words[1];  // 두 번째 단어 사용
//            System.out.println("➡️ 두 번째 키워드 사용: " + keyword);
//        } else if (words.length == 1) {
//            keyword = words[0];  // 하나일 경우 첫 번째 단어 사용
//            System.out.println("➡️ 첫 번째 키워드만 사용: " + keyword);
//        } else {
//            throw new IllegalArgumentException("키워드가 비어 있습니다.");
//        }

        // 4. 크롤링
//        List<PlaceInfo> places = naverCrawlerService.crawlPlaces(keyword);
        List<PlaceInfo> places = new ArrayList<>();
        for(String keywordStr : keyword) {
            List<PlaceInfo> placesList = naverCrawlerService.crawlPlaces(keywordStr);
            places.addAll(placesList);
        }
        System.out.println("🕸 크롤링 완료. 장소 수: " + places.size());

        // 5. 평점 내림차순 정렬
        places.sort((p1, p2) -> Double.compare(p2.getRating(), p1.getRating()));

        return places;
    }
}