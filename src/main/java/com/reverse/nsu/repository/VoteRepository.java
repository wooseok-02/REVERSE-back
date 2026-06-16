package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Vote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Integer> {
    Page<Vote> findAllByOrderByCreatedDateDesc(Pageable pageable);
    Page<Vote> findAllByUserIdOrderByCreatedDateDesc(String userId, Pageable pageable);
    void deleteAllByUserId(String userId);
}
