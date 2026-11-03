# BorrowBack

A personal lending tracker for books, chargers, tools, and other items. Each loan
stores an item name, borrower, borrowing date, expected return date, and optional
notes. Records stay on the device after the app closes.

## Run

Open this folder in Android Studio, allow Gradle to sync, select an emulator or
phone running Android 7.0 (API 24) or later, and click Run. Use Android Studio's
bundled JDK for Gradle. The existing SDK and Gradle configuration are retained.

## Screens

- Home: saved-loan and overdue counts; buttons to add, list, and search loans.
- Add a loan: required item, borrower, and dates; optional notes.
- All loans: a RecyclerView ordered by expected return date, earliest first.
- Search: case-insensitive partial matching of item names and borrower names.
- Loan details: full record and a deletion dialog with Cancel and Delete choices.

Tap the borrowing or expected return field to choose a date from a calendar.
Dates are saved as `YYYY-MM-DD`. A loan becomes overdue after its expected return date;
an item due today is still on loan. To close a loan after the item is returned,
delete its record. Deletion is permanent; the app does not keep a return history
or send reminder notifications. Multiple loans with identical names are allowed
and remain separate records.

## Source structure

Application Java classes are in `app/src/main/java/com/example/borrowback`.

- `DataManager`: SQLiteOpenHelper, CREATE TABLE, INSERT, DELETE, SELECT, and search.
- `Loan`: one lending record.
- `LoanAdapter` and `LoanViewHolder`: bind records to `res/layout/row_loan.xml`.
- `DeleteLoanDialog`: DialogFragment with confirmation and cancellation.
- `LoanDateDialog`: DialogFragment containing the date picker for either date field.
- `LoanDates`: date format checks and overdue calculation.
- Five activity classes: each has its own XML layout and uses explicit Intents.

The app uses Java, XML layouts, AppCompatActivity, findViewById, explicit click
listeners, ArrayList, RecyclerView, CardView, ScrollView, TableLayout, Toast, and
SQLite. The database follows the DataManager/helper structure in the provided
slides (PDF pages 213-223). Adapter/ViewHolder follow pages 160-174, dialogs
follow pages 147-151, and Intent data passing follows pages 186-194. The
system-bar inset handling comes from the generated Android Studio template and
is retained on each screen for compatibility with the project's target SDK.

SQL values are bound separately from SQL statements so apostrophes and other
user-entered characters are handled correctly. Deletion uses the unique record
ID rather than its item or borrower name. Cursors and helpers are closed after
use. Search and list screens reload after returning from details.

## Verification

See [TESTING.md](TESTING.md) for a manual acceptance checklist.

With Android Studio's JDK configured and an emulator or phone connected:

```powershell
.\gradlew.bat assembleDebug testDebugUnitTest lintDebug connectedDebugAndroidTest
```

Unit tests check date edge cases. Instrumented tests check SQLite persistence,
literal search characters, deletion by ID, all five activities, data passing,
dialog cancellation/confirmation, form recreation, invalid input, and missing
loan IDs. Tests remove their own uniquely named records.
