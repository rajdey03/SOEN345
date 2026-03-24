package com.example.ticketreservationapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class EventBrowseActivity extends AppCompatActivity {

    private RecyclerView recyclerViewEvents;
    private EventAdapter eventAdapter;
    private List<Event> dummyEventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_browse);

        recyclerViewEvents = findViewById(R.id.recyclerViewEvents);

        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));

        loadDummyData();

        eventAdapter = new EventAdapter(dummyEventList);
        recyclerViewEvents.setAdapter(eventAdapter);
    }

    private void loadDummyData() {
        dummyEventList = new ArrayList<>();

        dummyEventList.add(new Event("1", "Summer Music Festival", "CONCERT", "Parc Jean-Drapeau, Montreal", "July 15, 2026 - 4:00 PM", 500));

        dummyEventList.add(new Event("2", "Local Derby: CF Montreal vs TFC", "SPORTS", "Saputo Stadium", "September 5, 2026 - 7:30 PM", 85));

        dummyEventList.add(new Event("3", "Indie Film Premiere", "MOVIE", "Cinema Banque Scotia", "October 12, 2026 - 8:00 PM", 12));

        dummyEventList.add(new Event("4", "Weekend Getaway: Bus to Quebec City", "TRAVEL", "Gare d'autocars de Montreal", "November 20, 2026 - 8:00 AM", 45));
    }
}