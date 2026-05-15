package com.reverse.nsu.repository;

import com.reverse.nsu.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {

    // [수정] post.postId 경로를 명시
    Optional<PostLike> findByUserIdAndPost_PostId(String userId, Integer postId);

    // [수정] 위와 동일하게 변경
    boolean existsByUserIdAndPost_PostId(String userId, Integer postId);
}