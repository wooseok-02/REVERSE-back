package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Vote;
import com.reverse.nsu.entity.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteOptionRepository extends JpaRepository<VoteOption, Integer> {
    List<VoteOption> findAllByVoteOrderBySortOrderAsc(Vote vote);
    void deleteAllByVote(Vote vote);
}
