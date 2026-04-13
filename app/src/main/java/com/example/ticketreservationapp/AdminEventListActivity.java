package com.example.ticketreservationapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

public class AdminEventListActivity extends AppCompatActivity implements AdminEventAdapter.AdminEventActionListener {

    private static final String PREFS_NAME = "TicketApp";
    private static final String PREF_ADMIN_USER_ID = "adminUserId";

    private final List<AdminEvent> adminEvents = new ArrayList<>();
    private AdminEventAdapter adminEventAdapter;
    private ActivityResultLauncher<Intent> formLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_event_list);

        Button btnAddEvent = findViewById(R.id.btnAddEvent);
        RecyclerView recyclerViewAdminEvents = findViewById(R.id.recyclerViewAdminEvents);

        Button btnExitAdmin = findViewById(R.id.btnExitAdmin);
        if (btnExitAdmin != null) {
            btnExitAdmin.setOnClickListener(v -> {
                Intent intent = new Intent(AdminEventListActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }

        recyclerViewAdminEvents.setLayoutManager(new LinearLayoutManager(this));
        adminEventAdapter = new AdminEventAdapter(adminEvents, this);
        recyclerViewAdminEvents.setAdapter(adminEventAdapter);

        formLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        AdminEvent updatedEvent = new AdminEvent(
                                data.getStringExtra(AdminEventFormActivity.RESULT_EVENT_ID),
                                data.getStringExtra(AdminEventFormActivity.RESULT_ORGANIZER_ID),
                                data.getStringExtra(AdminEventFormActivity.RESULT_TITLE),
                                data.getStringExtra(AdminEventFormActivity.RESULT_DESCRIPTION),
                                data.getStringExtra(AdminEventFormActivity.RESULT_CATEGORY),
                                data.getStringExtra(AdminEventFormActivity.RESULT_LOCATION),
                                data.getStringExtra(AdminEventFormActivity.RESULT_EVENT_DATE),
                                data.getStringExtra(AdminEventFormActivity.RESULT_START_TIME),
                                data.getStringExtra(AdminEventFormActivity.RESULT_END_TIME),
                                data.getIntExtra(AdminEventFormActivity.RESULT_TOTAL_CAPACITY, 0),
                                data.getStringExtra(AdminEventFormActivity.RESULT_STATUS)
                        );

                        int editPosition = data.getIntExtra("RESULT_POSITION", -1);
                        if (editPosition >= 0) {
                            adminEventAdapter.updateEvent(editPosition, updatedEvent);
                        } else {
                            adminEventAdapter.addEvent(updatedEvent);
                        }

                        loadEventsViaApi();
                    }
                }
        );

        btnAddEvent.setOnClickListener(v -> {
            Intent intent = new Intent(AdminEventListActivity.this, AdminEventFormActivity.class);
            intent.putExtra(AdminEventFormActivity.EXTRA_MODE, AdminEventFormActivity.MODE_CREATE);
            formLauncher.launch(intent);
        });

        loadEventsViaApi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEventsViaApi();
    }

    @Override
    public void onCancelRequested(AdminEvent event, int position) {
        String adminUserId = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getString(PREF_ADMIN_USER_ID, null);

        String validationMessage = AdminEventListLogic.validateCancelRequest(adminUserId, event.getEventId());
        if (validationMessage != null) {
            Toast.makeText(this, validationMessage, Toast.LENGTH_LONG).show();
            return;
        }

        cancelEventViaApi(adminUserId, event, position);
    }

    @Override
    public void onEditRequested(AdminEvent event, int position) {
        Intent intent = new Intent(AdminEventListActivity.this, AdminEventFormActivity.class);
        intent.putExtra(AdminEventFormActivity.EXTRA_MODE, AdminEventFormActivity.MODE_EDIT);
        intent.putExtra(AdminEventFormActivity.EXTRA_EVENT_ID, event.getEventId());
        intent.putExtra(AdminEventFormActivity.EXTRA_ORGANIZER_ID, event.getOrganizerId());
        intent.putExtra(AdminEventFormActivity.EXTRA_TITLE, event.getTitle());
        intent.putExtra(AdminEventFormActivity.EXTRA_DESCRIPTION, event.getDescription());
        intent.putExtra(AdminEventFormActivity.EXTRA_CATEGORY, event.getCategory());
        intent.putExtra(AdminEventFormActivity.EXTRA_LOCATION, event.getLocation());
        intent.putExtra(AdminEventFormActivity.EXTRA_EVENT_DATE, event.getEventDate());
        intent.putExtra(AdminEventFormActivity.EXTRA_START_TIME, event.getStartTime());
        intent.putExtra(AdminEventFormActivity.EXTRA_END_TIME, event.getEndTime());
        intent.putExtra(AdminEventFormActivity.EXTRA_TOTAL_CAPACITY, event.getTotalCapacity());
        intent.putExtra(AdminEventFormActivity.EXTRA_STATUS, event.getStatus());
        intent.putExtra(AdminEventFormActivity.RESULT_POSITION, position);
        formLauncher.launch(intent);
    }

    private void cancelEventViaApi(String adminUserId, AdminEvent event, int position) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL("http://10.0.2.2:8080/api/admin/events/" + event.getEventId() + "/cancel");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PATCH");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("X-Admin-User-Id", adminUserId);

                int responseCode = connection.getResponseCode();
                String responseBody = readResponseBody(connection, responseCode);

                handler.post(() -> {
                    if (responseCode == 200) {
                        adminEventAdapter.markCancelled(position);
                        Toast.makeText(this, "Event cancelled successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, parseErrorMessage(responseBody), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(this, "Network Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private String readResponseBody(HttpURLConnection connection, int responseCode) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    responseCode >= 400 ? connection.getErrorStream() : connection.getInputStream(),
                    "utf-8"
            ));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            return sb.toString();
        } catch (Exception ignored) {
            return "";
        }
    }

    private String parseErrorMessage(String body) {
        return AdminEventListLogic.parseErrorMessage(body);
    }

    private void loadEventsViaApi() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String adminUserId = prefs.getString(PREF_ADMIN_USER_ID, null);
        if (adminUserId == null || adminUserId.trim().isEmpty()) {
            Toast.makeText(this, "Admin access is missing. Open the Admin Portal again.", Toast.LENGTH_LONG).show();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL("http://10.0.2.2:8080/api/admin/events");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("X-Admin-User-Id", adminUserId);

                int responseCode = connection.getResponseCode();
                String responseBody = readResponseBody(connection, responseCode);

                handler.post(() -> {
                    if (responseCode == 200) {
                        adminEvents.clear();
                        adminEvents.addAll(parseEvents(responseBody));
                        adminEventAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, parseErrorMessage(responseBody), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(this, "Network Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private List<AdminEvent> parseEvents(String responseBody) {
        List<AdminEvent> parsedEvents = new ArrayList<>();
        try {
            JSONArray items = new JSONArray(responseBody);
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                parsedEvents.add(new AdminEvent(
                        item.optString("event_id", ""),
                        item.optString("organizer_id", ""),
                        item.optString("title", ""),
                        item.optString("description", ""),
                        item.optString("category", ""),
                        item.optString("location", ""),
                        item.optString("event_date", ""),
                        item.optString("start_time", ""),
                        item.optString("end_time", ""),
                        item.optInt("total_capacity", 0),
                        item.optString("status", "ACTIVE")
                ));
            }
        } catch (Exception ignored) {
            return parsedEvents;
        }
        return parsedEvents;
    }
}
