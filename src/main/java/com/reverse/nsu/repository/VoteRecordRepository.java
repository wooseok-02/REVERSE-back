package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Vote;
import com.reverse.nsu.entity.VoteOption;
import com.reverse.nsu.entity.VoteRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoteRecordRepository extends JpaRepository<VoteRecord, Integer> {
    Optional<VoteRecord> findByVoteAndUserId(Vote vote, String userId);
    boolean existsByVoteAndUserId(Vote vote, String userId);
    boolean existsByVoteAndUserIdAndOption(Vote vote, String userId, VoteOption option);
    Optional<VoteRecord> findByVoteAndUserIdAndOption(Vote vote, String userId, VoteOption option);
    List<VoteRecord> findAllByVote(Vote vote);
    void deleteAllByVote(Vote vote);
    void deleteAllByOption(VoteOption option);
    void deleteAllByUserId(String userId);
}
