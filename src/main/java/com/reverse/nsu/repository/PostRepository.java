package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Integer> {

    // 1. 기본 목록 조회
    @Query("SELECT p FROM Post p WHERE p.boardId = :boardId ORDER BY p.createdDate DESC")
    Page<Post> findAllByBoardIdOrderByCreatedDateDesc(@Param("boardId") Integer boardId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.boardId = :boardId AND p.isExternal = true ORDER BY p.createdDate DESC")
    Page<Post> findAllByBoardIdAndIsExternalTrueOrderByCreatedDateDesc(@Param("boardId") Integer boardId, Pageable pageable);

    // 2. 카테고리 필터링 조회
    @Query("SELECT p FROM Post p WHERE p.boardId = :boardId AND p.postCategory = :postCategory ORDER BY p.createdDate DESC")
    Page<Post> findAllByBoardIdAndPostCategoryOrderByCreatedDateDesc(@Param("boardId") Integer boardId, @Param("postCategory") String postCategory, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.boardId = :boardId AND p.postCategory = :postCategory AND p.isExternal = true ORDER BY p.createdDate DESC")
    Page<Post> findAllByBoardIdAndPostCategoryAndIsExternalTrueOrderByCreatedDateDesc(@Param("boardId") Integer boardId, @Param("postCategory") String postCategory, Pageable pageable);

    // 3. 검색 기능 (제목)
    @Query("SELECT p FROM Post p WHERE p.boardId = :boardId AND p.postTitle LIKE %:title% ORDER BY p.createdDate DESC")
    Page<Post> findAllByBoardIdAndPostTitleContainingOrderByCreatedDateDesc(@Param("boardId") Integer boardId, @Param("title") String title, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.boardId = :boardId AND p.postTitle LIKE %:title% AND p.isExternal = true ORDER BY p.createdDate DESC")
    Page<Post> findAllByBoardIdAndPostTitleContainingAndIsExternalTrueOrderByCreatedDateDesc(@Param("boardId") Integer boardId, @Param("title") String title, Pageable pageable);

    // 4. 검색 기능 (내용)
    @Query("SELECT p FROM Post p WHERE p.boardId = :boardId AND p.postContents LIKE %:content% ORDER BY p.createdDate DESC")
    Page<Post> findAllByBoardIdAndPostContentsContainingOrderByCreatedDateDesc(@Param("boardId") Integer boardId, @Param("content") String content, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.boardId = :boardId AND p.postContents LIKE %:content% AND p.isExternal = true ORDER BY p.createdDate DESC")
    Page<Post> findAllByBoardIdAndPostContentsContainingAndIsExternalTrueOrderByCreatedDateDesc(@Param("boardId") Integer boardId, @Param("content") String content, Pageable pageable);

    // 5. 검색 기능 (작성자)
    @Query("SELECT p FROM Post p WHERE p.boardId = :boardId AND p.userId LIKE %:userId% ORDER BY p.createdDate DESC")
    Page<Post> findAllByBoardIdAndUserIdContainingOrderByCreatedDateDesc(@Param("boardId") Integer boardId, @Param("userId") String userId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.boardId = :boardId AND p.userId LIKE %:userId% AND p.isExternal = true ORDER BY p.createdDate DESC")
    Page<Post> findAllByBoardIdAndUserIdContainingAndIsExternalTrueOrderByCreatedDateDesc(@Param("boardId") Integer boardId, @Param("userId") String userId, Pageable pageable);

    // 6. 마이페이지 통계 및 목록
    Page<Post> findAllByUserIdOrderByCreatedDateDesc(String userId, Pageable pageable);
    long countByUserId(String userId);

    @Query("SELECT SUM(p.postLikeCount) FROM Post p WHERE p.userId = :userId")
    Integer sumPostLikeCountByUserId(@Param("userId") String userId);
}