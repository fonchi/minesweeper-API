package com.deviget.minesweeperAPI.domain;

import com.deviget.minesweeperAPI.enumeration.CellStatusEnum;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class Cell {

    private CellStatusEnum status;
    private boolean isMined;
    private int minesAround;

    public static Cell newHidden() {
        return new Cell(CellStatusEnum.HIDDEN, false, 0);
    }

    public void incrementMinesAround() {
        minesAround++;
    }
}
