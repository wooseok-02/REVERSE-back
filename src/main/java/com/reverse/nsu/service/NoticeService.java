package com.reverse.nsu.service;

import com.reverse.nsu.dto.*;
import com.reverse.nsu.entity.Post;
import com.reverse.nsu.entity.PostAttached;
import com.reverse.nsu.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class NoticeService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final PostAttachedRepository postAttachedRepository;

    private Integer noticeBoardId;

    private Integer getNoticeBoardId() {
        if (noticeBoardId == null) {
            noticeBoardId = boardRepository.findByBoardName("공지사항")
                    .orElseThrow(() -> new RuntimeException("공지사항 게시판이 존재하지 않습니다."))
                    .getBoardId();
        }
        return noticeBoardId;
    }

    // 목록 조회 (카테고리 필터 + 로그인 여부 + 6개 페이징)
    public Page<NoticeListResponseDto> getAll(String category, boolean isLoggedIn, int page) {
        Pageable pageable = PageRequest.of(page, 6);
        Integer boardId = getNoticeBoardId();
        boolean isAll = category == null || category.equals("전체");

        Page<Post> posts;
        if (isLoggedIn) {
            posts = isAll
                ? postRepository.findAllByBoardIdOrderByCreatedDateDesc(boardId, pageable)
                : postRepository.findAllByBoardIdAndPostCategoryOrderByCreatedDateDesc(boardId, category, pageable);
        } else {
            posts = isAll
                ? postRepository.findAllByBoardIdAndIsExternalTrueOrderByCreatedDateDesc(boardId, pageable)
                : postRepository.findAllByBoardIdAndPostCategoryAndIsExternalTrueOrderByCreatedDateDesc(boardId, category, pageable);
        }
        return posts.map(NoticeListResponseDto::new);
    }

    // 단건 조회
    public NoticeResponseDto getOne(Integer noticeId, boolean isLoggedIn) {
        Post post = postRepository.findById(noticeId)
                .filter(p -> p.getBoardId().equals(getNoticeBoardId()))
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));

        if (!isLoggedIn && !post.getIsExternal())
            throw new SecurityException("FORBIDDEN");

        List<String> imageUrls = postAttachedRepository.findAllByPostId(noticeId)
                .stream().map(PostAttached::getAttachedUrl).collect(Collectors.toList());

        return NoticeResponseDto.from(post, imageUrls);
    }

    // 등록
    @Transactional
    public NoticeAdminResponseDto create(NoticeAdminRequestDto dto, String userId) {
        if (dto.getTitle() == null || dto.getContent() == null)
            throw new IllegalArgumentException("MISSING_FIELD");

        Post post = postRepository.save(Post.createNotice(dto, userId, getNoticeBoardId()));

        if (dto.getImageUrls() != null) {
            dto.getImageUrls().forEach(url ->
                postAttachedRepository.save(PostAttached.create(post.getPostId(), userId, url)));
        }
        return new NoticeAdminResponseDto(post.getPostId());
    }

    // 수정
    @Transactional
    public NoticeAdminResponseDto update(NoticeAdminRequestDto dto, String userId) {
        if (dto.getTitle() == null || dto.getContent() == null)
            throw new IllegalArgumentException("MISSING_FIELD");

        Post post = postRepository.findById(dto.getNoticeId())
                .filter(p -> p.getBoardId().equals(getNoticeBoardId()))
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));
        post.update(dto);

        // 이미지 교체 (기존 삭제 후 재저장)
        if (dto.getImageUrls() != null) {
            postAttachedRepository.deleteAllByPostId(post.getPostId());
            dto.getImageUrls().forEach(url ->
                postAttachedRepository.save(PostAttached.create(post.getPostId(), userId, url)));
        }
        return new NoticeAdminResponseDto(postRepository.save(post).getPostId());
    }

    // 삭제
    @Transactional
    public Integer delete(Integer noticeId) {
        Post post = postRepository.findById(noticeId)
                .filter(p -> p.getBoardId().equals(getNoticeBoardId()))
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));
        postAttachedRepository.deleteAllByPostId(noticeId); // 이미지도 함께 삭제
        postRepository.delete(post);
        return noticeId;
    }
}