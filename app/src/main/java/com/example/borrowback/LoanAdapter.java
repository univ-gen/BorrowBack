package com.example.borrowback;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LoanAdapter extends RecyclerView.Adapter<LoanViewHolder> {
    private final Context context;
    private final ArrayList<Loan> loanList;

    public LoanAdapter(Context context, ArrayList<Loan> loanList) {
        this.context = context;
        this.loanList = loanList;
    }

    @Override
    public LoanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_loan, parent, false);
        return new LoanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LoanViewHolder holder, int position) {
        final Loan loan = loanList.get(position);
        holder.setItemName(loan.getItemName());
        holder.setBorrower(context.getString(R.string.borrower_value, loan.getBorrower()));
        holder.setReturnDate(context.getString(R.string.return_value, loan.getReturnDate()));
        holder.setStatus(context.getString(LoanDates.isOverdue(loan.getReturnDate())
                ? R.string.overdue : R.string.on_loan));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LoanDetailsActivity.class);
                intent.putExtra(LoanDetailsActivity.LOAN_ID, loan.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return loanList.size();
    }
}
