package com.reverse.nsu.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reverse.nsu.entity.Holiday;
import com.reverse.nsu.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayRepository holidayRepository;
    private final ObjectMapper objectMapper;

    @Value("${public-data.api-key}")
    private String apiKey;

    private static final String API_URL =
            "https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo";

    // 매년 1월 1일 00:05 자동 실행 - 해당 연도 공휴일 갱신
    @Scheduled(cron = "0 5 0 1 1 *")
    public void scheduleYearlySync() {
        int year = LocalDate.now().getYear();
        log.info("[공휴일 배치] {}년 공휴일 자동 동기화 시작", year);
        syncHolidays(year);
    }

    // 특정 연도 공휴일 동기화 (수동 트리거 가능)
    @Transactional
    public void syncHolidays(int year) {
        Short y = (short) year;

        // 기존 데이터 삭제 후 재동기화
        holidayRepository.deleteByYear(y);

        List<Holiday> holidays = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            try {
                List<Holiday> monthly = fetchHolidays(year, month, y);
                holidays.addAll(monthly);
            } catch (Exception e) {
                log.error("[공휴일 배치] {}년 {}월 조회 실패: {}", year, month, e.getMessage());
            }
        }

        holidayRepository.saveAll(holidays);
        log.info("[공휴일 배치] {}년 공휴일 {}건 저장 완료", year, holidays.size());
    }

    private List<Holiday> fetchHolidays(int year, int month, Short yearShort) throws Exception {
        String solMonth = String.format("%02d", month);

        URI uri = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("serviceKey", apiKey)
                .queryParam("solYear", year)
                .queryParam("solMonth", solMonth)
                .queryParam("numOfRows", 100)
                .queryParam("_type", "json")
                .encode()
                .build()
                .toUri();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        log.debug("[공휴일 API] {}년 {}월 응답: {}", year, month, response.body());

        return parseHolidays(response.body(), yearShort);
    }

    private List<Holiday> parseHolidays(String json, Short year) throws Exception {
        List<Holiday> result = new ArrayList<>();
        JsonNode root = objectMapper.readTree(json);
        JsonNode items = root.path("response").path("body").path("items").path("item");

        if (items.isMissingNode() || items.isNull()) {
            return result;
        }

        // 단건 응답은 Object, 복수 응답은 Array
        if (items.isArray()) {
            for (JsonNode item : items) {
                result.add(toEntity(item, year));
            }
        } else if (items.isObject()) {
            result.add(toEntity(items, year));
        }

        return result;
    }

    private Holiday toEntity(JsonNode item, Short year) {
        String locdate = item.path("locdate").asText();
        LocalDate date = LocalDate.parse(locdate, DateTimeFormatter.ofPattern("yyyyMMdd"));
        String isHolidayStr = item.path("isHoliday").asText("Y");

        return Holiday.builder()
                .holidayDate(date)
                .holidayName(item.path("dateName").asText())
                .isHoliday("Y".equalsIgnoreCase(isHolidayStr))
                .year(year)
                .build();
    }

    // 월별 공휴일 조회
    @Transactional(readOnly = true)
    public List<Holiday> getHolidaysByMonth(int year, int month) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());
        return holidayRepository.findByHolidayDateBetweenOrderByHolidayDateAsc(firstDay, lastDay);
    }
}
