# BorrowBack manual checks

Verified on 3 November 2026 with the Pixel 4 emulator running Android 16
(API 36): the debug build succeeded, all 3 unit tests and 6 instrumented tests
passed, and Android lint reported 0 errors. Lint still reports advisory warnings,
including dependency-version suggestions and layout/input recommendations.
All five screens and the deletion dialog were inspected visually. List and
search were checked in portrait and landscape. A saved record remained after
force-stopping and reopening the app; the temporary manual test record was then
deleted. Other Android versions and physical devices have not been tested here.

Run the app from Android Studio. Use test records that you can delete afterward.

| Check | Steps | Expected result |
|---|---|---|
| Home | Open the app. | Loan and overdue counts are shown. All three navigation buttons work. |
| Save a loan | Add Scientific calculator, Ahmed Ali, borrowing date 2026-11-01, return date 2026-11-10, and notes With cover. | A Loan saved Toast appears. The app returns to the preceding screen, and the count increases by one. |
| List and data passing | Open All loans, then tap the calculator. | The details screen shows the exact item, borrower, dates, and notes you entered. |
| Partial search | Search for calc, then AHMED. | Both searches find the calculator loan. |
| No results | Search for a name that does not exist. | A clear no-results message appears. |
| Empty search | Clear the search box and tap Search. | A field error asks for an item or borrower name. |
| Required fields | Try saving without an item name, then without a borrower. | Each missing field is rejected, and no record is saved. |
| Date selection | Tap each date field and choose a date from the calendar. | The selected date appears in YYYY-MM-DD format; the typing keyboard does not open. |
| Cancel date selection | Open a date picker, choose another day, then Cancel. | The previously saved field value stays unchanged. |
| Missing return date | Fill in the item and borrower but leave the return date unselected. | Saving is blocked until a return date is selected. |
| Picker recreation | Open the expected return date picker, rotate the device, choose a date, and confirm. | The picker remains usable and updates the expected return field. |
| Date order | Use borrowing date 2026-11-03 and return date 2026-11-02. | An error explains that the return date cannot be earlier. Equal dates are allowed. |
| Optional notes | Save a loan without notes. | Saving succeeds; details say No notes added. |
| Special characters | Save a loan with borrower O'Brien and item 100%_ cable. Search those values. | Text is preserved and search treats % and _ literally. |
| Duplicate names | Save two records with the same item name but different borrowers. | Both appear independently. Deleting one leaves the other. |
| Cancel deletion | Open a loan, tap Delete loan, then Cancel. | The dialog closes and the record remains. |
| Confirm deletion | Open the dialog again and confirm Delete loan. | A Loan deleted Toast appears. The preceding list/search refreshes and the home count updates. |
| Persistence | Save a loan, close the app from recent apps, and reopen it. | The record is still present and its details are unchanged. |
| Form recreation | Enter an unsaved loan and rotate the device. | Entered text remains. Saving afterward creates only one record. |
| Search recreation | Run a search and rotate the device. | The query and its results remain. |
| Dialog recreation | Open a deletion dialog and rotate. Cancel it. | The dialog remains usable, and the loan is not deleted. |
| Overdue state | Add a loan with both dates in the past. | Its card and details show Past return date; the home overdue count increases. A return date equal to today's device date is not overdue. |
| Navigation | Use Back on every screen and the Android system Back control. | Screens return to the preceding screen without crashes. |
| Small screen and keyboard | Open the add form, show the keyboard, and scroll to notes and Save loan. | Fields and the Save button remain reachable. |
| Large text | Increase the device font size, then check each screen in portrait and landscape. | Text wraps and scrollable content remains reachable. |
| Empty collection | Delete all test records. Open All loans. | The empty message appears; counts return to zero if there are no other records. |

Keep screenshots of the five screens and deletion dialog once you are satisfied
with the app. Report preparation is a separate step and has not started.
