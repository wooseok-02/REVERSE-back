package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Post;
import com.reverse.nsu.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {

    // [기존] 특정 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
    Optional<PostLike> findByUserIdAndPost(String userId, Post post);

    // [기존] 존재 여부 확인
    boolean existsByUserIdAndPost(String userId, Post post);

    // [추가] 게시글 삭제 테스트를 위해 필수!
    // 특정 게시글에 달린 모든 좋아요를 한 번에 삭제합니다.
    void deleteAllByPost(Post post);
}