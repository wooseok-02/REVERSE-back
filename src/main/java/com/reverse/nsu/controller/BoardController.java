package com.reverse.nsu.controller;

import com.reverse.nsu.dto.ApiResponse;
import com.reverse.nsu.dto.BoardPostListResponseDto;
import com.reverse.nsu.dto.BoardPostResponseDto;
import com.reverse.nsu.service.BoardService;
import com.reverse.nsu.service.R2Service;
import com.reverse.nsu.service.RoleCheckService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/posts/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final R2Service r2Service;
    private final RoleCheckService roleCheckService;

    /**
     * 파일 업로드 (준회원 이상)
     * JwtInterceptor에 의해 토큰 유효성은 이미 검증됨.
     */
    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) throws IOException {

        String userId = (String) request.getAttribute("userId");
        if (!roleCheckService.isAssociateOrAbove(userId)) return forbiddenResponse();
        return ResponseEntity.ok(r2Service.upload(file, "board", true));
    }

    /**
     * 게시글 목록 조회 (준회원 이상)
     */
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "0") int page,
            HttpServletRequest request) {

        String userId = (String) request.getAttribute("userId");
        if (!roleCheckService.isAssociateOrAbove(userId)) return forbiddenResponse();
        return ResponseEntity.ok(ApiResponse.ok(boardService.getAll(page)));
    }

    /**
     * 게시글 단건 조회 (준회원 이상)
     */
    @GetMapping("/{postId}")
    public ResponseEntity<?> getOne(
            @PathVariable Integer postId,
            HttpServletRequest request) {

        String userId = (String) request.getAttribute("userId");
        if (!roleCheckService.isAssociateOrAbove(userId)) return forbiddenResponse();

        try {
            return ResponseEntity.ok(ApiResponse.ok(boardService.getOne(postId)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("NOT_FOUND", "해당 게시글을 찾을 수 없습니다."));
        }
    }

    /**
     * 좋아요 토글 (준회원 이상)
     */
    @PostMapping("/{postId}/like")
    public ResponseEntity<?> toggleLike(
            @PathVariable Integer postId,
            HttpServletRequest request) {

        String userId = (String) request.getAttribute("userId");
        if (!roleCheckService.isAssociateOrAbove(userId)) return forbiddenResponse();

        try {
            boolean liked = boardService.toggleLike(postId, userId);
            String message = liked ? "좋아요를 눌렀습니다." : "좋아요를 취소했습니다.";
            return ResponseEntity.ok(ApiResponse.ok(liked, message));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error("NOT_FOUND", "해당 게시글을 찾을 수 없습니다."));
        }
    }

    private ResponseEntity<?> forbiddenResponse() {
        return ResponseEntity.status(403)
                .body(ApiResponse.error("FORBIDDEN", "준회원 이상만 이용 가능한 서비스입니다."));
    }
}
