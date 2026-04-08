package com.reverse.nsu.controller;

import com.reverse.nsu.entity.Terms;
import com.reverse.nsu.repository.TermsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terms") // 이 주소로 들어오는 요청을 처리하겠다는 뜻!
public class TermsController {

    @Autowired
    private TermsRepository termsRepository;

    // 1. 모든 약관 목록 가져오기
    @GetMapping
    public List<Terms> getAllTerms() {
        return termsRepository.findAll();
    }

    // 2. 새 약관 저장하기
    @PostMapping
    public Terms createTerms(@RequestBody Terms terms) {
        return termsRepository.save(terms);
    }

    @PutMapping("/{id}")
    public Terms updateTerms(@PathVariable Long id, @RequestBody Terms termsDetails) {
        Terms terms = termsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 약관을 찾을 수 없습니다. id: " + id));

        // 수정하고 싶은 필드들을 업데이트
        terms.setTitle(termsDetails.getTitle());
        terms.setContents(termsDetails.getContents());
        terms.setIsCurrent(termsDetails.getIsCurrent());
        terms.setSortOrder(termsDetails.getSortOrder());
        // version도 필요하다면 업데이트
        terms.setVersion(termsDetails.getVersion());

        return termsRepository.save(terms);
    }

    // 2. 삭제 (Delete)
    @DeleteMapping("/{id}")
    public String deleteTerms(@PathVariable Long id) {
        Terms terms = termsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 약관을 찾을 수 없습니다. id: " + id));

        termsRepository.delete(terms);
        return id + "번 약관이 성공적으로 삭제되었습니다.";
    }
}