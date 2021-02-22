package com.deviget.minesweeperAPI.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BoardStatusEnum {
    NEW("new"),
    PLAYING("playing"),
    LOST("lost"),
    WON("won");

    private String value;
}
