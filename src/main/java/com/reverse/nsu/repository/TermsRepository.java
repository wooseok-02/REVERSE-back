package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Terms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TermsRepository extends JpaRepository<Terms, Integer> {

    // [INTRO01_04] 화면 정의서의 '약관 내용 출력'을 위해
    // isCurrent 컬럼이 true(1)인 약관을 찾는 메서드 추가
    Optional<Terms> findByIsCurrentTrue();
}