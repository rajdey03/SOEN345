package com.example.ticketreservationapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReservationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        TextView tvTitle = findViewById(R.id.tvCheckoutEventTitle);
        TextView tvDateTime = findViewById(R.id.tvCheckoutEventDateTime);
        TextView tvLocation = findViewById(R.id.tvCheckoutEventLocation);
        EditText etQuantity = findViewById(R.id.etTicketQuantity);
        Button btnConfirm = findViewById(R.id.btnConfirmReservation);

        String eventId = getIntent().getStringExtra("EVENT_ID");
        String title = getIntent().getStringExtra("EVENT_TITLE");
        String dateTime = getIntent().getStringExtra("EVENT_DATETIME");
        String location = getIntent().getStringExtra("EVENT_LOCATION");

        if (title != null)
            tvTitle.setText(title);
        if (dateTime != null)
            tvDateTime.setText(dateTime);
        if (location != null)
            tvLocation.setText(location);

        btnConfirm.setOnClickListener(v -> {
            String quantityStr = etQuantity.getText().toString().trim();

            if (quantityStr.isEmpty()) {
                etQuantity.setError("Please enter a quantity");
                etQuantity.requestFocus();
                return;
            }

            int quantity = Integer.parseInt(quantityStr);

            SharedPreferences prefs = getSharedPreferences("TicketApp", MODE_PRIVATE);
            String customerId = prefs.getString("userId", null);

            if (customerId == null) {
                Toast.makeText(this, "Please log in first.", Toast.LENGTH_LONG).show();
                return;
            }

            if (eventId == null) {
                Toast.makeText(this, "Event information missing.", Toast.LENGTH_LONG).show();
                return;
            }

            btnConfirm.setEnabled(false);
            reserveTickets(customerId, eventId, quantity, title);
        });
    }

    private void reserveTickets(String customerId, String eventId, int quantity, String title) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        Button btnConfirm = findViewById(R.id.btnConfirmReservation);

        executor.execute(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/api/customers/reservations");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("customerId", customerId);
                jsonParam.put("eventId", eventId);
                jsonParam.put("numberOfTickets", quantity);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonParam.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();

                String responseBody = "";
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            responseCode >= 400 ? connection.getErrorStream() : connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                        sb.append(line);
                    reader.close();
                    responseBody = sb.toString();
                } catch (Exception ignored) {
                }

                final String body = responseBody;
                handler.post(() -> {
                    btnConfirm.setEnabled(true);
                    if (responseCode == 201) {
                        Toast.makeText(this, "Reserved " + quantity + " ticket(s) for " + title + "!",
                                Toast.LENGTH_LONG).show();
                        finish();
                    } else if (responseCode == 409) {
                        Toast.makeText(this, "Reservation failed: " + parseError(body), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Error: " + parseError(body), Toast.LENGTH_LONG).show();
                    }
                });
                connection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    btnConfirm.setEnabled(true);
                    Toast.makeText(this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private String parseError(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            if (obj.has("message"))
                return obj.getString("message");
            if (obj.has("error"))
                return obj.getString("error");
        } catch (Exception ignored) {
        }
        return json;
    }
}