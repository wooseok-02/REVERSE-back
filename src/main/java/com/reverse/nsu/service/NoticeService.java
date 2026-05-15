package com.reverse.nsu.service;

import com.reverse.nsu.dto.NoticeAdminRequestDto;
import com.reverse.nsu.dto.NoticeAdminResponseDto;
import com.reverse.nsu.dto.NoticeListResponseDto;
import com.reverse.nsu.dto.NoticeResponseDto;
import com.reverse.nsu.entity.Post;
import com.reverse.nsu.entity.PostAttached;
import com.reverse.nsu.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NoticeService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final PostAttachedRepository postAttachedRepository;
    private final UsersRepository usersRepository;
    private final PostLikeRepository postLikeRepository;

    private Integer noticeBoardId;

    private Integer getNoticeBoardId() {
        if (noticeBoardId == null) {
            noticeBoardId = boardRepository.findByBoardName("공지사항")
                    .orElseThrow(() -> new RuntimeException("공지사항 게시판이 존재하지 않습니다."))
                    .getBoardId();
        }
        return noticeBoardId;
    }

    public Page<NoticeListResponseDto> getAll(String category, boolean isLoggedIn, int page) {
        Pageable pageable = PageRequest.of(page, 6);
        Integer boardId = getNoticeBoardId();
        Page<Post> posts;

        if (category != null && !category.trim().isEmpty() && !category.equals("전체")) {
            posts = isLoggedIn ?
                    postRepository.findAllByBoardIdAndPostCategoryOrderByCreatedDateDesc(boardId, category, pageable) :
                    postRepository.findAllByBoardIdAndPostCategoryAndIsExternalTrueOrderByCreatedDateDesc(boardId, category, pageable);
        } else {
            posts = isLoggedIn ?
                    postRepository.findAllByBoardIdOrderByCreatedDateDesc(boardId, pageable) :
                    postRepository.findAllByBoardIdAndIsExternalTrueOrderByCreatedDateDesc(boardId, pageable);
        }
        return posts.map(NoticeListResponseDto::new);
    }

    public NoticeResponseDto getOne(Integer postId, boolean isLoggedIn) {
        Post post = postRepository.findById(postId)
                .filter(p -> p.getBoardId().equals(getNoticeBoardId()))
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));

        if (!isLoggedIn && !post.getIsExternal()) {
            throw new SecurityException("FORBIDDEN");
        }

        // 객체 내 이미지 리스트 반환
        List<String> imageUrls = post.getImageUrlList();
        return NoticeResponseDto.from(post, imageUrls);
    }

    @Transactional
    public NoticeAdminResponseDto create(NoticeAdminRequestDto dto, String userId) {
        validateDto(dto);
        Post post = Post.createPost(dto, userId, getNoticeBoardId());
        Post savedPost = postRepository.save(post);

        // 이미지 저장
        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            saveAttachedImages(savedPost, userId, dto.getImageUrls());
        }

        // [수정] 1차 캐시 문제 해결을 위해 dto의 URL을 직접 사용
        List<String> responseUrls = dto.getImageUrls() != null ? dto.getImageUrls() : Collections.emptyList();
        return new NoticeAdminResponseDto(savedPost, responseUrls);
    }

    @Transactional
    public NoticeAdminResponseDto update(NoticeAdminRequestDto dto, String userId) {
        validateDto(dto);

        Post post = postRepository.findById(dto.getPostId())
                .filter(p -> p.getBoardId().equals(getNoticeBoardId()))
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        if (hasAdminPrivilege(userId) || post.getUserId().equals(userId)) {
            post.update(dto);
            if (dto.getImageUrls() != null) {
                // [수정] 리포지토리 메서드명 변경 반영 (PostId -> Post 객체)
                postAttachedRepository.deleteAllByPost(post);
                saveAttachedImages(post, userId, dto.getImageUrls());
            }
        } else {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        // [수정] 업데이트 직후 최신 URL 리스트 반환
        List<String> responseUrls = dto.getImageUrls() != null ? dto.getImageUrls() : Collections.emptyList();
        return new NoticeAdminResponseDto(post, responseUrls);
    }

    @Transactional
    public Post delete(Integer postId, String userId) {
        Post post = postRepository.findById(postId)
                .filter(p -> p.getBoardId().equals(getNoticeBoardId()))
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        if (hasAdminPrivilege(userId) || post.getUserId().equals(userId)) {
            // [테스트 핵심] 자식 데이터부터 순서대로 지워야 함!
            postLikeRepository.deleteAllByPost(post);    // 1. 좋아요 먼저
            postAttachedRepository.deleteAllByPost(post); // 2. 이미지 먼저

            postRepository.delete(post);                  // 3. 마지막에 게시글
        } else {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        return post;
    }

    private void validateDto(NoticeAdminRequestDto dto) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty() ||
                dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("MISSING_REQUIRED_FIELDS");
        }
    }

    private void saveAttachedImages(Post post, String userId, List<String> urls) {
        urls.forEach(url ->
                postAttachedRepository.save(PostAttached.create(post, userId, url))
        );
    }

    private boolean hasAdminPrivilege(String userId) {
        return usersRepository.findById(userId)
                .map(user -> user.getRole().getRoleId() == 1 || user.getRole().getRoleId() == 2)
                .orElse(false);
    }
}