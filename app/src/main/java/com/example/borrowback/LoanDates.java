package com.example.borrowback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LoanDates {
    // A fixed format makes saved dates easy to compare and read.
    public static boolean isValid(String value) {
        if (!value.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
            return false;
        }
        if (value.startsWith("0000")) {
            return false;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        format.setLenient(false);
        try {
            format.parse(value);
            return true;
        } catch (ParseException exception) {
            return false;
        }
    }

    public static String today() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    public static boolean isOverdue(String returnDate) {
        return isValid(returnDate) && returnDate.compareTo(today()) < 0;
    }
}
