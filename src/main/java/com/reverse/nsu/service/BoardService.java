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
@Transactional(readOnly = true) // 기본적으로 읽기 전용 설정
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

    // 목록 조회
    public Page<BoardPostListResponseDto> getAll(int page) {
        Pageable pageable = PageRequest.of(page, 6);
        return postRepository.findAllByBoardIdOrderByCreatedDateDesc(getBoardId(), pageable)
                .map(BoardPostListResponseDto::new);
    }

    // 단건 조회
    public BoardPostResponseDto getOne(Integer postId) {
        Post post = postRepository.findById(postId)
                .filter(p -> p.getBoardId().equals(getBoardId()))
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));

        // lazy 로딩 문제를 피하기 위해 postAttachedRepository로 직접 조회
        List<String> imageUrls = postAttachedRepository.findAllByPost(post)
                .stream()
                .map(PostAttached::getAttachedUrl)
                .collect(Collectors.toList());

        return BoardPostResponseDto.from(post, imageUrls);
    }

    // BRD07 - 좋아요 토글
    @Transactional
    public boolean toggleLike(Integer postId, String userId) {
        Post post = postRepository.findById(postId)
                .filter(p -> p.getBoardId().equals(getBoardId()))
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));

        // [수정] PostId(Integer) 대신 Post 객체 자체를 넘겨서 조회하도록 통일합니다.
        // PostLikeRepository에 findByUserIdAndPost(String userId, Post post) 가 있어야 합니다.
        Optional<PostLike> existing = postLikeRepository.findByUserIdAndPost(userId, post);

        if (existing.isPresent()) {
            postLikeRepository.delete(existing.get());
            post.decrementLikeCount();
            return false;
        } else {
            postLikeRepository.save(PostLike.create(post, userId));
            post.incrementLikeCount();
            return true;
        }
    }
}