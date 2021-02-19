package com.deviget.minesweeperAPI.domain;

import com.deviget.minesweeperAPI.enumeration.BoardStatusEnum;
import com.deviget.minesweeperAPI.util.GridDrawer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@ToString
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

    public boolean wasAllCellsRevealed() {
        return revealedMines + minesAmount == rowSize * colSize;
    }

    public void drawGrid() {
        GridDrawer.draw(this);
    }

    public void start() {
        if (isNull(startedDatetime))
            startedDatetime = Instant.now();
        if (BoardStatusEnum.NEW.equals(status))
            status = BoardStatusEnum.PLAYING;
    }

    public float getGameSecondsElapsed() {
        Instant lastDatetime = isNull(finishDatetime) ? Instant.now() : finishDatetime;
        return ChronoUnit.MILLIS.between(startedDatetime, lastDatetime) / 1000;
    }

    public boolean wasWon() {
        return BoardStatusEnum.WON.equals(status);
    }

    public boolean wasLost() {
        return BoardStatusEnum.LOST.equals(status);
    }
}
