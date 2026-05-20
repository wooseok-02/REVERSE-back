package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Study;
import com.reverse.nsu.entity.StudyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRepository extends JpaRepository<Study, Integer> {

    Page<Study> findAllByOrderByCreatedDateDesc(Pageable pageable);

    Page<Study> findAllByStatusOrderByCreatedDateDesc(StudyStatus status, Pageable pageable);

    @Query("SELECT s FROM Study s WHERE " +
            "s.studyName LIKE %:keyword% OR " +
            "s.description LIKE %:keyword% OR " +
            "s.goal LIKE %:keyword% " +
            "ORDER BY s.createdDate DESC")
    Page<Study> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT s FROM Study s WHERE " +
            "s.status = :status AND " +
            "(s.studyName LIKE %:keyword% OR s.description LIKE %:keyword% OR s.goal LIKE %:keyword%) " +
            "ORDER BY s.createdDate DESC")
    Page<Study> searchByStatusAndKeyword(@Param("status") StudyStatus status,
                                          @Param("keyword") String keyword,
                                          Pageable pageable);
}
