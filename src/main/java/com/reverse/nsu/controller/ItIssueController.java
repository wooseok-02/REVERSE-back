package com.reverse.nsu.controller;

import com.reverse.nsu.dto.ApiResponse;
import com.reverse.nsu.dto.ItIssueDto;
import com.reverse.nsu.service.ItIssueCrawlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/it-issues")
@RequiredArgsConstructor
public class ItIssueController {

    private final ItIssueCrawlerService itIssueCrawlerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ItIssueDto>>> getIssues() {
        List<ItIssueDto> issues = itIssueCrawlerService.getIssues();
        if (issues.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("NO_DATA", "현재 이슈를 불러올 수 없습니다."));
        }
        return ResponseEntity.ok(ApiResponse.ok(issues));
    }
}
