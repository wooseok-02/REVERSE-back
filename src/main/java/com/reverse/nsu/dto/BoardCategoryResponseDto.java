package com.reverse.nsu.dto;

import com.reverse.nsu.entity.Board;
import lombok.Getter;

@Getter
public class BoardCategoryResponseDto {
    private final Integer boardId;
    private final String boardName;
    private final String boardDescription;

    public BoardCategoryResponseDto(Board board) {
        this.boardId = board.getBoardId();
        this.boardName = board.getBoardName();
        this.boardDescription = board.getBoardDescription();
    }
}