package com.deviget.minesweeperAPI.domain;

import com.deviget.minesweeperAPI.enumeration.BoardStatusEnum;
import com.deviget.minesweeperAPI.util.GridDrawer;
import lombok.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Board {

    private String id;
    private User user;
    private Map<String, Cell> grid;
    private int rowSize;
    private int colSize;
    private int minesAmount;
    private int revealedMines;
    private Instant creationDatetime;
    private Instant startedDatetime;
    private Instant finishDatetime;
    private BoardStatusEnum status;

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

    public void finish(BoardStatusEnum status) {
        this.status = status;
        this.finishDatetime = Instant.now();
    }

    public long getGameSecondsElapsed() {
        Instant lastDatetime = isNull(finishDatetime) ? Instant.now() : finishDatetime;
        return ChronoUnit.SECONDS.between(startedDatetime, lastDatetime);
    }

    public boolean wasWon() {
        return BoardStatusEnum.WON.equals(status);
    }

    public boolean wasLost() {
        return BoardStatusEnum.LOST.equals(status);
    }
}
