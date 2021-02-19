package com.deviget.minesweeperAPI.dto;

import com.deviget.minesweeperAPI.domain.Board;
import com.deviget.minesweeperAPI.domain.Cell;
import com.deviget.minesweeperAPI.domain.Position;
import com.deviget.minesweeperAPI.dto.component.CellDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class RevealFlagResponseDto {

    @JsonProperty("board_id")
    private String boardId;
    @JsonProperty("selected_row_num")
    private int selectedRowNum;
    @JsonProperty("selected_col_num")
    private int selectedColNum;
    @JsonProperty("status")
    private String status;
    @JsonProperty("creation_datetime")
    private Instant creationDatetime;
    @JsonProperty("started_datetime")
    private Instant startedDatetime;
    @JsonProperty("finished_datetime")
    private Instant finishedDatetime;
    @JsonProperty("seconds_elapsed")
    private float secondsElapsed;
    @JsonProperty("board_cells")
    private List<CellDto> boardCells;

    public static RevealFlagResponseDto fromEntity(Board board) {

        List<CellDto> boardCells = new ArrayList<>();
        board.getGrid().entrySet().stream().forEach(entry -> boardCells.add(getCellDto(entry.getKey(), entry.getValue())));

        return RevealFlagResponseDto.builder()
                .boardId(board.getId())
                .selectedRowNum(board.getRowSize())
                .selectedColNum(board.getColSize())
                .status(board.getStatus().getValue())
                .creationDatetime(board.getCreationDatetime())
                .startedDatetime(board.getStartedDatetime())
                .finishedDatetime(board.getFinishDatetime())
                .secondsElapsed(board.getGameSecondsElapsed())
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
