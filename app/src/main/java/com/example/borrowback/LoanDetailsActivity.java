package com.example.borrowback;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.database.sqlite.SQLiteException;

public class LoanDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String LOAN_ID = "loan_id";
    private DataManager dm;
    private long loanId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loan_details);
        // System-bar handling retained from the Android Studio starter template.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main),
                new androidx.core.view.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat insets) {
                        Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                                | WindowInsetsCompat.Type.ime());
                        view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                        return insets;
                    }
                });
        dm = new DataManager(this);
        // The adapter sends this identifier with Intent.putExtra().
        loanId = getIntent().getLongExtra(LOAN_ID, -1);
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnDeleteLoan).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Loan loan = dm.findLoan(loanId);
        if (loan == null) {
            Toast.makeText(this, R.string.loan_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        ((TextView) findViewById(R.id.txtDetailItem)).setText(loan.getItemName());
        ((TextView) findViewById(R.id.txtDetailBorrower)).setText(loan.getBorrower());
        ((TextView) findViewById(R.id.txtDetailBorrowedDate)).setText(loan.getBorrowedDate());
        ((TextView) findViewById(R.id.txtDetailReturnDate)).setText(loan.getReturnDate());
        ((TextView) findViewById(R.id.txtDetailNotes)).setText(loan.getNotes().isEmpty()
                ? getString(R.string.no_notes) : loan.getNotes());
        ((TextView) findViewById(R.id.txtDetailStatus)).setText(
                LoanDates.isOverdue(loan.getReturnDate()) ? R.string.overdue : R.string.on_loan);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnBack) { finish(); }
        else if (view.getId() == R.id.btnDeleteLoan) {
            if (getSupportFragmentManager().findFragmentByTag("delete_loan") == null) {
                new DeleteLoanDialog().show(getSupportFragmentManager(), "delete_loan");
            }
        }
    }

    public void deleteLoan() {
        try {
            dm.delete(loanId);
            Toast.makeText(this, R.string.loan_deleted, Toast.LENGTH_SHORT).show();
            finish();
        } catch (SQLiteException exception) {
            Toast.makeText(this, R.string.delete_failed, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        dm.close();
        super.onDestroy();
    }
}
