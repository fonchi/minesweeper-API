package com.deviget.minesweeperAPI.domain;

import com.deviget.minesweeperAPI.enumeration.CellStatusEnum;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Cell {

    private int row;
    private int col;
    private CellStatusEnum status;
    private boolean isMined;
    private int minesAround;

    public Cell(CellStatusEnum status, int row, int col) {
        this.status = status;
        this.row = row;
        this.col = col;
    }

    public static Cell newHidden(int row, int col) {
        return new Cell(CellStatusEnum.HIDDEN, row, col);
    }

    public void incrementMinesAround() {
        minesAround++;
    }

    public boolean isVisible() {
        return CellStatusEnum.VISIBLE.equals(status);
    }

    public boolean isFlagged() {
        return CellStatusEnum.FLAGGED.equals(status);
    }

    public boolean isHidden() {
        return CellStatusEnum.HIDDEN.equals(status);
    }

    public static String getKey(int row, int col) {
        return String.format("(%s,%s)", row, col);
    }
}
