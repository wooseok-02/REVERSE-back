package com.reverse.nsu.repository;

import com.reverse.nsu.entity.RecruitmentNotifyEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RecruitmentNotifyEmailRepository extends JpaRepository<RecruitmentNotifyEmail, Integer> {
  //이메일 중복 체크
    Optional<RecruitmentNotifyEmail> findByEmail(String email);
}