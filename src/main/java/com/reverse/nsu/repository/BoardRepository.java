package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Integer> {
    Optional<Board> findByBoardName(String boardName);
}