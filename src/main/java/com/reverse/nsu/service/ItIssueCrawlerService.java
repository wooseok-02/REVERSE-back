package com.reverse.nsu.service;

import com.reverse.nsu.dto.ItIssueDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class ItIssueCrawlerService {

    private static final String MAIN_URL = "https://www.aitimes.com/news/articleList.html?box=pop";
    private static final String BASE_URL = "https://www.aitimes.com";
    private static final int MAX_COUNT = 6;

    private List<ItIssueDto> cachedIssues = Collections.emptyList();

    @PostConstruct
    public void init() {
        crawl();
    }

    // 매일 05:00 갱신
    @Scheduled(cron = "0 0 5 * * *")
    public void crawl() {
        try {
            log.info("[IT 이슈] 크롤링 시작");

            // 1. 메인 페이지에서 Most Popular 기사 목록 수집
            Document mainDoc = Jsoup.connect(MAIN_URL)
                    .userAgent("Mozilla/5.0")
                    .timeout(10_000)
                    .get();

            // articleView 링크 전체 수집
            Elements popularItems = mainDoc.select("a[href*='articleView.html']");
            log.info("[IT 이슈] 발견된 링크 수: {}", popularItems.size());
            for (int i = 0; i < Math.min(10, popularItems.size()); i++) {
                log.info("[IT 이슈] [{}] {}", i, popularItems.get(i).text());
            }

            List<ItIssueDto> result = new ArrayList<>();

            for (Element item : popularItems) {
                if (result.size() >= MAX_COUNT) break;

                String title = item.text().trim();
                if (title.isBlank()) continue;

                String href = item.attr("href");
                String articleUrl = href.startsWith("http") ? href : BASE_URL + href;

                // 2. 각 기사 페이지에서 대표 이미지 수집
                String imageUrl = fetchThumbnail(articleUrl);

                result.add(new ItIssueDto(title, imageUrl, articleUrl));
                log.info("[IT 이슈] 수집: {}", title);
            }

            if (!result.isEmpty()) {
                cachedIssues = Collections.unmodifiableList(result);
                log.info("[IT 이슈] 크롤링 완료: {}건", result.size());
            } else {
                log.warn("[IT 이슈] 크롤링 결과 없음 — 기존 캐시 유지");
            }

        } catch (Exception e) {
            log.error("[IT 이슈] 크롤링 실패 — 기존 캐시 유지: {}", e.getMessage());
        }
    }

    private String fetchThumbnail(String articleUrl) {
        try {
            Document doc = Jsoup.connect(articleUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(8_000)
                    .get();

            // cdn.aitimes.com/news/photo/ 패턴 이미지 우선
            Elements images = doc.select("img[src*='cdn.aitimes.com/news/photo']");
            if (!images.isEmpty()) {
                return images.first().attr("src");
            }

            // fallback: 첫 번째 이미지
            Elements allImages = doc.select("img[src^='https']");
            if (!allImages.isEmpty()) {
                return allImages.first().attr("src");
            }

        } catch (Exception e) {
            log.warn("[IT 이슈] 이미지 수집 실패 ({}): {}", articleUrl, e.getMessage());
        }
        return "";
    }

    public List<ItIssueDto> getIssues() {
        return cachedIssues;
    }
}
