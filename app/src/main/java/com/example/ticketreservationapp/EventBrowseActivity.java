package com.example.ticketreservationapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventBrowseActivity extends AppCompatActivity {

    private RecyclerView recyclerViewEvents;
    private EventAdapter eventAdapter;
    private List<Event> backendEventList;

    private EditText etSearchEvents;
    private Spinner spinnerCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_browse);

        recyclerViewEvents = findViewById(R.id.recyclerViewEvents);
        etSearchEvents = findViewById(R.id.etSearchEvents);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        Button btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                Intent intent = new Intent(EventBrowseActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }

        Button btnMyReservations = findViewById(R.id.btnMyReservations);
        btnMyReservations.setOnClickListener(v -> {
            startActivity(new android.content.Intent(EventBrowseActivity.this, MyReservationsActivity.class));
        });

        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));

        backendEventList = new ArrayList<>();
        eventAdapter = new EventAdapter(backendEventList);
        recyclerViewEvents.setAdapter(eventAdapter);

        setupSpinners();
        setupFilters();

        fetchEventsFromBackend();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchEventsFromBackend();
    }

    private void fetchEventsFromBackend() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/api/customers/events");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONArray jsonArray = new JSONArray(response.toString());
                    List<Event> fetchedEvents = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        String id = obj.getString("eventId");
                        String title = obj.getString("title");
                        String category = obj.getString("category");
                        String location = obj.getString("location");

                        String date = obj.getString("eventDate");
                        String time = obj.getString("startTime");
                        String dateTime = date + " - " + time;

                        int availableCapacity = obj.getInt("availableCapacity");

                        fetchedEvents.add(new Event(id, title, category, location, dateTime, availableCapacity));
                    }

                    handler.post(() -> {
                        backendEventList.clear();
                        backendEventList.addAll(fetchedEvents);
                        eventAdapter.setFilteredList(backendEventList);
                        Toast.makeText(EventBrowseActivity.this, "Events loaded from API!", Toast.LENGTH_SHORT).show();
                    });

                } else {
                    handler.post(() -> Toast
                            .makeText(EventBrowseActivity.this, "Failed to load events", Toast.LENGTH_SHORT).show());
                }
                connection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast
                        .makeText(EventBrowseActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG)
                        .show());
            }
        });
    }

    private void setupSpinners() {
        String[] categories = { "ALL", "CONCERT", "SPORTS", "MOVIE", "TRAVEL" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                categories);
        spinnerCategory.setAdapter(adapter);
    }

    private void setupFilters() {
        etSearchEvents.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEvents();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterEvents();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void filterEvents() {
        String searchText = etSearchEvents.getText().toString().toLowerCase().trim();
        String selectedCategory = spinnerCategory.getSelectedItem().toString();

        List<Event> filteredList = new ArrayList<>();

        for (Event event : backendEventList) {
            boolean matchesSearch = event.getTitle().toLowerCase().contains(searchText) ||
                    event.getLocation().toLowerCase().contains(searchText);

            boolean matchesCategory = selectedCategory.equals("ALL") ||
                    event.getCategory().equalsIgnoreCase(selectedCategory);

            if (matchesSearch && matchesCategory) {
                filteredList.add(event);
            }
        }
        eventAdapter.setFilteredList(filteredList);
    }
}