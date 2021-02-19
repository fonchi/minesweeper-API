package com.deviget.minesweeperAPI.domain;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Position {

    private int row;
    private int col;
}
