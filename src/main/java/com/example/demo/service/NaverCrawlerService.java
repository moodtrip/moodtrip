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
import java.util.ArrayList;
import java.util.List;

@Service
public class NaverCrawlerService {

    public List<PlaceInfo> crawlPlaces(String keyword) throws Exception {
        String url = "https://m.map.naver.com/search2/search.naver?query=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        WebDriver driver = new ChromeDriver();
        driver.get(url);
        Thread.sleep(3000);

        String html = driver.getPageSource();
        driver.quit();
        Document doc = Jsoup.parse(html);

        Elements items = doc.select("li._list_item_sis14_40");
        List<PlaceInfo> resultList = new ArrayList<>();

        for (Element item : items) {
            PlaceInfo info = new PlaceInfo();

            Element titleEl = item.selectFirst("strong._item_name_sis14_275");
            if (titleEl != null) info.setTitle(titleEl.text());

            Element categoryEl = item.selectFirst("em._item_category_sis14_282");
            if (categoryEl != null) info.setCategory(categoryEl.text());

            Element addressEl = item.selectFirst("button._item_address_sis14_319");
            if (addressEl != null) info.setAddress(addressEl.text());

            Element imgEl = item.selectFirst("img._thumb_img_sis14_77");
            if (imgEl != null) info.setImageUrl(imgEl.attr("src"));

            Element urlEl = item.selectFirst("a._item_thumb_sis14_62");
            if (urlEl != null) info.setPlaceUrl(urlEl.attr("href"));

            resultList.add(info);
        }

        return resultList;
    }
}
