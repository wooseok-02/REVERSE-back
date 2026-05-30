package com.reverse.nsu.service;

import com.reverse.nsu.entity.Board;
import com.reverse.nsu.repository.BoardRepository;
import com.reverse.nsu.repository.CommentRepository;
import com.reverse.nsu.repository.PostAttachedRepository;
import com.reverse.nsu.repository.PostLikeRepository;
import com.reverse.nsu.repository.PostRepository;
import com.reverse.nsu.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardAdminService {

    private final PostRepository postRepository;
    private final PostAttachedRepository postAttachedRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UsersRepository usersRepository;

    // 관리자 권한 확인 (roleId 1 또는 2)
    public boolean isAdmin(String userId) {
        return usersRepository.findById(userId)
                .map(user -> user.getRole().getRoleId() == 1 || user.getRole().getRoleId() == 2)
                .orElse(false);
    }

    // 게시글 강제 삭제 (관리자 전용)
    @Transactional
    public void forceDeletePost(Integer postId) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        postLikeRepository.deleteAllByPost(post);
        postAttachedRepository.deleteAllByPost(post);
        commentRepository.deleteAllByPost(post);
        postRepository.delete(post);
    }

    // 게시판 카테고리(보드) 전체 조회
    @Transactional(readOnly = true)
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    // 게시판 카테고리 추가
    @Transactional
    public Board createBoard(String boardName, String boardDescription) {
        if (boardName == null || boardName.isBlank()) {
            throw new IllegalArgumentException("게시판 이름은 필수입니다.");
        }
        Board board = Board.builder()
                .boardName(boardName)
                .boardDescription(boardDescription)
                .build();
        return boardRepository.save(board);
    }

    // 게시판 카테고리 삭제
    @Transactional
    public void deleteBoard(Integer boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));
        boardRepository.delete(board);
    }
}
