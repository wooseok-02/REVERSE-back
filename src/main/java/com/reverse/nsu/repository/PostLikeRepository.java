package com.reverse.nsu.repository;

import com.reverse.nsu.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {

    Optional<PostLike> findByUserIdAndPostId(String userId, Integer postId);

    boolean existsByUserIdAndPostId(String userId, Integer postId);
}
