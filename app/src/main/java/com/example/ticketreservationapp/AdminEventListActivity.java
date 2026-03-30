package com.example.ticketreservationapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdminEventListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_event_list);

        Button btnAddEvent = findViewById(R.id.btnAddEvent);
        RecyclerView recyclerViewAdminEvents = findViewById(R.id.recyclerViewAdminEvents);

        recyclerViewAdminEvents.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdminEvents.setAdapter(new AdminEventAdapter(buildPlaceholderEvents()));

        btnAddEvent.setOnClickListener(v -> {
            Intent intent = new Intent(AdminEventListActivity.this, AdminEventFormActivity.class);
            intent.putExtra(AdminEventFormActivity.EXTRA_MODE, AdminEventFormActivity.MODE_CREATE);
            startActivity(intent);
        });
    }

    private List<AdminEvent> buildPlaceholderEvents() {
        List<AdminEvent> adminEvents = new ArrayList<>();

        adminEvents.add(new AdminEvent(
                "event-001",
                "org-001",
                "Montreal Jazz Evening",
                "A night of live jazz performances.",
                "CONCERT",
                "Place des Arts",
                "2026-06-14",
                "19:00",
                "22:00",
                180,
                "ACTIVE"
        ));

        adminEvents.add(new AdminEvent(
                "event-002",
                "org-001",
                "City Food Expo",
                "A downtown showcase of local restaurants.",
                "EXPO",
                "Palais des congres",
                "2026-07-08",
                "10:00",
                "17:00",
                300,
                "ACTIVE"
        ));

        adminEvents.add(new AdminEvent(
                "event-003",
                "org-002",
                "Late Night Screening",
                "Special one-night film screening.",
                "MOVIE",
                "Cinema Banque Scotia",
                "2026-08-01",
                "21:00",
                "23:30",
                120,
                "CANCELLED"
        ));

        return adminEvents;
    }
}
