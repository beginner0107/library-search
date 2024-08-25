package com.library.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final DateTimeFormatter YYYYMMDD_FORMATER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static LocalDate parseYYYYMMDD(String date) {
        return LocalDate.parse(date, YYYYMMDD_FORMATER);
    }
}
