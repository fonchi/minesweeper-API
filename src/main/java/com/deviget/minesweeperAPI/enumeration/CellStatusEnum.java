package com.deviget.minesweeperAPI.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CellStatusEnum {
    HIDDEN("hidden"),
    FLAGGED("flagged"),
    VISIBLE("visible");

    private String value;
}
