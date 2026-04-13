package com.reverse.nsu.service;

import com.reverse.nsu.dto.NoticeAdminRequestDto;
import com.reverse.nsu.dto.NoticeAdminResponseDto;
import com.reverse.nsu.dto.NoticeListResponseDto;
import com.reverse.nsu.dto.NoticeResponseDto;
import com.reverse.nsu.entity.Board;
import com.reverse.nsu.entity.Post;
import com.reverse.nsu.repository.BoardRepository;
import com.reverse.nsu.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class NoticeService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;

    private Integer noticeBoardId; // 캐싱

    private Integer getNoticeBoardId() {
        if (noticeBoardId == null) {
            noticeBoardId = boardRepository.findByBoardName("공지사항")
                    .orElseThrow(() -> new RuntimeException("공지사항 게시판이 존재하지 않습니다."))
                    .getBoardId();
        }
        return noticeBoardId;
    }
    // 나머지 동일

    // 단건 조회
    public NoticeResponseDto getOne(Integer noticeId) {
        return postRepository.findById(noticeId)
                .filter(p -> p.getBoardId().equals(getNoticeBoardId()))
                .map(NoticeResponseDto::from)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));
    }

    // 목록 조회
    public List<NoticeListResponseDto> getAll() {
        return postRepository.findAllByBoardIdOrderByCreatedDateDesc(getNoticeBoardId())
                .stream().map(NoticeListResponseDto::new).collect(Collectors.toList());
    }

        // 등록
    public NoticeAdminResponseDto create(NoticeAdminRequestDto dto, String userId) {
        if (dto.getTitle() == null || dto.getContent() == null)
            throw new IllegalArgumentException("MISSING_FIELD");

        Post post = Post.createNotice(dto, userId, getNoticeBoardId());
        return new NoticeAdminResponseDto(postRepository.save(post).getPostId());
    }

    // 수정
    public NoticeAdminResponseDto update(NoticeAdminRequestDto dto, String userId) {
        if (dto.getTitle() == null || dto.getContent() == null)
            throw new IllegalArgumentException("MISSING_FIELD");

        Post post = postRepository.findById(dto.getNoticeId())
                .filter(p -> p.getBoardId().equals(getNoticeBoardId()))
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));
        post.update(dto);
        return new NoticeAdminResponseDto(postRepository.save(post).getPostId());
    }

    // 삭제
    public Integer delete(Integer noticeId) {
        Post post = postRepository.findById(noticeId)
                .filter(p -> p.getBoardId().equals(getNoticeBoardId()))
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));
        postRepository.delete(post);
        return noticeId;
    }
}