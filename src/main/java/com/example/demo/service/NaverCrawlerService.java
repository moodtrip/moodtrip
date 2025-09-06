//package com.example.demo.service;
//
//import com.example.demo.model.PlaceInfo;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.springframework.stereotype.Service;
//
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class NaverCrawlerService {
//
//    public List<PlaceInfo> crawlPlaces(String keyword) throws Exception {
//        String url = "https://m.map.naver.com/search2/search.naver?query=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
//        WebDriver driver = new ChromeDriver();
//        driver.get(url);
//        Thread.sleep(3000);
//
//        String html = driver.getPageSource();
//        driver.quit();
//        Document doc = Jsoup.parse(html);
//
//        Elements items = doc.select("li._list_item_sis14_40");
//        List<PlaceInfo> resultList = new ArrayList<>();
//
//        for (Element item : items) {
//            PlaceInfo info = new PlaceInfo();
//
//            // 이름
//            Element nameEl = item.selectFirst("strong._item_name_sis14_275");
//            if (nameEl != null) info.setName(nameEl.text());
//
//            // 카테고리
//            Element categoryEl = item.selectFirst("em._item_category_sis14_282");
//            if (categoryEl != null) info.setCategory(categoryEl.text());
//
//            // 주소
//            Element addressEl = item.selectFirst("button._item_address_sis14_319");
//            if (addressEl != null) {
//                String addressText = addressEl.text();
//                addressText = addressText.replace("주소보기", "").trim();  // "주소보기" 제거
//                info.setAddress(addressText);
//            }
//
//            // 이미지
//            Element imgEl = item.selectFirst("img._thumb_img_sis14_77");
//            if (imgEl != null) info.setImageUrl(imgEl.attr("src"));
//
//            // 상세 페이지 링크
//            Element urlEl = item.selectFirst("a._item_thumb_sis14_62");
//            if (urlEl != null) info.setPlaceUrl(urlEl.attr("href"));
//
//            // 평점 (없으면 0.0)
//            Element ratingEl = item.selectFirst("span._item_rating_score_sis14_299");
//            if (ratingEl != null) {
//                try {
//                    info.setRating(Double.parseDouble(ratingEl.text()));
//                } catch (NumberFormatException e) {
//                    info.setRating(0.0);
//                }
//            } else {
//                info.setRating(0.0);
//            }
//
//            // 설명은 LLM이 요약하므로 name + category 기반으로 기본 설정
//            info.setDescription(info.getCategory() != null
//                    ? info.getName() + " (" + info.getCategory() + ")"
//                    : info.getName());
//
//            resultList.add(info);
//        }
//
//        return resultList;
//    }
//}

package com.example.demo.service;

import com.example.demo.model.PlaceInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NaverCrawlerService {

    // 키워드별 임시 캐시
    private final Map<String, List<PlaceInfo>> cache = new ConcurrentHashMap<>();

    public List<PlaceInfo> crawlPlaces(String keyword) throws Exception {
        // 이미 캐시에 있으면 바로 반환
//        if (cache.containsKey(keyword)) {
//            System.out.println("캐시에서 결과 반환: " + keyword);
//            return cache.get(keyword);
//        }

        String url = "https://m.map.naver.com/search2/search.naver?query=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        WebDriver driver = new ChromeDriver();
        driver.get(url);
        Thread.sleep(800); // 기본값 : 3000 || 최적화 값 : 800 ~ 1600

        String html = driver.getPageSource();
        driver.quit();
        Document doc = Jsoup.parse(html);

        Elements items = doc.select("li._list_item_sis14_40");
        List<PlaceInfo> resultList = new ArrayList<>();

        for (Element item : items) {
            PlaceInfo info = new PlaceInfo();

            Element nameEl = item.selectFirst("strong._item_name_sis14_275");
            if (nameEl != null) info.setName(nameEl.text());

            Element categoryEl = item.selectFirst("em._item_category_sis14_282");
            if (categoryEl != null) info.setCategory(categoryEl.text());

            Element addressEl = item.selectFirst("button._item_address_sis14_319");
            if (addressEl != null) {
                String addressText = addressEl.text().replace("주소보기", "").trim();
                info.setAddress(addressText);
            }

            Element imgEl = item.selectFirst("img._thumb_img_sis14_77");
            if (imgEl != null) info.setImageUrl(imgEl.attr("src"));

            Element urlEl = item.selectFirst("a._item_thumb_sis14_62");
            if (urlEl != null) info.setPlaceUrl(urlEl.attr("href"));

            Element ratingEl = item.selectFirst("span._item_rating_score_sis14_299");
            if (ratingEl != null) {
                try {
                    info.setRating(Double.parseDouble(ratingEl.text()));
                } catch (NumberFormatException e) {
                    info.setRating(0.0);
                }
            } else {
                info.setRating(0.0);
            }

            info.setDescription(info.getCategory() != null
                    ? info.getName() + " (" + info.getCategory() + ")"
                    : info.getName());

            resultList.add(info);
        }

        // 캐시에 저장 <- 캐시 사용 시 검색 결과가 75개 이상 나오는 문제 발생
        // 때문에 주석 처리바꿈
//        cache.put(keyword, resultList);
        System.out.println("크롤링 후 캐시에 저장: " + keyword);

        List<PlaceInfo> returnList = new ArrayList<>();

        for(int i = 0 ; i < 25; i++){
            if(resultList.size() <= i )break;
            returnList.add(resultList.get(i));
        }
        System.out.println(returnList.size()+"개 저장 됌");
        return returnList;
    }
}
