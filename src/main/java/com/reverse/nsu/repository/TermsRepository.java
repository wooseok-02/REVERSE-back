package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Terms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TermsRepository extends JpaRepository<Terms, Integer> {

    /**
     * [INTRO01_04] 현재 활성화된 약관 조회
     * 여러 개가 존재할 경우를 대비해 ID 역순(최신순)으로 첫 번째 데이터만 가져옵니다.
     */
    Optional<Terms> findFirstByIsCurrentTrueOrderByTermsIdDesc();

    /**
     * 새로운 약관을 현재 약관으로 설정하기 전,
     * 기존의 모든 활성 약관을 비활성(false) 상태로 변경하는 벌크 업데이트
     */
    @Modifying
    @Query("UPDATE Terms t SET t.isCurrent = false WHERE t.isCurrent = true")
    void updateAllIsCurrentToFalse();
}