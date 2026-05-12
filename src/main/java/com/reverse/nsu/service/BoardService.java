package com.reverse.nsu.service;

import com.reverse.nsu.dto.BoardPostListResponseDto;
import com.reverse.nsu.dto.BoardPostResponseDto;
import com.reverse.nsu.entity.Post;
import com.reverse.nsu.entity.PostAttached;
import com.reverse.nsu.entity.PostLike;
import com.reverse.nsu.repository.BoardRepository;
import com.reverse.nsu.repository.PostAttachedRepository;
import com.reverse.nsu.repository.PostLikeRepository;
import com.reverse.nsu.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final PostAttachedRepository postAttachedRepository;
    private final PostLikeRepository postLikeRepository;

    private Integer boardId;

    private Integer getBoardId() {
        if (boardId == null) {
            boardId = boardRepository.findByBoardName("게시판")
                    .orElseThrow(() -> new RuntimeException("게시판이 존재하지 않습니다."))
                    .getBoardId();
        }
        return boardId;
    }

    // 목록 조회 (10개 페이징)
    public Page<BoardPostListResponseDto> getAll(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return postRepository.findAllByBoardIdOrderByCreatedDateDesc(getBoardId(), pageable)
                .map(BoardPostListResponseDto::new);
    }

    // 단건 조회
    public BoardPostResponseDto getOne(Integer postId) {
        Post post = postRepository.findById(postId)
                .filter(p -> p.getBoardId().equals(getBoardId()))
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));

        List<String> imageUrls = postAttachedRepository.findAllByPostId(postId)
                .stream().map(PostAttached::getAttachedUrl).collect(Collectors.toList());

        return BoardPostResponseDto.from(post, imageUrls);
    }

    // BRD07 - 좋아요 토글 (true = 좋아요 추가, false = 좋아요 취소)
    @Transactional
    public boolean toggleLike(Integer postId, String userId) {
        Post post = postRepository.findById(postId)
                .filter(p -> p.getBoardId().equals(getBoardId()))
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));

        Optional<PostLike> existing = postLikeRepository.findByUserIdAndPostId(userId, postId);
        if (existing.isPresent()) {
            postLikeRepository.delete(existing.get());
            post.decrementLikeCount();
            postRepository.save(post);
            return false; // 좋아요 취소
        } else {
            postLikeRepository.save(PostLike.create(postId, userId));
            post.incrementLikeCount();
            postRepository.save(post);
            return true; // 좋아요 추가
        }
    }
}
