package com.deviget.minesweeperAPI.dto;

import com.deviget.minesweeperAPI.domain.Board;
import com.deviget.minesweeperAPI.domain.Cell;
import com.deviget.minesweeperAPI.domain.Position;
import com.deviget.minesweeperAPI.dto.component.CellDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@ToString
public class BoardResponseDto {

    @JsonProperty("board_id")
    private String boardId;
    @JsonProperty("row_size")
    private int rowSize;
    @JsonProperty("col_size")
    private int colSize;
    @JsonProperty("mines_amount")
    private int minesAmount;
    @JsonProperty("status")
    private String status;
    @JsonProperty("creation_datetime")
    private Instant creationDatetime;
    @JsonProperty("board_cells")
    private List<CellDto> boardCells;

    public static BoardResponseDto fromEntity(Board board) {

        List<CellDto> boardCells = new ArrayList<>();
        board.getGrid().entrySet().stream().forEach(entry -> boardCells.add(getCellDto(entry.getKey(), entry.getValue())));

        return BoardResponseDto.builder()
                .boardId(board.getId())
                .rowSize(board.getRowSize())
                .colSize(board.getColSize())
                .minesAmount(board.getMinesAmount())
                .status(board.getStatus().getValue())
                .creationDatetime(board.getCreationDatetime())
                .boardCells(boardCells)
                .build();
    }

    private static CellDto getCellDto(Position pos, Cell cell) {

        return CellDto.builder()
                .row(pos.getRow())
                .col(pos.getCol())
                .isMined(cell.isMined())
                .minesAround(cell.getMinesAround())
                .status(cell.getStatus().getValue())
                .build();
    }
}
