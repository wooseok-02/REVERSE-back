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
    private final Integer parentCommentId;
    private final String createdAt;
    private final String modifiedAt;
    private final List<CommentResponseDto> replies;

    public CommentResponseDto(Comment comment) {
        this.commentId = comment.getCommentId();
        this.userId = comment.getUserId();
        this.commentDetail = comment.getCommentDetail();
        this.parentCommentId = comment.getParentCommentId();
        this.createdAt = comment.getCreatedDate().toLocalDate().toString();
        this.modifiedAt = comment.getModifiedDate() != null
                ? comment.getModifiedDate().toLocalDate().toString() : null;
        this.replies = new ArrayList<>();
    }

    public void addReply(CommentResponseDto reply) {
        this.replies.add(reply);
    }
}
