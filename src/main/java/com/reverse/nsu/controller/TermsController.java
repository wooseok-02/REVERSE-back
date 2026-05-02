package com.reverse.nsu.controller;

import com.reverse.nsu.entity.Terms;
import com.reverse.nsu.service.TermsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terms")
@RequiredArgsConstructor // 필드 주입 대신 생성자 주입 방식 사용
public class TermsController {

    private final TermsService termsService;

    /**
     * [INTRO01_04] 약관 내용 소개 호출 API
     * 화면 정의서 트리거: 페이지 진입 시 호출
     */
    @GetMapping("/current")
    public ResponseEntity<Terms> getCurrentTerms() {
        Terms terms = termsService.getCurrentTerms();
        return ResponseEntity.ok(terms);
    }

    // 1. 모든 약관 목록 가져오기 (관리자용)
    @GetMapping
    public List<Terms> getAllTerms() {
        return termsService.getAllTerms();
    }

    // 2. 새 약관 저장하기 (관리자용)
    @PostMapping
    public Terms createTerms(@RequestBody Terms terms) {
        return termsService.saveTerms(terms);
    }

    // 3. 약관 수정
    @PutMapping("/{id}")
    public Terms updateTerms(@PathVariable Integer id, @RequestBody Terms termsDetails) {
        // 기존 데이터를 찾아서 수정하는 로직은 서비스에서 처리하거나
        // 컨트롤러에서 ID를 세팅하여 저장합니다.
        termsDetails.setTermsId(id);
        return termsService.saveTerms(termsDetails);
    }

    // 4. 약관 삭제
    @DeleteMapping("/{id}")
    public String deleteTerms(@PathVariable Integer id) {
        termsService.deleteTerms(id);
        return id + "번 약관이 성공적으로 삭제되었습니다.";
    }
}