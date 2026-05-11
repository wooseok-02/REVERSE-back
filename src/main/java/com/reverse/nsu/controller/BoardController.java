package com.reverse.nsu.controller;

import com.reverse.nsu.dto.ApiResponse;
import com.reverse.nsu.dto.BoardPostListResponseDto;
import com.reverse.nsu.dto.BoardPostResponseDto;
import com.reverse.nsu.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BoardPostListResponseDto>>> getAll(
            @RequestParam(defaultValue = "0") int page
    ) {
        return ResponseEntity.ok(ApiResponse.ok(boardService.getAll(page)));
    }

    // 단건 조회 (게시글 내용 확인)
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<BoardPostResponseDto>> getOne(
            @PathVariable Integer postId
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(boardService.getOne(postId)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("NOT_FOUND", "해당 게시글을 찾을 수 없습니다."));
        }
    }

    // BRD07 - 좋아요 토글
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Boolean>> toggleLike(
            @PathVariable Integer postId,
            HttpServletRequest request
    ) {
        try {
            String userId = (String) request.getAttribute("userId");
            boolean liked = boardService.toggleLike(postId, userId);
            String message = liked ? "좋아요를 눌렀습니다." : "좋아요를 취소했습니다.";
            return ResponseEntity.ok(ApiResponse.ok(liked, message));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("NOT_FOUND", "해당 게시글을 찾을 수 없습니다."));
        }
    }
}
