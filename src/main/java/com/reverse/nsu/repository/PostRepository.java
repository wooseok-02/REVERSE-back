package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    // boardId로 전체 조회 (최신순)
    List<Post> findAllByBoardIdOrderByCreatedDateDesc(Integer boardId);
}