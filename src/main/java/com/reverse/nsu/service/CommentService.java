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
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));

        // [수정] postId 숫자 대신 post 객체를 넘겨 조회 (Repository 메서드명 확인 필요)
        List<Comment> all = commentRepository.findAllByPostOrderByCreatedDateAsc(post);

        // 원댓글 Map 구성
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

        // [수정] Comment.create에 postId 대신 post 객체 전달
        Comment comment = commentRepository.save(Comment.create(post, userId, dto.getCommentDetail()));

        post.incrementCommentCount();
        // postRepository.save(post); // Dirty Checking으로 자동 반영되므로 생략 가능
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

        // [수정] Comment.createReply에 postId 대신 post 객체 전달
        Comment reply = commentRepository.save(Comment.createReply(post, userId, parentCommentId, dto.getCommentDetail()));

        post.incrementCommentCount();
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
        return new CommentResponseDto(comment); // save 호출 없이도 Dirty Checking으로 업데이트됨
    }

    // BRD06 - 댓글 삭제
    @Transactional
    public void deleteComment(Integer commentId, String userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));

        if (!comment.getUserId().equals(userId)) {
            throw new SecurityException("FORBIDDEN");
        }

        // [수정] comment.getPost()를 통해 바로 Post 객체에 접근 가능
        Post post = comment.getPost();

        commentRepository.delete(comment);
        post.decrementCommentCount();
    }
}