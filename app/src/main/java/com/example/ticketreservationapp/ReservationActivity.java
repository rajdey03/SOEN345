package com.example.ticketreservationapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ReservationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        TextView tvTitle = findViewById(R.id.tvCheckoutEventTitle);
        TextView tvDateTime = findViewById(R.id.tvCheckoutEventDateTime);
        TextView tvLocation = findViewById(R.id.tvCheckoutEventLocation);
        EditText etQuantity = findViewById(R.id.etTicketQuantity);
        Button btnConfirm = findViewById(R.id.btnConfirmReservation);

        String title = getIntent().getStringExtra("EVENT_TITLE");
        String dateTime = getIntent().getStringExtra("EVENT_DATETIME");
        String location = getIntent().getStringExtra("EVENT_LOCATION");

        if (title != null) tvTitle.setText(title);
        if (dateTime != null) tvDateTime.setText(dateTime);
        if (location != null) tvLocation.setText(location);

        btnConfirm.setOnClickListener(v -> {
            String quantityStr = etQuantity.getText().toString().trim();

            if (quantityStr.isEmpty()) {
                etQuantity.setError("Please enter a quantity");
                etQuantity.requestFocus();
                return;
            }

            int quantity = Integer.parseInt(quantityStr);

            // Will be changed later
            Toast.makeText(this, "Booking " + quantity + " tickets for " + title + "!", Toast.LENGTH_LONG).show();
        });
    }
}