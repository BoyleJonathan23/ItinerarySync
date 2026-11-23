package com.example.itinerarysync.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itinerarysync.Database.Repository;
import com.example.itinerarysync.Entities.Excursion;
import com.example.itinerarysync.Entities.Vacation;
import com.example.itinerarysync.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class VacationDetails extends AppCompatActivity {

    public static final String EXTRA_EXCURSION_ID = "com.example.itinerarysync.EXTRA_EXCURSION_ID";
    public static final String EXTRA_EXCURSION_TITLE = "com.example.itinerarysync.EXTRA_EXCURSION_TITLE";
    public static final String EXTRA_EXCURSION_DATE = "com.example.itinerarysync.EXTRA_EXCURSION_DATE";
    public static final String EXTRA_VACATION_ID = "com.example.itinerarysync.EXTRA_VACATION_ID";
    public static final String EXTRA_VACATION_START_DATE = "com.example.itinerarysync.EXTRA_VACATION_START_DATE";
    public static final String EXTRA_VACATION_END_DATE = "com.example.itinerarysync.EXTRA_VACATION_END_DATE";

    private TextInputEditText etTitle;
    private TextInputEditText etHotel;
    private TextInputEditText etStartDate;
    private TextInputEditText etEndDate;
    private TextView tvNoExcursions;
    private ExcursionAdapter excursionAdapter;

    private Repository repository;
    private int vacationId = -1;
    private List<Excursion> currentExcursions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_vacation_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        repository = new Repository(getApplication());

        etTitle = findViewById(R.id.etVacationTitle);
        etHotel = findViewById(R.id.etVacationHotel);
        etStartDate = findViewById(R.id.etVacationStartDate);
        etEndDate = findViewById(R.id.etVacationEndDate);
        tvNoExcursions = findViewById(R.id.tvNoExcursions);
        MaterialButton btnSave = findViewById(R.id.btnSaveVacation);
        MaterialButton btnAddExcursion = findViewById(R.id.btnAddExcursion);
        RecyclerView rvExcursions = findViewById(R.id.rvExcursions);

        rvExcursions.setLayoutManager(new LinearLayoutManager(this));
        excursionAdapter = new ExcursionAdapter(this);
        rvExcursions.setAdapter(excursionAdapter);

        // Date picker listeners
        etStartDate.setOnClickListener(v -> DateUtils.showDatePicker(this, etStartDate));
        etEndDate.setOnClickListener(v -> DateUtils.showDatePicker(this, etEndDate));

        // Read intent data
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(VacationList.EXTRA_VACATION_ID)) {
            vacationId = intent.getIntExtra(VacationList.EXTRA_VACATION_ID, -1);
        }

        if (vacationId > 0 && intent != null) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit Vacation");
            }
            etTitle.setText(intent.getStringExtra(VacationList.EXTRA_VACATION_TITLE));
            etHotel.setText(intent.getStringExtra(VacationList.EXTRA_VACATION_HOTEL));
            etStartDate.setText(intent.getStringExtra(VacationList.EXTRA_VACATION_START_DATE));
            etEndDate.setText(intent.getStringExtra(VacationList.EXTRA_VACATION_END_DATE));
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Add Vacation");
            }
            btnAddExcursion.setVisibility(View.GONE);
            tvNoExcursions.setText(R.string.save_first_to_add_excursions);
            tvNoExcursions.setVisibility(View.VISIBLE);
        }

        btnSave.setOnClickListener(v -> saveVacation());

        btnAddExcursion.setOnClickListener(v -> {
            if (vacationId <= 0) {
                Toast.makeText(this, R.string.save_first_to_add_excursions, Toast.LENGTH_SHORT).show();
                return;
            }
            Intent addIntent = new Intent(VacationDetails.this, ExcursionDetails.class);
            addIntent.putExtra(EXTRA_VACATION_ID, vacationId);
            addIntent.putExtra(EXTRA_VACATION_START_DATE, getTextTrimmed(etStartDate));
            addIntent.putExtra(EXTRA_VACATION_END_DATE, getTextTrimmed(etEndDate));
            startActivity(addIntent);
        });

        excursionAdapter.setOnExcursionClickListener(excursion -> {
            Intent editIntent = new Intent(VacationDetails.this, ExcursionDetails.class);
            editIntent.putExtra(EXTRA_EXCURSION_ID, excursion.getId());
            editIntent.putExtra(EXTRA_EXCURSION_TITLE, excursion.getTitle());
            editIntent.putExtra(EXTRA_EXCURSION_DATE, excursion.getStartDate());
            editIntent.putExtra(EXTRA_VACATION_ID, vacationId);
            editIntent.putExtra(EXTRA_VACATION_START_DATE, getTextTrimmed(etStartDate));
            editIntent.putExtra(EXTRA_VACATION_END_DATE, getTextTrimmed(etEndDate));
            startActivity(editIntent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (vacationId > 0) {
            refreshExcursions();
        }
    }

    private void refreshExcursions() {
        currentExcursions = repository.getAssociatedExcursions(vacationId);
        excursionAdapter.setExcursions(currentExcursions);
        if (currentExcursions == null || currentExcursions.isEmpty()) {
            tvNoExcursions.setText(R.string.no_excursions_found);
            tvNoExcursions.setVisibility(View.VISIBLE);
        } else {
            tvNoExcursions.setVisibility(View.GONE);
        }
    }

    private void saveVacation() {
        String title = getTextTrimmed(etTitle);
        String hotel = getTextTrimmed(etHotel);
        String startStr = getTextTrimmed(etStartDate);
        String endStr = getTextTrimmed(etEndDate);

        if (title.isEmpty()) {
            etTitle.setError("Title is required");
            etTitle.requestFocus();
            return;
        }
        if (hotel.isEmpty()) {
            etHotel.setError("Hotel is required");
            etHotel.requestFocus();
            return;
        }
        if (startStr.isEmpty()) {
            etStartDate.setError("Start date is required");
            etStartDate.requestFocus();
            return;
        }
        if (endStr.isEmpty()) {
            etEndDate.setError("End date is required");
            etEndDate.requestFocus();
            return;
        }

        LocalDate startDate;
        LocalDate endDate;

        try {
            startDate = DateUtils.parse(startStr);
        } catch (DateTimeParseException e) {
            etStartDate.setError("Invalid format (MM/dd/yyyy)");
            Toast.makeText(this, "Start date must be in MM/dd/yyyy format", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            endDate = DateUtils.parse(endStr);
        } catch (DateTimeParseException e) {
            etEndDate.setError("Invalid format (MM/dd/yyyy)");
            Toast.makeText(this, "End date must be in MM/dd/yyyy format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (endDate.isBefore(startDate)) {
            etEndDate.setError("End date cannot be before start date");
            Toast.makeText(this, "End date cannot be before start date", Toast.LENGTH_SHORT).show();
            return;
        }

        String formattedStart = DateUtils.format(startDate);
        String formattedEnd = DateUtils.format(endDate);

        if (vacationId > 0) {
            Vacation vacation = new Vacation(vacationId, title, formattedStart, hotel, formattedEnd);
            repository.update(vacation);
            Toast.makeText(this, "Vacation updated", Toast.LENGTH_SHORT).show();
        } else {
            Vacation vacation = new Vacation(0, title, formattedStart, hotel, formattedEnd);
            repository.insert(vacation);
            Toast.makeText(this, "Vacation saved", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void deleteVacation() {
        if (vacationId <= 0) {
            finish();
            return;
        }

        List<Excursion> associated = repository.getAssociatedExcursions(vacationId);
        if (associated != null && !associated.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Cannot Delete Vacation")
                    .setMessage("This vacation cannot be deleted because it still has " +
                            associated.size() + " associated excursion(s). Please delete all associated excursions first.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete Vacation")
                .setMessage("Are you sure you want to delete this vacation?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    String title = getTextTrimmed(etTitle);
                    String hotel = getTextTrimmed(etHotel);
                    String startStr = getTextTrimmed(etStartDate);
                    String endStr = getTextTrimmed(etEndDate);
                    Vacation vacation = new Vacation(vacationId, title, startStr, hotel, endStr);
                    repository.delete(vacation);
                    Toast.makeText(this, "Vacation deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void shareVacation() {
        String title = getTextTrimmed(etTitle);
        String hotel = getTextTrimmed(etHotel);
        String startStr = getTextTrimmed(etStartDate);
        String endStr = getTextTrimmed(etEndDate);

        StringBuilder sb = new StringBuilder();
        sb.append("Vacation Itinerary: ").append(title).append("\n");
        sb.append("Hotel / Lodging: ").append(hotel).append("\n");
        sb.append("Dates: ").append(startStr).append(" to ").append(endStr).append("\n\n");
        sb.append("Associated Excursions:\n");

        if (currentExcursions != null && !currentExcursions.isEmpty()) {
            for (Excursion excursion : currentExcursions) {
                sb.append("• ").append(excursion.getTitle())
                        .append(" - ").append(excursion.getStartDate()).append("\n");
            }
        } else {
            sb.append("No excursions scheduled.\n");
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Vacation Itinerary: " + title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        startActivity(Intent.createChooser(shareIntent, "Share Vacation Details"));
    }

    private void setDateAlert(boolean isStartDate) {
        if (vacationId <= 0) {
            Toast.makeText(this, "Please save the vacation before setting alerts", Toast.LENGTH_SHORT).show();
            return;
        }

        String dateStr = isStartDate ? getTextTrimmed(etStartDate) : getTextTrimmed(etEndDate);
        String rawTitle = getTextTrimmed(etTitle);
        String title = rawTitle.isEmpty() ? "Vacation" : rawTitle;

        if (dateStr.isEmpty()) {
            Toast.makeText(this, "Please select a date first", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocalDate date = DateUtils.parse(dateStr);
            String displayDate = DateUtils.format(date);
            String alertType = isStartDate ? "starting" : "ending";
            String alertTitle = isStartDate ? "Vacation Starting!" : "Vacation Ending!";
            String message = "Your vacation \"" + title + "\" is " + alertType + " today (" + displayDate + ")!";
            int notificationId = (vacationId * 10) + (isStartDate ? 1 : 2);

            DateUtils.scheduleAlert(this, date, alertTitle, message, notificationId);
            Toast.makeText(this, "Alert set for " + (isStartDate ? "start" : "end") + " date: " + displayDate, Toast.LENGTH_SHORT).show();
        } catch (DateTimeParseException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
        }
    }

    private String getTextTrimmed(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation_details, menu);
        if (vacationId <= 0) {
            MenuItem deleteItem = menu.findItem(R.id.action_delete_vacation);
            if (deleteItem != null) {
                deleteItem.setVisible(false);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_delete_vacation) {
            deleteVacation();
            return true;
        } else if (id == R.id.action_share_vacation) {
            shareVacation();
            return true;
        } else if (id == R.id.action_alert_start) {
            setDateAlert(true);
            return true;
        } else if (id == R.id.action_alert_end) {
            setDateAlert(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
