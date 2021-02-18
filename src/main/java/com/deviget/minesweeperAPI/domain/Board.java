package com.deviget.minesweeperAPI.domain;

import com.deviget.minesweeperAPI.enumeration.BoardStatusEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Builder
public class Board {

    private String id;
    private User user;
    private Map<Position, Cell> grid;
    private int rowSize;
    private int colSize;
    private int minesAmount;
    private int revealedMines;
    private Instant creationDatetime;
    private Instant startedDatetime;
    private Instant finishDatetime;
    private BoardStatusEnum status;
    private int attempts;

    public void incrementRevealedMines() {
        revealedMines++;
    }

    public boolean allCellsRevealed() {
        return revealedMines == (rowSize * colSize);
    }
}
