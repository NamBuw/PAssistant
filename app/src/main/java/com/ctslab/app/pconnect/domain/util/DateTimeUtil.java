package com.avis.app.ptalk.domain.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class DateTimeUtil {
    public static LocalDateTime unixTimeToDateTime(long unixTimeMillis) {
        return LocalDateTime.ofEpochSecond(unixTimeMillis / 1000, 0, ZoneOffset.UTC);
    }

    public static String unixTimeToDateTimeString(long unixTimeMillis) {
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(unixTimeMillis / 1000, 0, ZoneOffset.UTC);
        return dateTime.toString();
    }
}
