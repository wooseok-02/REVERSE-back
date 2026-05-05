package com.reverse.nsu.controller;

import com.reverse.nsu.entity.Terms;
import com.reverse.nsu.service.TermsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/terms")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")// 통합된 위치에 설정
public class TermsController {

    private final TermsService termsService;

    /**
     * [INTRO01_04] 현재 활성화된 약관 내용 조회
     */
    @GetMapping("/current")
    public ResponseEntity<Terms> getCurrentTerms() {
        Terms terms = termsService.getCurrentTerms();
        return ResponseEntity.ok(terms);
    }

    /**
     * 모든 약관 목록 가져오기 (관리자용)
     */
    @GetMapping
    public List<Terms> getAllTerms() {
        return termsService.getAllTerms();
    }

    /**
     * 특정 약관 상세 보기
     */
    @GetMapping("/{id}")
    public ResponseEntity<Terms> getTermsById(@PathVariable Integer id) {
        Terms terms = termsService.getTermsById(id);
        return ResponseEntity.ok(terms);
    }

    /**
     * 새 약관 저장하기 (관리자용)
     */
    @PostMapping
    public Terms createTerms(@RequestBody Terms terms) {
        return termsService.saveTerms(terms);
    }

    /**
     * 약관 수정
     */
    @PutMapping("/{id}")
    public Terms updateTerms(@PathVariable Integer id, @RequestBody Terms termsDetails) {
        // 서비스에서 ID 세팅 및 수정 로직 처리
        termsDetails.setTermsId(id);
        return termsService.saveTerms(termsDetails);
    }

    /**
     * 약관 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTerms(@PathVariable Integer id) {
        termsService.deleteTerms(id);
        return ResponseEntity.ok(Map.of(
                "message", id + "번 약관이 성공적으로 삭제되었습니다."
        ));
    }
}