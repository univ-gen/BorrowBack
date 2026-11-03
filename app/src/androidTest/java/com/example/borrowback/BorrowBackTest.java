package com.example.borrowback;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.DatePicker;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.UiController;
import org.hamcrest.Matcher;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class BorrowBackTest {
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    private void selectDate(int fieldId, int year, int month, int day) {
        onView(withId(fieldId)).perform(scrollTo(), click());
        setPickerDate(year, month, day);
    }

    private void setPickerDate(final int year, final int month, final int day) {
        onView(isAssignableFrom(DatePicker.class)).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() { return isAssignableFrom(DatePicker.class); }

            @Override
            public String getDescription() { return "Choose a calendar date"; }

            @Override
            public void perform(UiController controller, View view) {
                ((DatePicker) view).updateDate(year, month - 1, day);
                controller.loopMainThreadUntilIdle();
            }
        });
        onView(withId(android.R.id.button1)).perform(click());
    }

    @Test
    public void databasePersistsSearchesLiteralTextAndDeletesOnlyChosenId() {
        String item = "BB_SQL_" + System.nanoTime() + " 100%_ O'Brien";
        DataManager dm = new DataManager(context);
        ArrayList<Long> ids = new ArrayList<>();
        try {
            dm.insert(item, "Sara", "2026-11-01", "2026-11-10", "Cable included");
            dm.insert(item, "O'Brien", "2026-11-02", "2026-11-11", "");
            ArrayList<Loan> loans = dm.readLoans(dm.search(item));
            assertEquals(2, loans.size());
            for (Loan loan : loans) { ids.add(loan.getId()); }
            assertEquals(2, dm.readLoans(dm.search(item.toLowerCase())).size());
            long firstId = ids.get(0);
            dm.close();
            dm = new DataManager(context);
            assertEquals("Cable included", dm.findLoan(firstId).getNotes());
            boolean found = false;
            for (Loan loan : dm.readLoans(dm.selectAll())) {
                if (loan.getId() == firstId) { found = true; }
            }
            assertTrue(found);
            dm.delete(firstId);
            assertNull(dm.findLoan(firstId));
            assertEquals(1, dm.readLoans(dm.search(item)).size());
            assertNotNull(dm.findLoan(ids.get(1)));
        } finally {
            // Remove only this test's uniquely named loans.
            for (Loan loan : dm.readLoans(dm.search(item))) { dm.delete(loan.getId()); }
            dm.close();
        }
    }

    @Test
    public void completeLoanFlowAcrossAllFiveActivities() {
        String item = "BB_UI_" + System.nanoTime();
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.btnAddLoan)).perform(scrollTo(), click());
            onView(withId(R.id.editItemName)).perform(scrollTo(), replaceText(item), closeSoftKeyboard());
            onView(withId(R.id.editBorrower)).perform(scrollTo(), replaceText("O'Brien"), closeSoftKeyboard());
            selectDate(R.id.editBorrowedDate, 2000, 1, 1);
            selectDate(R.id.editReturnDate, 2000, 1, 2);
            onView(withId(R.id.editNotes)).perform(scrollTo(), replaceText("With case"), closeSoftKeyboard());
            onView(withId(R.id.btnSaveLoan)).perform(scrollTo(), click());
            onView(withId(R.id.btnAllLoans)).perform(scrollTo(), click());
            onView(allOf(withId(R.id.txtItemName), withText(item))).perform(click());
            onView(withId(R.id.txtDetailBorrower)).check(matches(withText("O'Brien")));
            onView(withId(R.id.txtDetailNotes)).check(matches(withText("With case")));
            onView(withId(R.id.btnDeleteLoan)).perform(scrollTo(), click());
            onView(withText(R.string.cancel)).perform(click());
            onView(withId(R.id.txtDetailItem)).check(matches(withText(item)));
            onView(withId(R.id.btnBack)).perform(scrollTo(), click());
            onView(withId(R.id.btnBack)).perform(click());
            onView(withId(R.id.btnSearchLoans)).perform(scrollTo(), click());
            onView(withId(R.id.editSearch)).perform(replaceText(item), closeSoftKeyboard());
            onView(withId(R.id.btnSearch)).perform(click());
            onView(allOf(withId(R.id.txtItemName), withText(item))).perform(click());
            onView(withId(R.id.btnDeleteLoan)).perform(scrollTo(), click());
            onView(withText(R.string.delete_loan)).perform(click());
            onView(withId(R.id.txtEmpty)).check(matches(withText(R.string.no_results)));
        } finally {
            DataManager dm = new DataManager(context);
            for (Loan loan : dm.readLoans(dm.search(item))) { dm.delete(loan.getId()); }
            dm.close();
        }
    }

    @Test
    public void formKeepsInputAfterRecreationAndRejectsEarlierReturn() {
        try (ActivityScenario<AddLoanActivity> scenario = ActivityScenario.launch(AddLoanActivity.class)) {
            onView(withId(R.id.btnSaveLoan)).perform(scrollTo(), click());
            onView(withId(R.id.editItemName)).check(matches(hasErrorText(context.getString(R.string.item_required))));
            onView(withId(R.id.editItemName)).perform(scrollTo(), replaceText("Validation test"), closeSoftKeyboard());
            onView(withId(R.id.editBorrower)).perform(scrollTo(), replaceText("Sara"), closeSoftKeyboard());
            selectDate(R.id.editBorrowedDate, 2026, 11, 3);
            scenario.recreate();
            onView(withId(R.id.editItemName)).check(matches(withText("Validation test")));
            onView(withId(R.id.editBorrowedDate)).check(matches(withText("2026-11-03")));
            onView(withId(R.id.btnSaveLoan)).perform(scrollTo(), click());
            onView(withId(R.id.editReturnDate)).check(matches(hasErrorText(context.getString(R.string.invalid_date))));
            selectDate(R.id.editReturnDate, 2026, 11, 2);
            onView(withId(R.id.btnSaveLoan)).perform(scrollTo(), click());
            onView(withId(R.id.editReturnDate)).check(matches(hasErrorText(context.getString(R.string.date_order_error))));
        }
    }

    @Test
    public void datePickerSurvivesRecreationAndCancelKeepsDate() {
        try (ActivityScenario<AddLoanActivity> scenario = ActivityScenario.launch(AddLoanActivity.class)) {
            selectDate(R.id.editBorrowedDate, 2026, 11, 3);
            onView(withId(R.id.editReturnDate)).perform(scrollTo(), click());
            scenario.recreate();
            onView(isAssignableFrom(DatePicker.class)).check(matches(isDisplayed()));
            setPickerDate(2026, 11, 15);
            onView(withId(R.id.editReturnDate)).check(matches(withText("2026-11-15")));
            onView(withId(R.id.editReturnDate)).perform(scrollTo(), click());
            onView(withId(android.R.id.button2)).perform(click());
            onView(withId(R.id.editReturnDate)).check(matches(withText("2026-11-15")));
        }
    }

    @Test
    public void landscapeKeepsLoanListsAndSearchResultsVisible() {
        String item = "BB_LAND_" + System.nanoTime();
        DataManager dm = new DataManager(context);
        dm.insert(item, "Sara", "2000-01-01", "2000-01-02", "");
        try {
            try (ActivityScenario<LoanListActivity> scenario = ActivityScenario.launch(LoanListActivity.class)) {
                scenario.onActivity(new ActivityScenario.ActivityAction<LoanListActivity>() {
                    @Override
                    public void perform(LoanListActivity activity) {
                        activity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                });
                onView(allOf(withId(R.id.txtItemName), withText(item))).check(matches(isDisplayed()));
            }
            try (ActivityScenario<SearchLoanActivity> scenario = ActivityScenario.launch(SearchLoanActivity.class)) {
                scenario.onActivity(new ActivityScenario.ActivityAction<SearchLoanActivity>() {
                    @Override
                    public void perform(SearchLoanActivity activity) {
                        activity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                });
                onView(withId(R.id.editSearch)).perform(replaceText(item), closeSoftKeyboard());
                onView(withId(R.id.btnSearch)).perform(click());
                onView(allOf(withId(R.id.txtItemName), withText(item))).check(matches(isDisplayed()));
            }
        } finally {
            for (Loan loan : dm.readLoans(dm.search(item))) { dm.delete(loan.getId()); }
            dm.close();
        }
    }

    @Test
    public void missingLoanIdentifierClosesDetailsSafely() {
        Intent intent = new Intent(context, LoanDetailsActivity.class);
        try (ActivityScenario<LoanDetailsActivity> scenario = ActivityScenario.launch(intent)) {
            assertEquals(androidx.lifecycle.Lifecycle.State.DESTROYED, scenario.getState());
        }
    }
}
