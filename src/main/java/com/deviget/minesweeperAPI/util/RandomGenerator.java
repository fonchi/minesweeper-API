package com.deviget.minesweeperAPI.util;

import java.util.Random;

public class RandomGenerator {

    public static int generateRandomInt(int bound) {
        Random random = new Random();
        return random.nextInt(bound);
    }
}
