package com.deviget.minesweeperAPI.util;

import java.util.UUID;

public class UniqueIdGenerator {

    public static String generate() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
