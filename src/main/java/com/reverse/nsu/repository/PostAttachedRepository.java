package com.reverse.nsu.repository;

import com.reverse.nsu.entity.PostAttached;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostAttachedRepository extends JpaRepository<PostAttached, Integer> {
    List<PostAttached> findAllByPostId(Integer postId);
    void deleteAllByPostId(Integer postId);
}