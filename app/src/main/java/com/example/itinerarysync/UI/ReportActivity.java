package com.example.itinerarysync.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.itinerarysync.Database.Repository;
import com.example.itinerarysync.Entities.Excursion;
import com.example.itinerarysync.Entities.Vacation;
import com.example.itinerarysync.R;
import com.google.android.material.button.MaterialButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    private Repository repository;
    private TextView tvReport;
    private String generatedReportText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_report);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.master_schedule_report);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        repository = new Repository(getApplication());
        tvReport = findViewById(R.id.tvReportContent);
        MaterialButton btnShare = findViewById(R.id.btnShareReport);

        generateAndDisplayReport();

        btnShare.setOnClickListener(v -> shareReport());
    }

    private void generateAndDisplayReport() {
        List<Vacation> vacations = repository.getAllVacations();
        List<Excursion> allExcursions = repository.getAllExcursions();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a", Locale.US);
        String formattedTimestamp = now.format(timeFormatter);

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("   ITINERARYSYNC MASTER SCHEDULE REPORT \n");
        sb.append("========================================\n\n");
        sb.append("Report Title: Complete Itinerary Master Schedule\n");
        sb.append("Generated On: ").append(formattedTimestamp).append("\n");
        sb.append("Total Vacations: ").append(vacations.size()).append("\n");
        sb.append("Total Excursions: ").append(allExcursions.size()).append("\n");
        sb.append("----------------------------------------\n\n");

        if (vacations.isEmpty()) {
            sb.append("No vacations currently scheduled.\n");
        } else {
            int vacationIndex = 1;
            for (Vacation v : vacations) {
                sb.append("VACATION #").append(vacationIndex++).append(": ").append(v.getTitle()).append("\n");
                sb.append("  • Hotel / Lodging : ").append(v.getHotel()).append("\n");
                sb.append("  • Travel Dates    : ").append(v.getStartDate()).append(" to ").append(v.getEndDate()).append("\n");

                List<Excursion> excursions = repository.getAssociatedExcursions(v.getId());
                sb.append("  • Excursions (").append(excursions.size()).append("):\n");

                if (excursions.isEmpty()) {
                    sb.append("      - None scheduled\n");
                } else {
                    int excursionIndex = 1;
                    for (Excursion e : excursions) {
                        sb.append("      ").append(excursionIndex++)
                                .append(") ")
                                .append(e.getTitle())
                                .append(" [Date: ").append(e.getStartDate()).append("]\n");
                    }
                }
                sb.append("----------------------------------------\n\n");
            }
        }

        sb.append("========================================\n");
        sb.append("            END OF REPORT               \n");
        sb.append("========================================\n");

        generatedReportText = sb.toString();
        tvReport.setText(generatedReportText);
    }

    private void shareReport() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Master Schedule Report - ItinerarySync");
        shareIntent.putExtra(Intent.EXTRA_TEXT, generatedReportText);
        startActivity(Intent.createChooser(shareIntent, "Share Master Schedule Report"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_share_report) {
            shareReport();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
