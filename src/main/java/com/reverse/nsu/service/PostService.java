package com.reverse.nsu.service;

import com.reverse.nsu.dto.NoticeAdminRequestDto;
import com.reverse.nsu.entity.Post;
import com.reverse.nsu.repository.PostRepository;
import com.reverse.nsu.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UsersRepository usersRepository;

    /**
     * 게시글 작성
     */
    @Transactional
    public Integer createPost(NoticeAdminRequestDto dto, String userId, Integer boardId) {
        validateDto(dto);
        Post post = Post.createPost(dto, userId, boardId);
        return postRepository.save(post).getPostId();
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public void updatePost(Integer postId, NoticeAdminRequestDto dto, String userId) {
        Post post = findPostOrThrow(postId);
        if (!post.getUserId().equals(userId)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }
        validateDto(dto);
        post.update(dto);
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void deletePost(Integer postId, String userId) {
        Post post = findPostOrThrow(postId);
        // 작성자 본인 확인 또는 관리자(ADMIN) 권한 확인
        if (!post.getUserId().equals(userId) && !userId.equals("ADMIN")) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }
        postRepository.delete(post);
    }

    /**
     * 게시글 목록 조회 및 검색 (카테고리 필터링 및 boardId 필터링 강화)
     */
    @Transactional(readOnly = true)
    public Page<Post> searchPosts(Integer boardId, String category, String type, String keyword, Pageable pageable, String userId) {
        // 로그인 여부 확인
        boolean isLoggedIn = (userId != null && usersRepository.existsById(userId));

        // [핵심 수정] 프론트에서 "전체"를 보낼 경우 필터링을 하지 않도록 처리
        if ("전체".equals(category) || (category != null && category.trim().isEmpty())) {
            category = null;
        }

        // 1. 카테고리 필터링 (검색어가 없고 특정 카테고리만 선택된 경우)
        if ((keyword == null || keyword.trim().isEmpty()) && category != null) {
            return isLoggedIn ?
                    postRepository.findAllByBoardIdAndPostCategoryOrderByCreatedDateDesc(boardId, category, pageable) :
                    postRepository.findAllByBoardIdAndPostCategoryAndIsExternalTrueOrderByCreatedDateDesc(boardId, category, pageable);
        }

        // 2. 검색어 제한 (2글자 미만 체크)
        if (keyword != null && !keyword.trim().isEmpty() && keyword.trim().length() < 2) {
            throw new IllegalArgumentException("검색어는 최소 2글자 이상 입력해 주세요.");
        }

        // 3. 검색어가 없는 경우 (일반 목록 조회)
        if (keyword == null || keyword.trim().isEmpty()) {
            return isLoggedIn ?
                    postRepository.findAllByBoardIdOrderByCreatedDateDesc(boardId, pageable) :
                    postRepository.findAllByBoardIdAndIsExternalTrueOrderByCreatedDateDesc(boardId, pageable);
        }

        // 4. 검색어가 있는 경우 (타입별 검색)
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
     * 게시글 단건 조회
     */
    @Transactional(readOnly = true)
    public Post getPostById(Integer postId) {
        return findPostOrThrow(postId);
    }

    /**
     * 나의 게시글 목록 확인 (마이페이지용)
     */
    @Transactional(readOnly = true)
    public Page<Post> getMyPosts(String userId, Pageable pageable) {
        return postRepository.findAllByUserIdOrderByCreatedDateDesc(userId, pageable);
    }

    /**
     * 나의 통계 정보 조회 (마이페이지용)
     */
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