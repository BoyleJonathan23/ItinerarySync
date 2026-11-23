package com.example.itinerarysync.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.itinerarysync.Database.Repository;
import com.example.itinerarysync.Entities.Excursion;
import com.example.itinerarysync.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ExcursionDetails extends AppCompatActivity {

    private TextInputEditText etTitle;
    private TextInputEditText etDate;

    private Repository repository;
    private int excursionId = -1;
    private int vacationId = -1;
    private String vacationStartDate = "";
    private String vacationEndDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_excursion_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        repository = new Repository(getApplication());

        etTitle = findViewById(R.id.etExcursionTitle);
        etDate = findViewById(R.id.etExcursionDate);
        TextView tvRangeHint = findViewById(R.id.tvVacationRangeHint);
        MaterialButton btnSave = findViewById(R.id.btnSaveExcursion);

        etDate.setOnClickListener(v -> DateUtils.showDatePicker(this, etDate));

        Intent intent = getIntent();
        if (intent != null) {
            excursionId = intent.getIntExtra(VacationDetails.EXTRA_EXCURSION_ID, -1);
            vacationId = intent.getIntExtra(VacationDetails.EXTRA_VACATION_ID, -1);
            vacationStartDate = intent.getStringExtra(VacationDetails.EXTRA_VACATION_START_DATE);
            vacationEndDate = intent.getStringExtra(VacationDetails.EXTRA_VACATION_END_DATE);

            if (vacationStartDate == null) {
                vacationStartDate = "";
            }
            if (vacationEndDate == null) {
                vacationEndDate = "";
            }

            if (!vacationStartDate.isEmpty() && !vacationEndDate.isEmpty()) {
                tvRangeHint.setText(getString(R.string.allowed_vacation_window, vacationStartDate, vacationEndDate));
            } else {
                tvRangeHint.setText(R.string.within_parent_vacation_hint);
            }

            if (excursionId > 0) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Edit Excursion");
                }
                etTitle.setText(intent.getStringExtra(VacationDetails.EXTRA_EXCURSION_TITLE));
                etDate.setText(intent.getStringExtra(VacationDetails.EXTRA_EXCURSION_DATE));
            } else {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Add Excursion");
                }
            }
        }

        btnSave.setOnClickListener(v -> saveExcursion());
    }

    private void saveExcursion() {
        String title = getTextTrimmed(etTitle);
        String dateStr = getTextTrimmed(etDate);

        if (title.isEmpty()) {
            etTitle.setError("Title is required");
            etTitle.requestFocus();
            return;
        }
        if (dateStr.isEmpty()) {
            etDate.setError("Date is required");
            etDate.requestFocus();
            return;
        }

        LocalDate excursionDate;
        try {
            excursionDate = DateUtils.parse(dateStr);
        } catch (DateTimeParseException e) {
            etDate.setError("Invalid date format (MM/dd/yyyy)");
            Toast.makeText(this, "Date must follow MM/dd/yyyy format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate excursion date falls between vacation start and end dates
        if (!vacationStartDate.isEmpty() && !vacationEndDate.isEmpty()) {
            try {
                LocalDate vacStart = DateUtils.parse(vacationStartDate);
                LocalDate vacEnd = DateUtils.parse(vacationEndDate);

                if (excursionDate.isBefore(vacStart) || excursionDate.isAfter(vacEnd)) {
                    String errorMsg = "Excursion date must fall between vacation dates (" +
                            vacationStartDate + " – " + vacationEndDate + ").";
                    etDate.setError("Date out of range");
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                    return;
                }
            } catch (DateTimeParseException ignored) {
            }
        }

        String formattedDate = DateUtils.format(excursionDate);

        if (excursionId > 0) {
            Excursion updated = new Excursion(excursionId, title, formattedDate, vacationId);
            repository.update(updated);
            Toast.makeText(this, "Excursion updated", Toast.LENGTH_SHORT).show();
        } else {
            Excursion newExcursion = new Excursion(0, title, formattedDate, vacationId);
            repository.insert(newExcursion);
            Toast.makeText(this, "Excursion added", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void deleteExcursion() {
        if (excursionId <= 0) {
            finish();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete Excursion")
                .setMessage("Are you sure you want to delete this excursion?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    String title = getTextTrimmed(etTitle);
                    String dateStr = getTextTrimmed(etDate);
                    Excursion excursion = new Excursion(excursionId, title, dateStr, vacationId);
                    repository.delete(excursion);
                    Toast.makeText(this, "Excursion deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setExcursionAlert() {
        if (excursionId <= 0) {
            Toast.makeText(this, "Please save the excursion before setting alerts", Toast.LENGTH_SHORT).show();
            return;
        }
        String rawTitle = getTextTrimmed(etTitle);
        String title = rawTitle.isEmpty() ? "Excursion" : rawTitle;
        String dateStr = getTextTrimmed(etDate);
        if (dateStr.isEmpty()) {
            Toast.makeText(this, "Please select an excursion date first", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocalDate date = DateUtils.parse(dateStr);
            String displayDate = DateUtils.format(date);
            String message = "Your excursion \"" + title + "\" is scheduled for today (" + displayDate + ")!";
            int notificationId = (excursionId * 100) + 7;

            DateUtils.scheduleAlert(this, date, "Excursion Today!", message, notificationId);
            Toast.makeText(this, "Alert set for excursion date: " + displayDate, Toast.LENGTH_SHORT).show();
        } catch (DateTimeParseException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
        }
    }

    private String getTextTrimmed(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_excursion_details, menu);
        if (excursionId <= 0) {
            MenuItem deleteItem = menu.findItem(R.id.action_delete_excursion);
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
        } else if (id == R.id.action_delete_excursion) {
            deleteExcursion();
            return true;
        } else if (id == R.id.action_alert_excursion) {
            setExcursionAlert();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
