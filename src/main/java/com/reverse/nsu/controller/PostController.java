package com.reverse.nsu.controller;

import com.reverse.nsu.dto.NoticeAdminRequestDto;
import com.reverse.nsu.entity.Post;
import com.reverse.nsu.repository.UsersRepository;
import com.reverse.nsu.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UsersRepository usersRepository; // 유저 존재 확인을 위해 주입

    /**
     * 유저 식별 및 DB 존재 여부 로직
     */
    private String resolveUserId(HttpServletRequest request, String debugUserId) {
        // 1. 인터셉터가 넣어준 ID 확인
        String userId = (String) request.getAttribute("userId");

        // 2. 인터셉터에 없으면 디버그용 헤더 확인
        if (userId == null) userId = debugUserId;

        // 3. 아이디가 아예 없거나, DB에 존재하지 않는 아이디면 null 반환
        if (userId == null || userId.trim().isEmpty() || !usersRepository.existsById(userId)) {
            return null;
        }

        return userId;
    }

    /**
     * 1. 게시글 작성
     */
    @PostMapping("/{boardId}")
    public ResponseEntity<?> createPost(
            @PathVariable Integer boardId,
            @RequestBody NoticeAdminRequestDto dto,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-User-Id", required = false) String debugUserId,
            HttpServletRequest request) {

        String userId = resolveUserId(request, debugUserId);
        if (userId == null) return unauthorizedResponse();

        Integer postId = postService.createPost(dto, userId, boardId);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "postId", postId,
                "message", "등록되었습니다."
        ));
    }

    /**
     * 2. 게시글 수정
     */
    @PatchMapping("/post/{postId}")
    public ResponseEntity<?> updatePost(
            @PathVariable Integer postId,
            @RequestBody NoticeAdminRequestDto dto,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-User-Id", required = false) String debugUserId,
            HttpServletRequest request) {

        String userId = resolveUserId(request, debugUserId);
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
            @PathVariable Integer postId,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-User-Id", required = false) String debugUserId,
            HttpServletRequest request) {

        String userId = resolveUserId(request, debugUserId);
        if (userId == null) return unauthorizedResponse();

        try {
            postService.deletePost(postId, userId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 4. 전체 게시판 목록 및 검색 (비로그인 허용)
     */
    @GetMapping("/{boardId}")
    public ResponseEntity<?> getPosts(
            @PathVariable Integer boardId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-User-Id", required = false) String debugUserId,
            HttpServletRequest request,
            @PageableDefault(size = 10) Pageable pageable) {

        // 목록 조시는 로그인 체크가 실패해도 null인 상태로 진행 (비로그인 조회 허용)
        String userId = resolveUserId(request, debugUserId);

        Page<Post> posts = postService.searchPosts(boardId, category, type, keyword, pageable, userId);
        return ResponseEntity.ok(posts);
    }

    /**
     * 5. 내 통계 정보 조회
     */
    @GetMapping("/my/stats")
    public ResponseEntity<?> getMyStats(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-User-Id", required = false) String debugUserId,
            HttpServletRequest request) {

        String userId = resolveUserId(request, debugUserId);
        if (userId == null) return unauthorizedResponse();

        Map<String, Object> stats = postService.getMyPostStats(userId);
        return ResponseEntity.ok(stats);
    }

    /**
     * 6. 나의 게시글 목록 확인
     */
    @GetMapping("/my/posts")
    public ResponseEntity<?> getMyPosts(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "X-User-Id", required = false) String debugUserId,
            HttpServletRequest request,
            @PageableDefault(size = 7) Pageable pageable) {

        String userId = resolveUserId(request, debugUserId);
        if (userId == null) return unauthorizedResponse();

        Page<Post> myPosts = postService.getMyPosts(userId, pageable);
        return ResponseEntity.ok(myPosts);
    }

    private ResponseEntity<?> unauthorizedResponse() {
        return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요한 서비스입니다."));
    }
}