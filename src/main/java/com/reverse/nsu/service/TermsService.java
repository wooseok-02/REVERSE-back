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
     * [INTRO01_04] 현재 활성화된 약관 조회
     * 수정 포인트: 여러 개가 있을 경우 에러를 내지 않고 가장 최신 ID를 가진 약관 하나만 가져옵니다.
     */
    public Terms getCurrentTerms() {
        return termsRepository.findFirstByIsCurrentTrueOrderByTermsIdDesc()
                .orElse(null);
    }

    /**
     * 관리자용: 모든 약관 목록 조회
     */
    public List<Terms> getAllTerms() {
        return termsRepository.findAll();
    }

    /**
     * 관리자용: 약관 상세 조회
     */
    public Terms getTermsById(Integer id) {
        return termsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 약관을 찾을 수 없습니다. ID: " + id));
    }

    /**
     * 관리자용: 약관 저장 및 수정
     * '현재 약관'으로 설정 시 기존 활성 약관들을 모두 비활성화합니다.
     */
    @Transactional
    public Terms saveTerms(Terms terms) {
        // 새 약관을 '현재 약관'으로 설정하려는 경우
        if (Boolean.TRUE.equals(terms.getIsCurrent())) {
            // DB에 있는 기존 true 항목들을 전부 false로 밀어버림 (중복 방지)
            termsRepository.updateAllIsCurrentToFalse();
        }
        return termsRepository.save(terms);
    }

    /**
     * 관리자용: 약관 삭제
     */
    @Transactional
    public void deleteTerms(Integer termsId) {
        termsRepository.deleteById(termsId);
    }
}