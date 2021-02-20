package com.deviget.minesweeperAPI.util;

import java.time.Instant;
import java.time.ZoneOffset;

public class TimeUtil {

    public static Instant getInstant(int year, int month, int day, int hour, int minute, int second, int nano) {

        return Instant.now().atZone(ZoneOffset.UTC)
                .withYear(year)
                .withMonth(month)
                .withDayOfMonth(day)
                .withHour(hour)
                .withMinute(minute)
                .withSecond(second)
                .withNano(nano)
                .toInstant();
    }
}
