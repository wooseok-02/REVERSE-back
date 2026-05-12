package com.reverse.nsu.repository;

import com.reverse.nsu.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {

    // 로그인 - 전체/카테고리
    Page<Post> findAllByBoardIdOrderByCreatedDateDesc(Integer boardId, Pageable pageable);
    Page<Post> findAllByBoardIdAndPostCategoryOrderByCreatedDateDesc(Integer boardId, String postCategory, Pageable pageable);

    // 비로그인 - 외부(isExternal=true)만
    Page<Post> findAllByBoardIdAndIsExternalTrueOrderByCreatedDateDesc(Integer boardId, Pageable pageable);
    Page<Post> findAllByBoardIdAndPostCategoryAndIsExternalTrueOrderByCreatedDateDesc(Integer boardId, String postCategory, Pageable pageable);
}