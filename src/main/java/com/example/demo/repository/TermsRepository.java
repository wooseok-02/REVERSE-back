package com.example.demo.repository;

import com.example.demo.entity.Terms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermsRepository extends JpaRepository<Terms, Long> {
    // 여기에 아무것도 안 적어도 됩니다!
    // JpaRepository를 상속받는 것만으로도 저장, 삭제, 조회 기능을 이미 다 갖게 돼요.
}