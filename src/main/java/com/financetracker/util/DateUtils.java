package com.financetracker.util;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public final class DateUtils {

    private DateUtils() {
        // Utility class
    }

    public static LocalDate getStartOfMonth(int month, int year) {
        return LocalDate.of(year, month, 1);
    }

    public static LocalDate getEndOfMonth(int month, int year) {
        return YearMonth.of(year, month).atEndOfMonth();
    }

    public static LocalDate getStartOfCurrentMonth() {
        return YearMonth.now().atDay(1);
    }

    public static LocalDate getEndOfCurrentMonth() {
        return YearMonth.now().atEndOfMonth();
    }

    public static String getMonthName(int month) {
        return java.time.Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    public static String getShortMonthName(int month) {
        return java.time.Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    }

    public static YearMonth getCurrentYearMonth() {
        return YearMonth.now();
    }
}
