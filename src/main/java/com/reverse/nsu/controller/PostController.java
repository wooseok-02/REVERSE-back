package com.reverse.nsu.controller;

import com.reverse.nsu.dto.BoardPostListResponseDto;
import com.reverse.nsu.dto.NoticeAdminRequestDto;
import com.reverse.nsu.entity.Post;
import com.reverse.nsu.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 중복되는 userId 추출 로직
    private String resolveUserId(HttpServletRequest request) {
        return (String) request.getAttribute("userId");
    }

    /**
     * 1. 게시글 작성
     */
    @PostMapping("/{boardId}")
    public ResponseEntity<?> createPost(
            @PathVariable("boardId") Integer boardId,
            @RequestBody NoticeAdminRequestDto dto,
            HttpServletRequest request) {

        String userId = resolveUserId(request);
        if (userId == null) return unauthorizedResponse();

        Integer postId = postService.createPost(dto, userId, boardId);
        return ResponseEntity.ok(Map.of("status", "success", "postId", postId, "message", "등록되었습니다."));
    }

    /**
     * 2. 게시글 수정
     */
    @PatchMapping("/post/{postId}")
    public ResponseEntity<?> updatePost(
            @PathVariable("postId") Integer postId,
            @RequestBody NoticeAdminRequestDto dto,
            HttpServletRequest request) {

        String userId = resolveUserId(request);
        if (userId == null) return unauthorizedResponse();

        try {
            postService.updatePost(postId, dto, userId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "수정되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 3. 게시글 삭제
     */
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<?> deletePost(
            @PathVariable("postId") Integer postId,
            HttpServletRequest request) {

        String userId = resolveUserId(request);
        if (userId == null) return unauthorizedResponse();

        try {
            postService.deletePost(postId, userId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 4. 전체 게시판 목록 및 검색
     */
    @GetMapping("/{boardId}")
    public ResponseEntity<?> getPosts(
            @PathVariable("boardId") Integer boardId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 6) Pageable pageable,
            HttpServletRequest request) {

        String userId = resolveUserId(request);

        // 서비스 호출 및 DTO 변환을 한 줄로 정리
        Page<BoardPostListResponseDto> response = postService
                .searchPosts(boardId, category, type, keyword, pageable, userId)
                .map(BoardPostListResponseDto::new);

        return ResponseEntity.ok(response);
    }

    /**
     * 5. 내 통계 정보 조회
     */
    @GetMapping("/my/stats")
    public ResponseEntity<?> getMyStats(HttpServletRequest request) {
        String userId = resolveUserId(request);
        if (userId == null) return unauthorizedResponse();

        return ResponseEntity.ok(postService.getMyPostStats(userId));
    }

    /**
     * 6. 나의 게시글 목록 확인
     */
    @GetMapping("/my/posts")
    public ResponseEntity<?> getMyPosts(
            @PageableDefault(size = 6) Pageable pageable,
            HttpServletRequest request) {

        String userId = resolveUserId(request);
        if (userId == null) return unauthorizedResponse();

        Page<BoardPostListResponseDto> response = postService
                .getMyPosts(userId, pageable)
                .map(BoardPostListResponseDto::new);

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<?> unauthorizedResponse() {
        return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요한 서비스입니다."));
    }
}