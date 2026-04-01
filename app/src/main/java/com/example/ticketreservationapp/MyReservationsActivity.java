package com.example.ticketreservationapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
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
import android.widget.ImageButton;

public class MyReservationsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ReservationAdapter adapter;
    private List<Reservation> reservationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_reservations);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerViewReservations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReservationAdapter(reservationList, this::onCancelClicked);
        recyclerView.setAdapter(adapter);

        fetchReservations();
    }

    private void fetchReservations() {
        String userId = getSharedPreferences("TicketApp", MODE_PRIVATE).getString("userId", null);
        if (userId == null) {
            Toast.makeText(this, "Please log in first.", Toast.LENGTH_LONG).show();
            return;
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/api/customers/reservations?userId=" + userId);
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
                    List<Reservation> fetched = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        // Only add if not cancelled
                        String status = obj.optString("status", "");
                        if (!"CANCELLED".equalsIgnoreCase(status)) {
                            fetched.add(Reservation.fromJson(obj));
                        }
                    }
                    handler.post(() -> {
                        reservationList.clear();
                        reservationList.addAll(fetched);
                        adapter.notifyDataSetChanged();
                    });
                } else {
                    handler.post(() -> Toast.makeText(this, "Failed to load reservations", Toast.LENGTH_SHORT).show());
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private void onCancelClicked(Reservation reservation) {
        String userId = getSharedPreferences("TicketApp", MODE_PRIVATE).getString("userId", null);
        if (userId == null) {
            Toast.makeText(this, "Please log in first.", Toast.LENGTH_LONG).show();
            return;
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/api/customers/reservations/" + reservation.getReservationId() + "?userId=" + userId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    handler.post(() -> {
                        reservationList.remove(reservation);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(this, "Reservation cancelled.", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    handler.post(() -> Toast.makeText(this, "Failed to cancel reservation", Toast.LENGTH_SHORT).show());
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }
}
