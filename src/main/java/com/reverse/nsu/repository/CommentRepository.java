package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findAllByPostIdOrderByCreatedDateAsc(Integer postId);

    int countByPostId(Integer postId);
}
