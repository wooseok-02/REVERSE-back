package com.reverse.nsu.service;

import com.reverse.nsu.dto.NoticeAdminRequestDto;
import com.reverse.nsu.dto.NoticeAdminResponseDto;
import com.reverse.nsu.dto.NoticeListResponseDto;
import com.reverse.nsu.dto.NoticeResponseDto;
import com.reverse.nsu.entity.Post;
import com.reverse.nsu.entity.PostAttached;
import com.reverse.nsu.entity.Users;
import com.reverse.nsu.repository.BoardRepository;
import com.reverse.nsu.repository.PostAttachedRepository;
import com.reverse.nsu.repository.PostRepository;
import com.reverse.nsu.repository.UsersRepository;
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

    private Integer noticeBoardId;

    private Integer getNoticeBoardId() {
        if (noticeBoardId == null) {
            // [해결] .getBoardId()를 호출하여 Integer 타입을 반환하도록 수정
            noticeBoardId = boardRepository.findByBoardName("공지사항")
                    .orElseThrow(() -> new RuntimeException("공지사항 게시판이 존재하지 않습니다."))
                    .getBoardId();
        }
        return noticeBoardId;
    }

    public Page<NoticeListResponseDto> getAll(String category, boolean isLoggedIn, int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Integer boardId = getNoticeBoardId();
        Page<Post> posts;

        if (category != null && !category.trim().isEmpty()) {
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

    public NoticeResponseDto getOne(Integer noticeId, boolean isLoggedIn) {
        Post post = postRepository.findById(noticeId)
                .filter(p -> p.getBoardId().equals(getNoticeBoardId()))
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));

        if (!isLoggedIn && !post.getIsExternal()) {
            throw new SecurityException("FORBIDDEN");
        }

        List<String> imageUrls = getAttachedImageUrls(noticeId);
        return NoticeResponseDto.from(post, imageUrls);
    }

    @Transactional
    public NoticeAdminResponseDto create(NoticeAdminRequestDto dto, String userId) {
        validateDto(dto);
        Post post = Post.createPost(dto, userId, getNoticeBoardId());
        Post savedPost = postRepository.save(post);

        List<String> imageUrls = Collections.emptyList();
        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            saveAttachedImages(savedPost.getPostId(), userId, dto.getImageUrls());
            imageUrls = dto.getImageUrls();
        }
        return new NoticeAdminResponseDto(savedPost, imageUrls);
    }

    @Transactional
    public NoticeAdminResponseDto update(NoticeAdminRequestDto dto, String userId) {
        validateDto(dto);
        Post post = postRepository.findById(dto.getNoticeId())
                .filter(p -> p.getBoardId().equals(getNoticeBoardId()))
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        // [해결] 관리자(1, 2)이거나 작성자 본인일 때만 수정 허용
        if (hasAdminPrivilege(userId) || post.getUserId().equals(userId)) {
            post.update(dto);
            if (dto.getImageUrls() != null) {
                postAttachedRepository.deleteAllByPostId(post.getPostId());
                saveAttachedImages(post.getPostId(), userId, dto.getImageUrls());
            }
        } else {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        return new NoticeAdminResponseDto(post, dto.getImageUrls() != null ? dto.getImageUrls() : Collections.emptyList());
    }

    @Transactional
    public Post delete(Integer noticeId, String userId) {
        Post post = postRepository.findById(noticeId)
                .filter(p -> p.getBoardId().equals(getNoticeBoardId()))
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        if (hasAdminPrivilege(userId) || post.getUserId().equals(userId)) {
            postAttachedRepository.deleteAllByPostId(noticeId);
            postRepository.delete(post);
        } else {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        return post;
    }

    // --- Private Helper Methods ---

    private void validateDto(NoticeAdminRequestDto dto) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty() ||
                dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("MISSING_REQUIRED_FIELDS");
        }
    }

    private void saveAttachedImages(Integer postId, String userId, List<String> urls) {
        urls.forEach(url ->
                postAttachedRepository.save(PostAttached.create(postId, userId, url))
        );
    }

    private List<String> getAttachedImageUrls(Integer postId) {
        return postAttachedRepository.findAllByPostId(postId)
                .stream()
                .map(PostAttached::getAttachedUrl)
                .collect(Collectors.toList());
    }

    private boolean hasAdminPrivilege(String userId) {
        return usersRepository.findById(userId)
                .map(user -> user.getRole().getRoleId() == 1 || user.getRole().getRoleId() == 2)
                .orElse(false);
    }
}