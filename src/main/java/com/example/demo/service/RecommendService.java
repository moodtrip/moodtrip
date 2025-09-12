package com.example.demo.service;

import com.example.demo.dto.PlaceDto;
import com.example.demo.model.PlaceInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class RecommendService {

    private final OpenAiService openAiService;
    private final NaverCrawlerService naverCrawlerService;

    public RecommendService(OpenAiService openAiService, NaverCrawlerService naverCrawlerService) {
        this.openAiService = openAiService;
        this.naverCrawlerService = naverCrawlerService;
    }

    public String userLocateSearch(String lat, String lng) throws Exception {
        String locate = naverCrawlerService.crawUserLocate(lat, lng);

        System.out.println(locate);

        return locate;
    }

    public PlaceDto detailPlace(String userInput) throws Exception {
        // 1. 키워드 추출
        List<String> keyword = openAiService.extractKeyword(userInput);
        System.out.println("올라마가 생성한 키워드: " + keyword);

        // 2. 특수문자 제거 및 정제
//        keyword = keyword.replaceAll("[^가-힣a-zA-Z0-9 ]", "").trim();
//        System.out.println("정제된 키워드: " + keyword);
//
//        // 3. 두 번째 단어 사용
//        String[] words = keyword.split("\\s+");
//        if (words.length >= 2) {
//            keyword = words[1];  // 두 번째 단어 사용
//            System.out.println("두 번째 키워드 사용: " + keyword);
//        } else if (words.length == 1) {
//            keyword = words[0];  // 하나일 경우 첫 번째 단어 사용
//            System.out.println("첫 번째 키워드만 사용: " + keyword);
//        } else {
//            throw new IllegalArgumentException("키워드가 비어 있습니다.");
//        }

        // 4. 크롤링
//        List<PlaceInfo> places = naverCrawlerService.crawlPlaces(keyword);
        List<PlaceInfo> places = new ArrayList<>();
        String comment = "";
        for(int i = 0; i < keyword.size(); i++) {
            if(i<keyword.size() - 1) {
                // 테스트용 문구
                System.out.println(i+"번째 장소 검색 중");

                List<PlaceInfo> placesList = naverCrawlerService.crawlPlaces(keyword.get(i));
                places.addAll(placesList);
            }
            else{
                comment = keyword.get(i);
            }
        }
        
        //중복제거
        places = places.stream().distinct().collect(Collectors.toList());

        //리스트 배열 섞기
//        Collections.shuffle(places);

        System.out.println("크롤링 완료. 장소 수: " + places.size());

        // 5. 평점 내림차순 정렬
        places.sort((p1, p2) -> Double.compare(p2.getRating(), p1.getRating()));

        return new PlaceDto(places, comment);
    }

    public PlaceDto recommendPlaces(String userInput, String searchType) throws Exception {
        List<PlaceInfo> places = new ArrayList<>();
        List<String> keyword = new ArrayList<>();

        if(searchType == "situation") {
            // 1. 키워드 추출
            keyword = openAiService.extractKeyword(userInput);
            System.out.println("올라마가 생성한 키워드: " + keyword);

        }else{
            keyword = openAiService.emotionAiSearch(userInput);
            System.out.println("올라마가 생성한 키워드: " + keyword);
        }

        String comment = keyword.get(keyword.size() - 1);
        List<String> searchKeywords = keyword.subList(0, keyword.size() - 1);

        ExecutorService executor = Executors.newFixedThreadPool(5);

        List<CompletableFuture<List<PlaceInfo>>> futures = searchKeywords.stream()
                .map(k -> CompletableFuture.supplyAsync(() -> {
                    try {
                        System.out.println("장소 검색: " + k);
                        return naverCrawlerService.crawlPlaces(k);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ArrayList<PlaceInfo>();
                    }
                }, executor))
                .collect(Collectors.toList());

//        for(String input : searchKeywords) {
//                // 테스트용 문구
//                System.out.println(input + "으로 검색 중");
//
//                List<PlaceInfo> placesList = naverCrawlerService.crawlPlaces(input);
//        }



        //중복제거
//        places = places.stream().distinct().collect(Collectors.toList());

        places = futures.stream().map(CompletableFuture::join).flatMap(List::stream).distinct().collect(Collectors.toList());

        //리스트 배열 섞기
        Collections.shuffle(places);

        System.out.println("크롤링 완료. 장소 수: " + places.size());

        // 5. 평점 내림차순 정렬
        places.sort((p1, p2) -> Double.compare(p2.getRating(), p1.getRating()));

        return new PlaceDto(places, comment);
    }

    public PlaceDto recommendPlacesByUserLocate(String userInput, String searchType, String userLocate) throws Exception {
        List<PlaceInfo> places = new ArrayList<>();
        List<String> keyword = new ArrayList<>();

        if(searchType == "situation") {
            // 1. 키워드 추출
            keyword = openAiService.extractKeyword(userInput);
            System.out.println("올라마가 생성한 키워드: " + keyword);

        }else{
            keyword = openAiService.emotionAiSearch(userInput);
            System.out.println("올라마가 생성한 키워드: " + keyword);
        }

        String comment = keyword.get(keyword.size() - 1);
        List<String> searchKeywords = keyword.subList(0, keyword.size() - 1);

        ExecutorService executor = Executors.newFixedThreadPool(5);

        List<CompletableFuture<List<PlaceInfo>>> futures = searchKeywords.stream()
                .map(k -> CompletableFuture.supplyAsync(() -> {
                    try {
                        System.out.println("장소 검색: " + userLocate + " " + k);
                        return naverCrawlerService.crawlPlaces(userLocate+ " " +k);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ArrayList<PlaceInfo>();
                    }
                }, executor))
                .collect(Collectors.toList());

//        for(String input : searchKeywords) {
//                // 테스트용 문구
//                System.out.println(input + "으로 검색 중");
//
//                List<PlaceInfo> placesList = naverCrawlerService.crawlPlaces(input);
//        }



        //중복제거
//        places = places.stream().distinct().collect(Collectors.toList());

        places = futures.stream().map(CompletableFuture::join).flatMap(List::stream).distinct().collect(Collectors.toList());

        //리스트 배열 섞기
        Collections.shuffle(places);

        System.out.println("크롤링 완료. 장소 수: " + places.size());

        // 5. 평점 내림차순 정렬
        places.sort((p1, p2) -> Double.compare(p2.getRating(), p1.getRating()));

        return new PlaceDto(places, comment);
    }
}