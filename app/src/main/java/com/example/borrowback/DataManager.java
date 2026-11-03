package com.example.borrowback;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DataManager {
    private static final String DB_NAME = "borrowback_db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_LOANS = "loans";
    private final MyDbHelper helper;
    private final SQLiteDatabase dbase;

    public DataManager(Context context) {
        helper = new MyDbHelper(context.getApplicationContext());
        dbase = helper.getWritableDatabase();
    }

    private static class MyDbHelper extends SQLiteOpenHelper {
        MyDbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase dbase) {
            String query = "CREATE TABLE " + TABLE_LOANS + " ("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "item_name TEXT NOT NULL,"
                    + "borrower TEXT NOT NULL,"
                    + "borrowed_date TEXT NOT NULL,"
                    + "return_date TEXT NOT NULL,"
                    + "notes TEXT NOT NULL);";
            dbase.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase dbase, int oldVersion, int newVersion) {
            // Version 1 has no earlier database schema to upgrade.
        }
    }

    public void insert(String itemName, String borrower, String borrowedDate,
                       String returnDate, String notes) {
        String query = "INSERT INTO " + TABLE_LOANS
                + " (item_name, borrower, borrowed_date, return_date, notes) VALUES (?, ?, ?, ?, ?)";
        // Values are passed separately so names such as O'Brien are saved correctly.
        dbase.execSQL(query, new Object[]{itemName, borrower, borrowedDate, returnDate, notes});
    }

    public void delete(long id) {
        dbase.execSQL("DELETE FROM " + TABLE_LOANS + " WHERE _id = ?", new Object[]{id});
    }

    public Cursor selectAll() {
        return dbase.rawQuery("SELECT * FROM " + TABLE_LOANS
                + " ORDER BY return_date ASC, _id DESC", null);
    }

    public Cursor search(String text) {
        // instr searches for literal text, including % and _, rather than SQL wildcards.
        return dbase.rawQuery("SELECT * FROM " + TABLE_LOANS
                        + " WHERE instr(lower(item_name), lower(?)) > 0"
                        + " OR instr(lower(borrower), lower(?)) > 0"
                        + " ORDER BY return_date ASC, _id DESC", new String[]{text, text});
    }

    public Loan findLoan(long id) {
        Cursor cursor = dbase.rawQuery("SELECT * FROM " + TABLE_LOANS + " WHERE _id = ?",
                new String[]{String.valueOf(id)});
        try {
            if (cursor.moveToFirst()) {
                return readLoan(cursor);
            }
            return null;
        } finally {
            cursor.close();
        }
    }

    public ArrayList<Loan> readLoans(Cursor cursor) {
        ArrayList<Loan> loans = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                loans.add(readLoan(cursor));
            }
        } finally {
            cursor.close();
        }
        return loans;
    }

    private Loan readLoan(Cursor cursor) {
        return new Loan(cursor.getLong(0), cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4), cursor.getString(5));
    }

    public void close() {
        helper.close();
    }
}
