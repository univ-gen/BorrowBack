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

import android.widget.EditText;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.KeyEvent;
import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class SearchLoanActivity extends AppCompatActivity implements View.OnClickListener {
    private DataManager dm;
    private EditText editSearch;
    private RecyclerView rvLoans;
    private TextView txtEmpty;
    private TextView txtResultCount;
    private String lastQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_loan);
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
        editSearch = findViewById(R.id.editSearch);
        rvLoans = findViewById(R.id.rvLoans);
        rvLoans.setLayoutManager(new LinearLayoutManager(this));
        txtEmpty = findViewById(R.id.txtEmpty);
        txtResultCount = findViewById(R.id.txtResultCount);
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnSearch).setOnClickListener(this);
        if (savedInstanceState != null) {
            lastQuery = savedInstanceState.getString("last_query");
        }
        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchLoans();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lastQuery != null) { showResults(); }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnBack) { finish(); }
        else if (view.getId() == R.id.btnSearch) { searchLoans(); }
    }

    private void searchLoans() {
        String query = editSearch.getText().toString().trim();
        if (query.isEmpty()) {
            editSearch.setError(getString(R.string.search_required));
            editSearch.requestFocus();
            return;
        }
        lastQuery = query;
        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
        editSearch.clearFocus();
        showResults();
    }

    private void showResults() {
        ArrayList<Loan> loans = dm.readLoans(dm.search(lastQuery));
        rvLoans.setAdapter(new LoanAdapter(this, loans));
        rvLoans.setVisibility(loans.isEmpty() ? View.GONE : View.VISIBLE);
        txtEmpty.setVisibility(loans.isEmpty() ? View.VISIBLE : View.GONE);
        txtEmpty.setText(R.string.no_results);
        txtResultCount.setText(getResources().getQuantityString(
                R.plurals.result_count, loans.size(), loans.size(), lastQuery));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("last_query", lastQuery);
    }

    @Override
    protected void onDestroy() {
        dm.close();
        super.onDestroy();
    }
}
