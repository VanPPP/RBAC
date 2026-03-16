package com.rbac.model;

import java.util.List;

public class FormatUtils {

    public static String padRight(String text, int length) {
        return String.format("%-" + length + "s", text);
    }

    public static String padLeft(String text, int length) {
        return String.format("%" + length + "s", text);
    }

    public static String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    public static String formatHeader(String text) {
        String line = "=".repeat(text.length() + 4);
        return String.format("\n%s\n  %s  \n%s", line, text.toUpperCase(), line);
    }

    public static String formatBox(String text) {
        String line = "+" + "-".repeat(text.length() + 2) + "+";
        return String.format("%s\n| %s |\n%s", line, text, line);
    }

    public static String formatTable(String[] headers, List<String[]> rows) {
        if (headers == null || headers.length == 0) return "";

        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            widths[i] = Math.max(headers[i].length(), 15);
            for (String[] row : rows) {
                if (row.length > i && row[i] != null) {
                    widths[i] = Math.max(widths[i], row[i].length());
                }
            }
        }

        StringBuilder sb = new StringBuilder();

        StringBuilder lineBuilder = new StringBuilder("+");
        for (int w : widths) lineBuilder.append("-".repeat(w + 2)).append("+");
        String separator = lineBuilder.toString();

        sb.append(separator).append("\n");

        sb.append("|");
        for (int i = 0; i < headers.length; i++) {
            sb.append(" ").append(padRight(headers[i], widths[i])).append(" |");
        }
        sb.append("\n").append(separator).append("\n");

        if (rows.isEmpty()) {
            sb.append("| ").append(padRight("No data available", separator.length() - 4)).append(" |\n");
        } else {
            for (String[] row : rows) {
                sb.append("|");
                for (int i = 0; i < headers.length; i++) {
                    String cell = (i < row.length) ? row[i] : "";
                    sb.append(" ").append(padRight(truncate(cell, widths[i]), widths[i])).append(" |");
                }
                sb.append("\n");
            }
        }
        sb.append(separator);

        return sb.toString();
    }
}