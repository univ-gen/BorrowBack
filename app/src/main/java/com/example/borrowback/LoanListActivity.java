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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class LoanListActivity extends AppCompatActivity implements View.OnClickListener {
    private DataManager dm;
    private RecyclerView rvLoans;
    private TextView txtEmpty;
    private TextView txtResultCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loan_list);
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
        rvLoans = findViewById(R.id.rvLoans);
        rvLoans.setLayoutManager(new LinearLayoutManager(this));
        txtEmpty = findViewById(R.id.txtEmpty);
        txtResultCount = findViewById(R.id.txtResultCount);
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnAddLoan).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<Loan> loans = dm.readLoans(dm.selectAll());
        rvLoans.setAdapter(new LoanAdapter(this, loans));
        txtEmpty.setVisibility(loans.isEmpty() ? View.VISIBLE : View.GONE);
        rvLoans.setVisibility(loans.isEmpty() ? View.GONE : View.VISIBLE);
        txtResultCount.setText(getResources().getQuantityString(
                R.plurals.loan_count, loans.size(), loans.size()));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnBack) { finish(); }
        else if (view.getId() == R.id.btnAddLoan) {
            startActivity(new Intent(this, AddLoanActivity.class));
        }
    }

    @Override
    protected void onDestroy() {
        dm.close();
        super.onDestroy();
    }
}
