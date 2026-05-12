package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Integer> {

    // 기본 목록 조회
    Page<Post> findAllByBoardIdOrderByCreatedDateDesc(Integer boardId, Pageable pageable);
    Page<Post> findAllByBoardIdAndIsExternalTrueOrderByCreatedDateDesc(Integer boardId, Pageable pageable);

    // [신규] 카테고리 필터링 조회
    Page<Post> findAllByBoardIdAndPostCategoryOrderByCreatedDateDesc(Integer boardId, String postCategory, Pageable pageable);
    Page<Post> findAllByBoardIdAndPostCategoryAndIsExternalTrueOrderByCreatedDateDesc(Integer boardId, String postCategory, Pageable pageable);

    // 검색 기능 (제목, 내용, 작성자)
    Page<Post> findAllByBoardIdAndPostTitleContainingOrderByCreatedDateDesc(Integer boardId, String title, Pageable pageable);
    Page<Post> findAllByBoardIdAndPostTitleContainingAndIsExternalTrueOrderByCreatedDateDesc(Integer boardId, String title, Pageable pageable);
    Page<Post> findAllByBoardIdAndPostContentsContainingOrderByCreatedDateDesc(Integer boardId, String content, Pageable pageable);
    Page<Post> findAllByBoardIdAndPostContentsContainingAndIsExternalTrueOrderByCreatedDateDesc(Integer boardId, String content, Pageable pageable);
    Page<Post> findAllByBoardIdAndUserIdContainingOrderByCreatedDateDesc(Integer boardId, String userId, Pageable pageable);
    Page<Post> findAllByBoardIdAndUserIdContainingAndIsExternalTrueOrderByCreatedDateDesc(Integer boardId, String userId, Pageable pageable);

    // 마이페이지 통계 및 목록
    Page<Post> findAllByUserIdOrderByCreatedDateDesc(String userId, Pageable pageable);
    long countByUserId(String userId);

    @Query("SELECT SUM(p.postLikeCount) FROM Post p WHERE p.userId = :userId")
    Integer sumPostLikeCountByUserId(@Param("userId") String userId);
}