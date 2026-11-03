package com.example.borrowback;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class LoanViewHolder extends RecyclerView.ViewHolder {
    private final TextView mItemName;
    private final TextView mBorrower;
    private final TextView mReturnDate;
    private final TextView mStatus;

    public LoanViewHolder(View itemView) {
        super(itemView);
        mItemName = itemView.findViewById(R.id.txtItemName);
        mBorrower = itemView.findViewById(R.id.txtBorrower);
        mReturnDate = itemView.findViewById(R.id.txtReturnDate);
        mStatus = itemView.findViewById(R.id.txtStatus);
    }

    public void setItemName(String text) { mItemName.setText(text); }
    public void setBorrower(String text) { mBorrower.setText(text); }
    public void setReturnDate(String text) { mReturnDate.setText(text); }
    public void setStatus(String text) { mStatus.setText(text); }
}
