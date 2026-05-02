package com.reverse.nsu.service;

import com.reverse.nsu.entity.Terms;
import com.reverse.nsu.repository.TermsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TermsService {

    private final TermsRepository termsRepository;

    /**
     * [INTRO01_04] 약관 내용 소개 호출 API를 위한 로직
     * 'Learn More' 클릭 시 화면에 보여줄 현재 활성화된 약관을 가져옵니다.
     */
    public Terms getCurrentTerms() {
        return termsRepository.findByIsCurrentTrue()
                .orElse(null); // 활성화된 약관이 없으면 null 반환
    }

    /**
     * 관리자용: 모든 약관 목록 조회
     */
    public List<Terms> getAllTerms() {
        return termsRepository.findAll();
    }

    /**
     * 관리자용: 약관 저장 및 수정 (CRUD)
     */
    @Transactional
    public Terms saveTerms(Terms terms) {
        return termsRepository.save(terms);
    }

    /**
     * 관리자용: 약관 삭제 (CRUD)
     */
    @Transactional
    public void deleteTerms(Integer termsId) {
        termsRepository.deleteById(termsId);
    }
}