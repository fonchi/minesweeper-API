package com.deviget.minesweeperAPI.domain;

import com.deviget.minesweeperAPI.enumeration.BoardStatusEnum;
import com.deviget.minesweeperAPI.enumeration.CellStatusEnum;
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

    public boolean allCellsRevealed() {
        return revealedMines + minesAmount == rowSize * colSize;
    }

    public void drawGrid() {
        System.out.print("\nValues:\n");
        for (int row = 0; row < rowSize; row++) {
            for (int col = 0; col < colSize; col++) {
                Cell cell = grid.get(new Position(row, col));
                if (cell.isMined())
                    System.out.print(" *");
                else
                    System.out.print(" " + cell.getMinesAround());
            }
            System.out.println();
        }
        System.out.println();
        System.out.println("Interface:");
        for (int row = 0; row < rowSize; row++) {
            for (int col = 0; col < colSize; col++) {
                Cell cell = grid.get(new Position(row, col));
                if (CellStatusEnum.VISIBLE.equals(cell.getStatus())) {
                    if (cell.isMined())
                        System.out.print(" *");
                    else if (cell.getMinesAround() > 0)
                        System.out.print(" " + cell.getMinesAround());
                    else
                        System.out.print(" 0");
                }
                if (CellStatusEnum.HIDDEN.equals(cell.getStatus()))
                    System.out.print(" #");
                if (CellStatusEnum.FLAGGED.equals(cell.getStatus()))
                    System.out.print(" F");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println("Status: " + status.getValue().toUpperCase() + "!\n");
    }

    public void start() {
        if (isNull(startedDatetime))
            startedDatetime = Instant.now();
        if (BoardStatusEnum.CREATED.equals(status))
            status = BoardStatusEnum.PLAYING;
    }

    public float getGameSecondsElapsed() {
        Instant lastDatetime = isNull(finishDatetime) ? Instant.now() : finishDatetime;
        return ChronoUnit.MILLIS.between(startedDatetime, lastDatetime) / 1000;
    }
}
