package com.deviget.minesweeperAPI.dto.component;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CellDto {

    private int row;
    private int col;
    private String status;
    private int minesAround;
    private boolean isMined;
}
