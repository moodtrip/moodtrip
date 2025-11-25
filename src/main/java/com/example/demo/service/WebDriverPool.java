package com.example.demo.service;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WebDriverPool {
    private final BlockingQueue<WebDriver> pool;
    private final int poolSize;

    public WebDriverPool(int poolSize) {
        this.poolSize = Math.max(poolSize, 5); // 최소 5개
        this.pool = new LinkedBlockingQueue<>(this.poolSize);
        for (int i = 0; i < this.poolSize; i++) {
            this.pool.offer(createDriver());
        }
    }

    private WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-logging");
        options.addArguments("--silent");
        options.addArguments("--blink-settings=imagesEnabled=false"); // 이미지 로딩 비활성화
        options.setPageLoadStrategy(PageLoadStrategy.EAGER); // 빠른 로딩
        return new ChromeDriver(options);
    }

    public WebDriver getDriver() throws InterruptedException {
            return pool.take();
        }

        public void returnDriver(WebDriver driver) {
            if (driver != null) {
                // 드라이버 상태를 초기화하고 풀에 반납
                driver.get("about:blank");
                pool.offer(driver);
            }
        }

        public void closeAll() {
            for (WebDriver driver : pool) {
                driver.quit();
            }
        }
}
