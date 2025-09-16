package com.example.demo.service;

import com.example.demo.model.PlaceInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NaverCrawlerService {

    // 캐시 엔트리 클래스
    private static class CacheEntry {
        private final List<PlaceInfo> data;
        private final LocalDateTime timestamp;
        public CacheEntry(List<PlaceInfo> data) {
            this.data = data;
            this.timestamp = LocalDateTime.now();
        }
        public boolean isExpired(long minutes) {
            return ChronoUnit.MINUTES.between(timestamp, LocalDateTime.now()) > minutes;
        }
        public List<PlaceInfo> getData() {
            return new ArrayList<>(data); // 방어적 복사
        }
    }

    // 키워드별 캐시 (30분 TTL)
    private final Map<String, CacheEntry> placeCache = new ConcurrentHashMap<>();
    private final Map<String, CacheEntry> locationCache = new ConcurrentHashMap<>();
    private static final long CACHE_TTL_MINUTES = 30;
    private static final int MAX_RESULTS = 25;
    private static final int CRAWL_DELAY_MS = 1000;

    /**
     * 사용자 위치 정보 크롤링 (캐시 적용)
     */
    public String crawUserLocate(String lat, String lng) throws Exception {
        String cacheKey = lat + "," + lng;
        // 캐시 확인
        CacheEntry cached = locationCache.get(cacheKey);
        if (cached != null && !cached.isExpired(CACHE_TTL_MINUTES)) {
            System.out.println("위치 정보 캐시에서 반환: " + cacheKey);
            return cached.getData().get(0).getAddress(); // 위치는 첫 번째 항목의 주소로 저장
        }

        String url = "https://map.naver.com/p?lat=" + lat + "&lng=" + lng;
        try (WebDriverManager driverManager = new WebDriverManager()) {
            WebDriver driver = driverManager.getDriver();
            driver.get(url);
            Thread.sleep(CRAWL_DELAY_MS);

            String html = driver.getPageSource();
            Document doc = Jsoup.parse(html);
            Elements items = doc.select("button.btn_address");
            String address = items.text();
            if (!address.isEmpty()) {
                address = address.split(" ")[0];
                // 캐시에 저장 (PlaceInfo 형태로 저장해서 일관성 유지)
                PlaceInfo locationInfo = new PlaceInfo();
                locationInfo.setAddress(address);
                locationCache.put(cacheKey, new CacheEntry(Arrays.asList(locationInfo)));
                System.out.println("위치 정보 크롤링 완료 및 캐시 저장: " + address);
                return address;
            }
        }
        return ""; // 빈 문자열 반환
    }

    /**
     * 장소 정보 크롤링 (중복 방지 및 캐시 적용)
     */
    public List<PlaceInfo> crawlPlaces(String keyword) throws Exception {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String normalizedKeyword = keyword.trim().toLowerCase();
        // 캐시 확인
        CacheEntry cached = placeCache.get(normalizedKeyword);
        if (cached != null && !cached.isExpired(CACHE_TTL_MINUTES)) {
            System.out.println("장소 정보 캐시에서 반환: " + keyword + " (" + cached.getData().size() + "개)");
            return cached.getData();
        }

        // 크롤링 실행
        List<PlaceInfo> results = performCrawling(keyword, normalizedKeyword);
        // 캐시에 저장
        if (!results.isEmpty()) {
            placeCache.put(normalizedKeyword, new CacheEntry(results));
            System.out.println("장소 정보 크롤링 완료 및 캐시 저장: " + keyword + " (" + results.size() + "개)");
        }
        return results;
    }

    /**
     * 실제 크롤링 수행
     */
    private List<PlaceInfo> performCrawling(String originalKeyword, String normalizedKeyword) throws Exception {
        String encodedKeyword = URLEncoder.encode(originalKeyword, StandardCharsets.UTF_8);
        String url = "https://m.map.naver.com/search2/search.naver?query=" + encodedKeyword;

        try (WebDriverManager driverManager = new WebDriverManager()) {
            WebDriver driver = driverManager.getDriver();
            driver.get(url);
            Thread.sleep(CRAWL_DELAY_MS);

            String html = driver.getPageSource();
            Document doc = Jsoup.parse(html);
            Elements items = doc.select("li._list_item_sis14_40");

            List<PlaceInfo> resultList = new ArrayList<>();
            Set<String> processedNames = new HashSet<>(); // 중복 제거용

            for (Element item : items) {
                if (resultList.size() >= MAX_RESULTS) break;

                PlaceInfo info = extractPlaceInfo(item, originalKeyword);
                // 중복 체크 (이름 기준)
                if (info.getName() != null && !processedNames.contains(info.getName())) {
                    processedNames.add(info.getName());
                    resultList.add(info);
                }
            }

            return resultList;
        }
    }

    /**
     * HTML 요소에서 PlaceInfo 추출
     */
    private PlaceInfo extractPlaceInfo(Element item, String keyword) {
        PlaceInfo info = new PlaceInfo();
        info.setKeyword(keyword);

        // 이름
        Element nameEl = item.selectFirst("strong._item_name_sis14_275");
        if (nameEl != null) {
            info.setName(nameEl.text().trim());
        }

        // 카테고리
        Element categoryEl = item.selectFirst("em._item_category_sis14_282");
        if (categoryEl != null) {
            info.setCategory(categoryEl.text().trim());
        }

        // 주소
        Element addressEl = item.selectFirst("button._item_address_sis14_319");
        if (addressEl != null) {
            String addressText = addressEl.text()
                    .replace("주소보기", "")
                    .trim();
            info.setAddress(addressText);
        }

        // 이미지
        Element imgEl = item.selectFirst("img._thumb_img_sis14_77");
        if (imgEl != null) {
            String imgSrc = imgEl.attr("src");
            if (!imgSrc.isEmpty()) {
                info.setImageUrl(imgSrc);
            }
        }

        // 상세 페이지 링크
        Element urlEl = item.selectFirst("a._item_thumb_sis14_62");
        if (urlEl != null) {
            String href = urlEl.attr("href");
            if (!href.isEmpty()) {
                info.setPlaceUrl(href);
            }
        }

        // 평점
        Element ratingEl = item.selectFirst("span._item_rating_score_sis14_299");
        double rating = 0.0;
        if (ratingEl != null) {
            try {
                rating = Double.parseDouble(ratingEl.text().trim());
            } catch (NumberFormatException e) {
                // 기본값 유지
            }
        }
        info.setRating(rating);

        // 설명 생성
        String description = info.getName() != null ? info.getName() : "";
        if (info.getCategory() != null && !info.getCategory().isEmpty()) {
            description += " (" + info.getCategory() + ")";
        }
        info.setDescription(description);

        return info;
    }

    /**
     * 캐시 정리 (선택적)
     */
    public void clearExpiredCache() {
        placeCache.entrySet().removeIf(entry -> entry.getValue().isExpired(CACHE_TTL_MINUTES));
        locationCache.entrySet().removeIf(entry -> entry.getValue().isExpired(CACHE_TTL_MINUTES));
        System.out.println("만료된 캐시 항목 정리 완료");
    }

    /**
     * WebDriver 관리 클래스 (자동 종료 보장)
     */
    private static class WebDriverManager implements AutoCloseable {
        private final WebDriver driver;

        public WebDriverManager() {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-logging");
            options.addArguments("--silent");
            this.driver = new ChromeDriver(options);
        }

        public WebDriver getDriver() {
            return driver;
        }

        @Override
        public void close() {
            if (driver != null) {
                try {
                    driver.quit();
                } catch (Exception e) {
                    System.err.println("WebDriver 종료 중 오류: " + e.getMessage());
                }
            }
        }
    }
}