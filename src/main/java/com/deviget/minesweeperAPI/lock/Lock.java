package com.deviget.minesweeperAPI.lock;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Lock {

    private String resourceId;
    private long ttl;

}
