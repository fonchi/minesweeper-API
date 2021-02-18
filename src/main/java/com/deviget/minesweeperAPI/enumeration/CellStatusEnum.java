package com.deviget.minesweeperAPI.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CellStatusEnum {
    HIDDEN("Hidden"),
    FLAGGED("Flagged"),
    VISIBLE("Visible");

    private String value;
}
