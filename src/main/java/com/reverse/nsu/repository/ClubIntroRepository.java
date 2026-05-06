package com.reverse.nsu.repository;

import com.reverse.nsu.entity.ClubIntro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClubIntroRepository extends JpaRepository<ClubIntro, Integer> {
    // isActive가 true인 데이터만 조회 (메인 페이지용)
    List<ClubIntro> findByIsActiveTrue();
}