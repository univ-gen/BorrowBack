package com.example.borrowback;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Locale;

public class LoanDateDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = requireArguments();
        final int fieldId = arguments.getInt("field_id");
        String selectedDate = arguments.getString("selected_date", "");
        Calendar date = Calendar.getInstance();
        if (LoanDates.isValid(selectedDate)) {
            String[] parts = selectedDate.split("-");
            date.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1,
                    Integer.parseInt(parts[2]));
        }
        DatePickerDialog dialog = new DatePickerDialog(requireActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker picker, int year, int month, int day) {
                        String value = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
                        ((AddLoanActivity) requireActivity()).setLoanDate(fieldId, value);
                    }
                }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
        dialog.setTitle(fieldId == R.id.editBorrowedDate
                ? R.string.borrowed_label : R.string.return_label);
        return dialog;
    }
}
