package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Project;
import com.reverse.nsu.entity.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

    /**
     * 1. 기본 목록 조회 (페이징)
     */
    Page<Project> findAllByOrderByCreatedDateDesc(Pageable pageable);

    /**
     * 2. 상태별 필터링 조회 (PENDING, ACTIVE, CLOSED)
     */
    Page<Project> findAllByStatusOrderByCreatedDateDesc(ProjectStatus status, Pageable pageable);

    /**
     * 3. 프로젝트명 또는 소개/목표에 검색어가 포함된 데이터 조회 (검색 기능)
     * 화면 정의서: "검색된 정보와 일치하는 프로젝트 제목, 내용이 있는 지 확인"
     */
    @Query("SELECT p FROM Project p WHERE " +
            "p.projectName LIKE %:keyword% OR " +
            "p.description LIKE %:keyword% OR " +
            "p.goal LIKE %:keyword% " +
            "ORDER BY p.createdDate DESC")
    Page<Project> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 4. 상태 필터링 + 검색어 동시 적용
     */
    @Query("SELECT p FROM Project p WHERE " +
            "p.status = :status AND " +
            "(p.projectName LIKE %:keyword% OR p.description LIKE %:keyword% OR p.goal LIKE %:keyword%) " +
            "ORDER BY p.createdDate DESC")
    Page<Project> searchByStatusAndKeyword(@Param("status") ProjectStatus status, @Param("keyword") String keyword, Pageable pageable);
}