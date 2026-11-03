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
import android.widget.Button;
import android.widget.EditText;

public class AddLoanActivity extends AppCompatActivity implements View.OnClickListener {
    private DataManager dm;
    private EditText editItemName;
    private EditText editBorrower;
    private EditText editBorrowedDate;
    private EditText editReturnDate;
    private EditText editNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_loan);
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
        editItemName = findViewById(R.id.editItemName);
        editBorrower = findViewById(R.id.editBorrower);
        editBorrowedDate = findViewById(R.id.editBorrowedDate);
        editReturnDate = findViewById(R.id.editReturnDate);
        editNotes = findViewById(R.id.editNotes);
        editBorrowedDate.setOnClickListener(this);
        editReturnDate.setOnClickListener(this);
        if (savedInstanceState == null) { editBorrowedDate.setText(LoanDates.today()); }
        findViewById(R.id.btnSaveLoan).setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnBack) { finish(); }
        else if (view.getId() == R.id.btnSaveLoan) { saveLoan(); }
        else if (view.getId() == R.id.editBorrowedDate || view.getId() == R.id.editReturnDate) {
            showDatePicker((EditText) view);
        }
    }

    private void showDatePicker(EditText field) {
        if (getSupportFragmentManager().findFragmentByTag("loan_date") != null) {
            return;
        }
        android.view.inputmethod.InputMethodManager keyboard =
                (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(field.getWindowToken(), 0);
        Bundle arguments = new Bundle();
        arguments.putInt("field_id", field.getId());
        String selectedDate = field.getText().toString();
        if (selectedDate.isEmpty()) {
            selectedDate = editBorrowedDate.getText().toString();
        }
        arguments.putString("selected_date", selectedDate);
        LoanDateDialog dialog = new LoanDateDialog();
        dialog.setArguments(arguments);
        dialog.show(getSupportFragmentManager(), "loan_date");
    }

    public void setLoanDate(int fieldId, String value) {
        EditText field = findViewById(fieldId);
        field.setText(value);
        field.setError(null);
    }

    private void saveLoan() {
        String itemName = editItemName.getText().toString().trim();
        String borrower = editBorrower.getText().toString().trim();
        String borrowedDate = editBorrowedDate.getText().toString().trim();
        String returnDate = editReturnDate.getText().toString().trim();
        String notes = editNotes.getText().toString().trim();
        if (itemName.isEmpty()) {
            editItemName.setError(getString(R.string.item_required));
            editItemName.requestFocus();
            return;
        }
        if (borrower.isEmpty()) {
            editBorrower.setError(getString(R.string.borrower_required));
            editBorrower.requestFocus();
            return;
        }
        if (!LoanDates.isValid(borrowedDate)) {
            editBorrowedDate.setError(getString(R.string.invalid_date));
            editBorrowedDate.requestFocus();
            return;
        }
        if (!LoanDates.isValid(returnDate)) {
            editReturnDate.setError(getString(R.string.invalid_date));
            editReturnDate.requestFocus();
            return;
        }
        if (returnDate.compareTo(borrowedDate) < 0) {
            editReturnDate.setError(getString(R.string.date_order_error));
            editReturnDate.requestFocus();
            return;
        }
        Button button = findViewById(R.id.btnSaveLoan);
        button.setEnabled(false);
        try {
            dm.insert(itemName, borrower, borrowedDate, returnDate, notes);
            Toast.makeText(this, R.string.loan_saved, Toast.LENGTH_SHORT).show();
            finish();
        } catch (SQLiteException exception) {
            button.setEnabled(true);
            Toast.makeText(this, R.string.save_failed, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        dm.close();
        super.onDestroy();
    }
}
