package com.reverse.nsu.repository;

import com.reverse.nsu.entity.RecruitmentNotifyEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Repository
public interface RecruitmentNotifyEmailRepository extends JpaRepository<RecruitmentNotifyEmail, Integer> {

  // 1. 이메일로 구독 정보 조회
  Optional<RecruitmentNotifyEmail> findByEmail(String email);

  // 2. 이메일 중복 체크 (boolean 반환으로 서비스 로직 간소화)
  boolean existsByEmail(String email);

  // 3. 이메일 주소로 구독 해지 (삭제)
  @Transactional
  void deleteByEmail(String email);
}