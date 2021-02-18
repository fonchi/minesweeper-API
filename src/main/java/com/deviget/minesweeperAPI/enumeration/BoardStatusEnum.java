package com.deviget.minesweeperAPI.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BoardStatusEnum {
    CREATED("Created"),
    STARTED("Started"),
    OVER("Over"),
    WON("Won");

    private String value;
}
