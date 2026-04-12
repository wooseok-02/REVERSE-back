package com.reverse.nsu.repository;

import com.reverse.nsu.entity.RecruitmentApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruitmentApplicationRepository extends JpaRepository<RecruitmentApplication, Integer> {
    // 지원서 저장 및 기본 CRUD 기능을 자동으로 제공합니다.
}