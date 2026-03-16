package com.rbac.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {
    @Test
    void testDateArithmetic() {
        String start = "2026-03-01";
        assertEquals("2026-03-06", DateUtils.addDays(start, 5));
    }

    @Test
    void testComparisons() {
        assertTrue(DateUtils.isBefore("2026-01-01", "2026-01-02"));
        assertTrue(DateUtils.isAfter("2026-03-20", "2026-03-10"));
    }

    @Test
    void testRelativeTime() {
        String today = DateUtils.getCurrentDate();
        String inTwoDays = DateUtils.addDays(today, 2);
        assertEquals("in 2 days", DateUtils.formatRelativeTime(inTwoDays));
    }
}