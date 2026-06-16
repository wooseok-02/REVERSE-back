package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Comment;
import com.reverse.nsu.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    // [기존 수정] 게시글별 댓글 리스트 조회
    List<Comment> findAllByPostOrderByCreatedDateAsc(Post post);

    // [기존 수정] 게시글별 댓글 개수 카운트
    int countByPost(Post post);

    // [추가] 게시글 삭제 시 연관된 모든 댓글(대댓글 포함)을 삭제하기 위해 필요
    // 💡 JPA가 내부적으로 대댓글-댓글 순서를 고려해서 삭제 쿼리를 날려줍니다.
    void deleteAllByPost(Post post);

    // 강제탈퇴 시 사용: 대댓글 먼저, 원댓글 나중에 삭제하여 cascade 충돌 방지
    void deleteAllByUserIdAndParentIsNotNull(String userId);
    void deleteAllByUserIdAndParentIsNull(String userId);
}