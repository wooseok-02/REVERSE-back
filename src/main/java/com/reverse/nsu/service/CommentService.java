package com.reverse.nsu.service;

import com.reverse.nsu.dto.CommentRequestDto;
import com.reverse.nsu.dto.CommentResponseDto;
import com.reverse.nsu.entity.Comment;
import com.reverse.nsu.entity.Post;
import com.reverse.nsu.repository.CommentRepository;
import com.reverse.nsu.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final RoleCheckService roleCheckService;

    /**
     * 댓글 목록 조회 (계층형 - 원댓글 + 대댓글)
     */
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getComments(Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));

        // 게시글에 달린 모든 댓글 조회
        List<Comment> all = commentRepository.findAllByPostOrderByCreatedDateAsc(post);

        // 1. 원댓글(parent가 null인 경우)들만 먼저 DTO로 변환하여 Map 구성
        Map<Integer, CommentResponseDto> parentMap = all.stream()
                .filter(c -> c.getParent() == null) // [수정] getParentCommentId() 대신 getParent() 사용
                .collect(Collectors.toMap(
                        Comment::getCommentId,
                        CommentResponseDto::new,
                        (a, b) -> a,
                        java.util.LinkedHashMap::new
                ));

        // 2. 대댓글(parent가 있는 경우)들을 찾아서 부모 DTO의 replies 리스트에 추가
        all.stream()
                .filter(c -> c.getParent() != null) // [수정]
                .forEach(c -> {
                    // c.getParent().getCommentId()를 통해 부모 ID를 가져옴
                    CommentResponseDto parentDto = parentMap.get(c.getParent().getCommentId());
                    if (parentDto != null) {
                        parentDto.addReply(new CommentResponseDto(c));
                    }
                });

        return List.copyOf(parentMap.values());
    }

    /**
     * 원댓글 작성
     */
    @Transactional
    public CommentResponseDto writeComment(Integer postId, String userId, CommentRequestDto dto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));

        Comment comment = commentRepository.save(Comment.create(post, userId, dto.getCommentDetail()));

        post.incrementCommentCount();
        return new CommentResponseDto(comment);
    }

    /**
     * 대댓글 작성
     */
    @Transactional
    public CommentResponseDto writeReply(Integer postId, Integer parentCommentId, String userId, CommentRequestDto dto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));

        // 부모 댓글 객체를 직접 찾음
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));

        // 대댓글의 대댓글 방지 (부모가 이미 대댓글인 경우 차단)
        if (parentComment.getParent() != null) {
            throw new IllegalArgumentException("대댓글에는 답글을 달 수 없습니다.");
        }

        // [수정] 부모 ID 대신 부모 '객체'를 전달
        Comment reply = commentRepository.save(Comment.createReply(post, userId, parentComment, dto.getCommentDetail()));

        post.incrementCommentCount();
        return new CommentResponseDto(reply);
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public CommentResponseDto updateComment(Integer commentId, String userId, CommentRequestDto dto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));

        if (!comment.getUserId().equals(userId) && !roleCheckService.isAdmin(userId)) {
            throw new SecurityException("FORBIDDEN");
        }

        comment.update(dto.getCommentDetail());
        return new CommentResponseDto(comment);
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(Integer commentId, String userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));

        if (!comment.getUserId().equals(userId) && !roleCheckService.isAdmin(userId)) {
            throw new SecurityException("FORBIDDEN");
        }

        Post post = comment.getPost();

        // 자식(대댓글)이 있는 경우 cascade 설정에 의해 자동 삭제되거나,
        // 로직에 따라 처리 (여기서는 JPA가 자동 처리하도록 함)
        commentRepository.delete(comment);
        post.decrementCommentCount();
    }
}