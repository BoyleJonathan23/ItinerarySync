package com.example.itinerarysync.UI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itinerarysync.Database.Repository;
import com.example.itinerarysync.Entities.Vacation;
import com.example.itinerarysync.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class VacationList extends AppCompatActivity {

    public static final String EXTRA_VACATION_ID = "com.example.itinerarysync.EXTRA_VACATION_ID";
    public static final String EXTRA_VACATION_TITLE = "com.example.itinerarysync.EXTRA_VACATION_TITLE";
    public static final String EXTRA_VACATION_HOTEL = "com.example.itinerarysync.EXTRA_VACATION_HOTEL";
    public static final String EXTRA_VACATION_START_DATE = "com.example.itinerarysync.EXTRA_VACATION_START_DATE";
    public static final String EXTRA_VACATION_END_DATE = "com.example.itinerarysync.EXTRA_VACATION_END_DATE";

    private Repository repository;
    private VacationAdapter adapter;
    private TextView tvEmpty;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_vacation_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.vacation_list_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        repository = new Repository(getApplication());

        RecyclerView recyclerView = findViewById(R.id.rvVacations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VacationAdapter(this);
        recyclerView.setAdapter(adapter);

        tvEmpty = findViewById(R.id.tvEmptyVacations);
        searchView = findViewById(R.id.searchViewVacations);

        adapter.setOnVacationClickListener(vacation -> {
            Intent intent = new Intent(VacationList.this, VacationDetails.class);
            intent.putExtra(EXTRA_VACATION_ID, vacation.getId());
            intent.putExtra(EXTRA_VACATION_TITLE, vacation.getTitle());
            intent.putExtra(EXTRA_VACATION_HOTEL, vacation.getHotel());
            intent.putExtra(EXTRA_VACATION_START_DATE, vacation.getStartDate());
            intent.putExtra(EXTRA_VACATION_END_DATE, vacation.getEndDate());
            startActivity(intent);
        });

        FloatingActionButton fab = findViewById(R.id.fabAddVacation);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(VacationList.this, VacationDetails.class);
            intent.putExtra(EXTRA_VACATION_ID, -1);
            startActivity(intent);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                updateEmptyState();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                updateEmptyState();
                return true;
            }
        });

        requestNotificationPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        List<Vacation> vacations = repository.getAllVacations();
        adapter.setVacations(vacations);
        if (searchView != null && searchView.getQuery() != null && !searchView.getQuery().toString().isEmpty()) {
            adapter.filter(searchView.getQuery().toString());
        }
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (adapter.getItemCount() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_report) {
            Intent intent = new Intent(VacationList.this, ReportActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }
}
