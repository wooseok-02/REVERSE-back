package com.reverse.nsu.controller;

import com.reverse.nsu.dto.ApiResponse;
import com.reverse.nsu.entity.Board;
import com.reverse.nsu.service.BoardAdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class BoardAdminController {

    private final BoardAdminService boardAdminService;

    private String resolveUserId(HttpServletRequest request) {
        return (String) request.getAttribute("userId");
    }

    @SuppressWarnings("unchecked")
    private <T> ResponseEntity<ApiResponse<T>> forbiddenResponse() {
        return (ResponseEntity<ApiResponse<T>>) (Object) ResponseEntity.status(403)
                .body(ApiResponse.error("FORBIDDEN", "관리자만 접근 가능합니다."));
    }

    // 게시글 강제 삭제
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<Void>> forceDeletePost(
            @PathVariable Integer postId,
            HttpServletRequest request) {
        String userId = resolveUserId(request);
        if (userId == null) return ResponseEntity.status(401).body(ApiResponse.error("UNAUTHORIZED", "로그인이 필요합니다."));
        if (!boardAdminService.isAdmin(userId)) return forbiddenResponse();
        try {
            boardAdminService.forceDeletePost(postId);
            return ResponseEntity.ok(ApiResponse.ok(null, "게시글이 삭제되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(ApiResponse.error("NOT_FOUND", e.getMessage()));
        }
    }

    // 게시판 목록 조회
    @GetMapping("/boards")
    public ResponseEntity<ApiResponse<List<Board>>> getAllBoards(HttpServletRequest request) {
        String userId = resolveUserId(request);
        if (userId == null) return ResponseEntity.status(401).body(ApiResponse.error("UNAUTHORIZED", "로그인이 필요합니다."));
        if (!boardAdminService.isAdmin(userId)) return forbiddenResponse();
        return ResponseEntity.ok(ApiResponse.ok(boardAdminService.getAllBoards()));
    }

    // 게시판 추가
    @PostMapping("/boards")
    public ResponseEntity<ApiResponse<Board>> createBoard(
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        String userId = resolveUserId(request);
        if (userId == null) return ResponseEntity.status(401).body(ApiResponse.error("UNAUTHORIZED", "로그인이 필요합니다."));
        if (!boardAdminService.isAdmin(userId)) return forbiddenResponse();
        try {
            Board board = boardAdminService.createBoard(body.get("boardName"), body.get("boardDescription"));
            return ResponseEntity.status(201).body(ApiResponse.ok(board, "게시판이 추가되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(ApiResponse.error("BAD_REQUEST", e.getMessage()));
        }
    }

    // 게시판 삭제
    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(
            @PathVariable Integer boardId,
            HttpServletRequest request) {
        String userId = resolveUserId(request);
        if (userId == null) return ResponseEntity.status(401).body(ApiResponse.error("UNAUTHORIZED", "로그인이 필요합니다."));
        if (!boardAdminService.isAdmin(userId)) return forbiddenResponse();
        try {
            boardAdminService.deleteBoard(boardId);
            return ResponseEntity.ok(ApiResponse.ok(null, "게시판이 삭제되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(ApiResponse.error("NOT_FOUND", e.getMessage()));
        }
    }
}
