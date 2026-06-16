package com.reverse.nsu.controller;

import com.reverse.nsu.dto.ApiResponse;
import com.reverse.nsu.dto.CommentRequestDto;
import com.reverse.nsu.dto.CommentResponseDto;
import com.reverse.nsu.service.CommentService;
import com.reverse.nsu.service.RoleCheckService;
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
    private final RoleCheckService roleCheckService;

    /**
     * 댓글 목록 조회 (준회원 이상)
     * JwtInterceptor에 의해 토큰 유효성은 이미 검증됨.
     */
    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> getComments(
            @PathVariable Integer postId,
            HttpServletRequest request) {

        String userId = (String) request.getAttribute("userId");
        if (!roleCheckService.isAssociateOrAbove(userId)) return forbiddenResponse();
        return ResponseEntity.ok(ApiResponse.ok(commentService.getComments(postId)));
    }

    /**
     * 댓글 작성 (준회원 이상)
     */
    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> writeComment(
            @PathVariable Integer postId,
            @RequestBody CommentRequestDto dto,
            HttpServletRequest request) {

        String userId = (String) request.getAttribute("userId");
        if (!roleCheckService.isAssociateOrAbove(userId)) return forbiddenResponse();

        try {
            return ResponseEntity.status(201)
                    .body(ApiResponse.ok(commentService.writeComment(postId, userId, dto), "댓글이 작성되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("NOT_FOUND", "게시글을 찾을 수 없습니다."));
        }
    }

    /**
     * 대댓글 작성 (준회원 이상)
     */
    @PostMapping("/{postId}/comments/{commentId}/reply")
    public ResponseEntity<?> writeReply(
            @PathVariable Integer postId,
            @PathVariable Integer commentId,
            @RequestBody CommentRequestDto dto,
            HttpServletRequest request) {

        String userId = (String) request.getAttribute("userId");
        if (!roleCheckService.isAssociateOrAbove(userId)) return forbiddenResponse();

        try {
            return ResponseEntity.status(201)
                    .body(ApiResponse.ok(commentService.writeReply(postId, commentId, userId, dto), "대댓글이 작성되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("NOT_FOUND", e.getMessage()));
        }
    }

    /**
     * 댓글 수정 (준회원 이상, 본인 또는 관리자)
     */
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable Integer commentId,
            @RequestBody CommentRequestDto dto,
            HttpServletRequest request) {

        String userId = (String) request.getAttribute("userId");
        if (!roleCheckService.isAssociateOrAbove(userId)) return forbiddenResponse();

        try {
            return ResponseEntity.ok(ApiResponse.ok(commentService.updateComment(commentId, userId, dto), "댓글이 수정되었습니다."));
        } catch (SecurityException e) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("FORBIDDEN", "본인의 댓글만 수정할 수 있습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("NOT_FOUND", "댓글을 찾을 수 없습니다."));
        }
    }

    /**
     * 댓글 삭제 (준회원 이상, 본인 또는 관리자)
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Integer commentId,
            HttpServletRequest request) {

        String userId = (String) request.getAttribute("userId");
        if (!roleCheckService.isAssociateOrAbove(userId)) return forbiddenResponse();

        try {
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

    private ResponseEntity<?> forbiddenResponse() {
        return ResponseEntity.status(403)
                .body(ApiResponse.error("FORBIDDEN", "준회원 이상만 이용 가능한 서비스입니다."));
    }
}
