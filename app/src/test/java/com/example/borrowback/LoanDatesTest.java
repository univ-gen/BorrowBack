package com.example.borrowback;

import org.junit.Test;
import static org.junit.Assert.*;

public class LoanDatesTest {
    @Test
    public void acceptsRealDatesIncludingLeapDay() {
        assertTrue(LoanDates.isValid("2028-02-29"));
        assertTrue(LoanDates.isValid("2026-11-03"));
    }

    @Test
    public void rejectsImpossibleOrAmbiguousDates() {
        String[] invalid = {"2026-02-29", "2026-04-31", "2026-13-01", "2026-00-01",
                "2026-01-00", "2026-1-01", "03/11/2026", "2026-11-03extra", "", "0000-01-01"};
        for (String date : invalid) {
            assertFalse(date, LoanDates.isValid(date));
        }
    }

    @Test
    public void dueTodayIsNotOverdue() {
        assertFalse(LoanDates.isOverdue(LoanDates.today()));
        assertTrue(LoanDates.isOverdue("2000-01-01"));
        assertFalse(LoanDates.isOverdue("9999-01-01"));
    }
}
