package com.deviget.minesweeperAPI.domain;

import com.deviget.minesweeperAPI.enumeration.CellStatusEnum;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class Cell {

    private Position position;
    private CellStatusEnum status;
    private boolean isMined;
    private int minesAround;

    public Cell(CellStatusEnum status, Position pos) {
        this.status = status;
        this.position = pos;
    }

    public static Cell newHidden(Position pos) {
        return new Cell(CellStatusEnum.HIDDEN, pos);
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
}
