package com.deviget.minesweeperAPI.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BoardStatusEnum {
    CREATED("created"),
    PLAYING("playing"),
    OVER("over"),
    WON("won");

    private String value;
}
