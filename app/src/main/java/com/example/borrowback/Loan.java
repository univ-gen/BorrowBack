package com.example.borrowback;

public class Loan {
    private final long mId;
    private final String mItemName;
    private final String mBorrower;
    private final String mBorrowedDate;
    private final String mReturnDate;
    private final String mNotes;

    public Loan(long id, String itemName, String borrower, String borrowedDate,
                String returnDate, String notes) {
        mId = id;
        mItemName = itemName;
        mBorrower = borrower;
        mBorrowedDate = borrowedDate;
        mReturnDate = returnDate;
        mNotes = notes;
    }

    public long getId() { return mId; }
    public String getItemName() { return mItemName; }
    public String getBorrower() { return mBorrower; }
    public String getBorrowedDate() { return mBorrowedDate; }
    public String getReturnDate() { return mReturnDate; }
    public String getNotes() { return mNotes; }
}
