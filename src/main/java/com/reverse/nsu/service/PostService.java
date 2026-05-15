package com.reverse.nsu.service;

import com.reverse.nsu.dto.NoticeAdminRequestDto;
import com.reverse.nsu.entity.Post;
import com.reverse.nsu.entity.PostAttached;
import com.reverse.nsu.repository.PostAttachedRepository; // 추가 필요
import com.reverse.nsu.repository.PostRepository;
import com.reverse.nsu.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UsersRepository usersRepository;
    private final PostAttachedRepository postAttachedRepository; // 추가

    /**
     * 게시글 작성 (이미지 저장 로직 추가)
     */
    @Transactional
    public Integer createPost(NoticeAdminRequestDto dto, String userId, Integer boardId) {
        validateDto(dto);
        Post post = Post.createPost(dto, userId, boardId);
        Post savedPost = postRepository.save(post);

        // [추가] 종호 님이 보낸 이미지 URL 리스트가 있다면 저장
        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            List<PostAttached> images = dto.getImageUrls().stream()
                    .map(url -> PostAttached.create(savedPost, userId, url))
                    .collect(Collectors.toList());
            postAttachedRepository.saveAll(images);
        }

        return savedPost.getPostId();
    }

    /**
     * 게시글 수정 (이미지 수정 로직 추가)
     */
    @Transactional
    public void updatePost(Integer postId, NoticeAdminRequestDto dto, String userId) {
        Post post = findPostOrThrow(postId);
        if (!post.getUserId().equals(userId)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }
        validateDto(dto);
        post.update(dto);

        // [추가] 기존 이미지 삭제 후 새로운 이미지 저장 (간단한 구현 방식)
        if (dto.getImageUrls() != null) {
            postAttachedRepository.deleteAllByPost(post); // 해당 메서드 Repository에 추가 필요
            List<PostAttached> images = dto.getImageUrls().stream()
                    .map(url -> PostAttached.create(post, userId, url))
                    .collect(Collectors.toList());
            postAttachedRepository.saveAll(images);
        }
    }

    /**
     * 게시글 삭제 (Post.java의 CascadeType.ALL 덕분에 코드는 그대로 유지)
     */
    @Transactional
    public void deletePost(Integer postId, String userId) {
        Post post = findPostOrThrow(postId);
        if (!post.getUserId().equals(userId) && !userId.equals("ADMIN")) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }
        // 이 한 줄로 Comment, PostLike, PostAttached가 모두 삭제됩니다.
        postRepository.delete(post);
    }

    /**
     * 게시글 목록 조회 (기존 로직 유지)
     */
    @Transactional(readOnly = true)
    public Page<Post> searchPosts(Integer boardId, String category, String type, String keyword, Pageable pageable, String userId) {
        boolean isLoggedIn = (userId != null && usersRepository.existsById(userId));

        if ("전체".equals(category) || (category != null && category.trim().isEmpty())) {
            category = null;
        }

        if ((keyword == null || keyword.trim().isEmpty()) && category != null) {
            return isLoggedIn ?
                    postRepository.findAllByBoardIdAndPostCategoryOrderByCreatedDateDesc(boardId, category, pageable) :
                    postRepository.findAllByBoardIdAndPostCategoryAndIsExternalTrueOrderByCreatedDateDesc(boardId, category, pageable);
        }

        if (keyword != null && !keyword.trim().isEmpty() && keyword.trim().length() < 2) {
            throw new IllegalArgumentException("검색어는 최소 2글자 이상 입력해 주세요.");
        }

        if (keyword == null || keyword.trim().isEmpty()) {
            return isLoggedIn ?
                    postRepository.findAllByBoardIdOrderByCreatedDateDesc(boardId, pageable) :
                    postRepository.findAllByBoardIdAndIsExternalTrueOrderByCreatedDateDesc(boardId, pageable);
        }

        String searchType = (type != null) ? type : "all";
        return switch (searchType) {
            case "title" -> isLoggedIn ?
                    postRepository.findAllByBoardIdAndPostTitleContainingOrderByCreatedDateDesc(boardId, keyword, pageable) :
                    postRepository.findAllByBoardIdAndPostTitleContainingAndIsExternalTrueOrderByCreatedDateDesc(boardId, keyword, pageable);

            case "contents" -> isLoggedIn ?
                    postRepository.findAllByBoardIdAndPostContentsContainingOrderByCreatedDateDesc(boardId, keyword, pageable) :
                    postRepository.findAllByBoardIdAndPostContentsContainingAndIsExternalTrueOrderByCreatedDateDesc(boardId, keyword, pageable);

            case "author" -> isLoggedIn ?
                    postRepository.findAllByBoardIdAndUserIdContainingOrderByCreatedDateDesc(boardId, keyword, pageable) :
                    postRepository.findAllByBoardIdAndUserIdContainingAndIsExternalTrueOrderByCreatedDateDesc(boardId, keyword, pageable);

            default -> isLoggedIn ?
                    postRepository.findAllByBoardIdOrderByCreatedDateDesc(boardId, pageable) :
                    postRepository.findAllByBoardIdAndIsExternalTrueOrderByCreatedDateDesc(boardId, pageable);
        };
    }

    /**
     * 게시글 단건 조회 (상세 조회용)
     */
    @Transactional(readOnly = true)
    public Post getPostById(Integer postId) {
        return findPostOrThrow(postId);
    }

    /**
     * 마이페이지 관련 로직 (기존 유지)
     */
    @Transactional(readOnly = true)
    public Page<Post> getMyPosts(String userId, Pageable pageable) {
        return postRepository.findAllByUserIdOrderByCreatedDateDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getMyPostStats(String userId) {
        Map<String, Object> stats = new HashMap<>();
        long postCount = postRepository.countByUserId(userId);
        Integer totalLikes = postRepository.sumPostLikeCountByUserId(userId);

        stats.put("postCount", postCount);
        stats.put("totalLikes", totalLikes != null ? totalLikes : 0);
        return stats;
    }

    private Post findPostOrThrow(Integer postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다. ID: " + postId));
    }

    private void validateDto(NoticeAdminRequestDto dto) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty() ||
                dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("필수 값이 누락되었습니다.");
        }
    }
}