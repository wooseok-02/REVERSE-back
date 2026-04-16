package com.reverse.nsu.repository;

import com.reverse.nsu.entity.RecruitmentPage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RecruitmentPageRepository extends JpaRepository<RecruitmentPage, Integer> {
    Optional<RecruitmentPage> findByRecruitmentId(Integer recruitmentId);
    void deleteByRecruitmentId(Integer recruitmentId);
}