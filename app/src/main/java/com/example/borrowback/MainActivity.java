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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private DataManager dm;
    private TextView txtLoanCount;
    private TextView txtOverdueCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
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
        txtLoanCount = findViewById(R.id.txtLoanCount);
        txtOverdueCount = findViewById(R.id.txtOverdueCount);
        findViewById(R.id.btnAddLoan).setOnClickListener(this);
        findViewById(R.id.btnAllLoans).setOnClickListener(this);
        findViewById(R.id.btnSearchLoans).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<Loan> loans = dm.readLoans(dm.selectAll());
        int overdue = 0;
        for (Loan loan : loans) {
            if (LoanDates.isOverdue(loan.getReturnDate())) { overdue++; }
        }
        txtLoanCount.setText(String.valueOf(loans.size()));
        txtOverdueCount.setText(String.valueOf(overdue));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnAddLoan) {
            startActivity(new Intent(this, AddLoanActivity.class));
        } else if (view.getId() == R.id.btnAllLoans) {
            startActivity(new Intent(this, LoanListActivity.class));
        } else if (view.getId() == R.id.btnSearchLoans) {
            startActivity(new Intent(this, SearchLoanActivity.class));
        }
    }

    @Override
    protected void onDestroy() {
        dm.close();
        super.onDestroy();
    }
}
