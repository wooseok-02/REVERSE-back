package com.reverse.nsu.controller;

import com.reverse.nsu.dto.ApiResponse;
import com.reverse.nsu.dto.CommentRequestDto;
import com.reverse.nsu.dto.CommentResponseDto;
import com.reverse.nsu.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/board")
@RequiredArgsConstructor
public class BoardCommentController {

    private final CommentService commentService;

    // 댓글 목록 조회
    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponseDto>>> getComments(
            @PathVariable Integer postId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(commentService.getComments(postId)));
    }

    // BRD04 - 댓글 작성
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponseDto>> writeComment(
            @PathVariable Integer postId,
            @RequestBody CommentRequestDto dto,
            HttpServletRequest request
    ) {
        try {
            String userId = (String) request.getAttribute("userId");
            return ResponseEntity.status(201)
                    .body(ApiResponse.ok(commentService.writeComment(postId, userId, dto), "댓글이 작성되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("NOT_FOUND", "게시글을 찾을 수 없습니다."));
        }
    }

    // BRD05 - 대댓글 작성
    @PostMapping("/{postId}/comments/{commentId}/reply")
    public ResponseEntity<ApiResponse<CommentResponseDto>> writeReply(
            @PathVariable Integer postId,
            @PathVariable Integer commentId,
            @RequestBody CommentRequestDto dto,
            HttpServletRequest request
    ) {
        try {
            String userId = (String) request.getAttribute("userId");
            return ResponseEntity.status(201)
                    .body(ApiResponse.ok(commentService.writeReply(postId, commentId, userId, dto), "대댓글이 작성되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("NOT_FOUND", e.getMessage()));
        }
    }

    // BRD06 - 댓글 수정
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> updateComment(
            @PathVariable Integer commentId,
            @RequestBody CommentRequestDto dto,
            HttpServletRequest request
    ) {
        try {
            String userId = (String) request.getAttribute("userId");
            return ResponseEntity.ok(ApiResponse.ok(commentService.updateComment(commentId, userId, dto), "댓글이 수정되었습니다."));
        } catch (SecurityException e) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("FORBIDDEN", "본인의 댓글만 수정할 수 있습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("NOT_FOUND", "댓글을 찾을 수 없습니다."));
        }
    }

    // BRD06 - 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Integer commentId,
            HttpServletRequest request
    ) {
        try {
            String userId = (String) request.getAttribute("userId");
            commentService.deleteComment(commentId, userId);
            return ResponseEntity.ok(ApiResponse.ok(null, "댓글이 삭제되었습니다."));
        } catch (SecurityException e) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("FORBIDDEN", "본인의 댓글만 삭제할 수 있습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("NOT_FOUND", "댓글을 찾을 수 없습니다."));
        }
    }
}
