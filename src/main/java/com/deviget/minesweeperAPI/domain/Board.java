package com.deviget.minesweeperAPI.domain;

import com.deviget.minesweeperAPI.enumeration.BoardStatusEnum;
import com.deviget.minesweeperAPI.enumeration.CellStatusEnum;
import com.deviget.minesweeperAPI.error.InternalServerException;
import com.deviget.minesweeperAPI.util.GridDrawer;
import com.deviget.minesweeperAPI.util.UniqueIdGenerator;
import lombok.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Board implements Cloneable {

    private String id;
    private User user;
    private Map<String, Cell> grid;
    private int rowSize;
    private int colSize;
    private int minesAmount;
    private int revealedCells;
    private Instant creationDatetime;
    private Instant startedDatetime;
    private Instant finishDatetime;
    private BoardStatusEnum status;

    public Cell getGridCell(int row, int col) {
        String cellKey = Cell.getKey(row, col);
        return grid.get(cellKey);
    }

    public void incrementRevealedCells() {
        revealedCells++;
    }

    public boolean wereAllCellsRevealed() {
        return revealedCells + minesAmount == rowSize * colSize;
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
        if (wasWon())
            flagAllMines();
        if (wasLost())
            showAllMines();
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

    /**
     * builds board based on configuration params
     *
     * @param user
     * @param rowSize
     * @param colSize
     * @param minesAmount
     * @return
     */
    public static Board buildBoard(User user, int rowSize, int colSize, int minesAmount) {

        return Board.builder()
                .id(UniqueIdGenerator.generate())
                .user(user)
                .rowSize(rowSize)
                .colSize(colSize)
                .minesAmount(minesAmount)
                .creationDatetime(Instant.now())
                .status(BoardStatusEnum.NEW)
                .grid(createGrid(rowSize, colSize))
                .build();
    }

    /**
     * creates rowSize x colSize grid with hidden cells
     *
     * @param rowSize
     * @param colSize
     * @return
     */
    private static Map<String, Cell> createGrid(int rowSize, int colSize) {

        Map<String, Cell> grid = new LinkedHashMap<>();
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) {
                String key = Cell.getKey(i, j);
                grid.put(key, Cell.newHidden(i, j));
            }
        }
        return grid;
    }

    private void flagAllMines() {
        grid.entrySet().stream().filter(entry -> entry.getValue().isMined())
                .forEach(entry -> entry.getValue().setStatus(CellStatusEnum.FLAGGED));
    }

    private void showAllMines() {
        grid.entrySet().stream().filter(entry -> entry.getValue().isMined())
                .forEach(entry -> entry.getValue().setStatus(CellStatusEnum.VISIBLE));
    }

    @Override
    public Board clone() {
        try {
            return (Board) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalServerException("Cannot clone board object. Error detail: " + e.getMessage());
        }
    }
}
