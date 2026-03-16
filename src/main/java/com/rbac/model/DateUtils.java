package com.rbac.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String getCurrentDate() {
        return LocalDate.now().format(DATE_FORMAT);
    }

    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DATE_TIME_FORMAT);
    }

    public static boolean isBefore(String date1, String date2) {
        return date1.compareTo(date2) < 0;
    }

    public static boolean isAfter(String date1, String date2) {
        return date1.compareTo(date2) > 0;
    }

    public static String addDays(String date, int days) {
        LocalDate localDate = LocalDate.parse(date, DATE_FORMAT);
        return localDate.plusDays(days).format(DATE_FORMAT);
    }

    public static String formatRelativeTime(String date) {
        LocalDate target = LocalDate.parse(date, DATE_FORMAT);
        LocalDate now = LocalDate.now();
        long days = ChronoUnit.DAYS.between(now, target);

        if (days == 0) return "today";
        if (days > 0) return "in " + days + " days";
        return Math.abs(days) + " days ago";
    }
}