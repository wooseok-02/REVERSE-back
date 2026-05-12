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

    // 댓글 목록 조회 (계층형 - 원댓글 + 대댓글)
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getComments(Integer postId) {
        List<Comment> all = commentRepository.findAllByPostIdOrderByCreatedDateAsc(postId);

        // 원댓글 Map
        Map<Integer, CommentResponseDto> parentMap = all.stream()
                .filter(c -> c.getParentCommentId() == null)
                .collect(Collectors.toMap(
                        Comment::getCommentId,
                        CommentResponseDto::new,
                        (a, b) -> a,
                        java.util.LinkedHashMap::new
                ));

        // 대댓글 → 원댓글에 붙이기
        all.stream()
                .filter(c -> c.getParentCommentId() != null)
                .forEach(c -> {
                    CommentResponseDto parent = parentMap.get(c.getParentCommentId());
                    if (parent != null) parent.addReply(new CommentResponseDto(c));
                });

        return List.copyOf(parentMap.values());
    }

    // BRD04 - 댓글 작성
    @Transactional
    public CommentResponseDto writeComment(Integer postId, String userId, CommentRequestDto dto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));

        Comment comment = commentRepository.save(Comment.create(postId, userId, dto.getCommentDetail()));
        post.incrementCommentCount();
        postRepository.save(post);
        return new CommentResponseDto(comment);
    }

    // BRD05 - 대댓글 작성
    @Transactional
    public CommentResponseDto writeReply(Integer postId, Integer parentCommentId, String userId, CommentRequestDto dto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));

        commentRepository.findById(parentCommentId)
                .filter(c -> c.getParentCommentId() == null) // 대댓글에 대댓글 방지
                .orElseThrow(() -> new IllegalArgumentException("INVALID_PARENT"));

        Comment reply = commentRepository.save(Comment.createReply(postId, userId, parentCommentId, dto.getCommentDetail()));
        post.incrementCommentCount();
        postRepository.save(post);
        return new CommentResponseDto(reply);
    }

    // BRD06 - 댓글 수정
    @Transactional
    public CommentResponseDto updateComment(Integer commentId, String userId, CommentRequestDto dto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));

        if (!comment.getUserId().equals(userId)) {
            throw new SecurityException("FORBIDDEN");
        }

        comment.update(dto.getCommentDetail());
        return new CommentResponseDto(commentRepository.save(comment));
    }

    // BRD06 - 댓글 삭제
    @Transactional
    public void deleteComment(Integer commentId, String userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));

        if (!comment.getUserId().equals(userId)) {
            throw new SecurityException("FORBIDDEN");
        }

        Post post = postRepository.findById(comment.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));

        commentRepository.delete(comment);
        post.decrementCommentCount();
        postRepository.save(post);
    }
}
