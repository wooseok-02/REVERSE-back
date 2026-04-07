package com.reverse.nsu.controller;

import com.example.demo.entity.Terms;
import com.example.demo.repository.TermsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/terms")
public class TermsController {

    @Autowired
    private TermsRepository termsRepository;

    // 1. 모든 약관 목록 가져오기
    @GetMapping
    public List<Terms> getAllTerms() {
        return termsRepository.findAll();
    }

    // 2. 특정 약관 상세 보기 (추가해두면 좋아요)
    @GetMapping("/{id}")
    public Terms getTermsById(@PathVariable Integer id) {
        return termsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 약관을 찾을 수 없습니다. id: " + id));
    }

    // 3. 새 약관 저장하기
    @PostMapping
    public Terms createTerms(@RequestBody Terms terms) {
        return termsRepository.save(terms);
    }

    // 4. 약관 수정 (Update)
    @PutMapping("/{id}")
    public Terms updateTerms(@PathVariable Integer id, @RequestBody Terms termsDetails) {
        // Repository의 PK 타입이 Integer이므로 매개변수 타입을 Integer로 변경
        Terms terms = termsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 약관을 찾을 수 없습니다. id: " + id));

        // 리더님이 추가한 필드들까지 포함해서 업데이트
        terms.setTitle(termsDetails.getTitle());
        terms.setContents(termsDetails.getContents());
        terms.setIsCurrent(termsDetails.getIsCurrent());
        terms.setSortOrder(termsDetails.getSortOrder());
        terms.setVersion(termsDetails.getVersion());
        terms.setUpdatedBy(termsDetails.getUpdatedBy()); // 리더님이 추가한 필드

        return termsRepository.save(terms);
    }

    // 5. 삭제 (Delete)
    @DeleteMapping("/{id}")
    public String deleteTerms(@PathVariable Integer id) {
        Terms terms = termsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 약관을 찾을 수 없습니다. id: " + id));

        termsRepository.delete(terms);
        return id + "번 약관이 성공적으로 삭제되었습니다.";
    }
}