package com.rbac.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String getCurrentDate() {
        return LocalDate.now().format(DATE_FMT);
    }

    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DT_FMT);
    }

    public static boolean isBefore(String date1, String date2) {
        return date1.compareTo(date2) < 0;
    }

    public static boolean isAfter(String date1, String date2) {
        return date1.compareTo(date2) > 0;
    }

    public static String addDays(String date, int days) {
        LocalDate parsedDate = LocalDate.parse(date, DATE_FMT);
        return parsedDate.plusDays(days).format(DATE_FMT);
    }

    public static String formatRelativeTime(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr, DATE_FMT);
        LocalDate now = LocalDate.now();
        long days = ChronoUnit.DAYS.between(now, date);

        if (days == 0) return "today";
        if (days == 1) return "tomorrow";
        if (days == -1) return "yesterday";
        if (days > 0) return "in " + days + " days";
        return Math.abs(days) + " days ago";
    }
}