package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Comment;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CommentResponseDto {
    private final Integer commentId;
    private final String userId;
    private final String commentDetail;
    private final Integer parentCommentId; // JSON 응답용 ID 값
    private final String createdAt;
    private final String modifiedAt;
    private final List<CommentResponseDto> replies;

    public CommentResponseDto(Comment comment) {
        this.commentId = comment.getCommentId();
        this.userId = comment.getUserId();
        this.commentDetail = comment.getCommentDetail();

        // [수정 핵심] comment.getParentCommentId() 대신 객체에서 ID를 추출합니다.
        // 부모가 있으면 그 부모의 ID를 넣고, 없으면(원댓글이면) null을 넣습니다.
        this.parentCommentId = (comment.getParent() != null)
                ? comment.getParent().getCommentId()
                : null;

        this.createdAt = comment.getCreatedDate().toLocalDate().toString();
        this.modifiedAt = comment.getModifiedDate() != null
                ? comment.getModifiedDate().toLocalDate().toString() : null;
        this.replies = new ArrayList<>();
    }

    public void addReply(CommentResponseDto reply) {
        this.replies.add(reply);
    }
}