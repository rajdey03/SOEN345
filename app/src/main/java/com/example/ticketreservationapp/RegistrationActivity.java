package com.example.ticketreservationapp; // Ensure this matches your package name!

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;
import org.json.JSONObject;
import java.util.Iterator;

public class RegistrationActivity extends AppCompatActivity {

    private EditText first_name_input, last_name_input, email_input, phone_input, password_input;
    private Button register_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        first_name_input = findViewById(R.id.etFirstName);
        last_name_input = findViewById(R.id.etLastName);
        email_input = findViewById(R.id.etEmail);
        phone_input = findViewById(R.id.etPhone);
        password_input = findViewById(R.id.etPassword);
        register_button = findViewById(R.id.btnRegister);

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndRegisterUser();
            }
        });
    }

    private void validateAndRegisterUser() {
        String firstName = first_name_input.getText().toString().trim();
        String lastName = last_name_input.getText().toString().trim();
        String email = email_input.getText().toString().trim();
        String phone = phone_input.getText().toString().trim();
        String password = password_input.getText().toString().trim();

        if (TextUtils.isEmpty(firstName)) {
            first_name_input.setError("First name is required");
            first_name_input.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            last_name_input.setError("Last name is required");
            last_name_input.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            password_input.setError("Password is required");
            password_input.requestFocus();
            return;
        }

        if (password.length() < 6) {
            password_input.setError("Password must be at least 6 characters");
            password_input.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "You must provide either an email or a phone number.", Toast.LENGTH_LONG).show();
            email_input.requestFocus();
            return;
        }

        sendRegistrationToServer(firstName, lastName, email, phone, password);
    }

    private void sendRegistrationToServer(String firstName, String lastName, String email, String phone,
            String password) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/api/register");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("first_name", firstName);
                jsonParam.put("last_name", lastName);

                if (!email.isEmpty())
                    jsonParam.put("email", email);
                if (!phone.isEmpty())
                    jsonParam.put("phone_number", phone);

                jsonParam.put("password", password);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonParam.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();

                String responseBody = "";
                try {
                    BufferedReader reader;
                    if (responseCode >= 200 && responseCode < 300) {
                        reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                    } else {
                        reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
                    }
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();
                    responseBody = sb.toString();
                } catch (Exception ignored) {
                }

                final String body = responseBody;

                handler.post(() -> {
                    if (responseCode == 200 || responseCode == 201) {
                        Toast.makeText(RegistrationActivity.this, "Registration Success! Check your Email/SMS.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        String errorMsg = parseErrorMessage(responseCode, body);
                        Toast.makeText(RegistrationActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                });

                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast
                        .makeText(RegistrationActivity.this, "Network Error: " + e.getMessage(), Toast.LENGTH_LONG)
                        .show());
            }
        });
    }

    private String parseErrorMessage(int responseCode, String body) {
        try {
            JSONObject json = new JSONObject(body);

            // Backend returns {"message": "..."} for business errors (conflict, invalid
            // registration)
            if (json.has("message")) {
                return json.getString("message");
            }

            // Backend returns {"field": "error message"} for validation errors
            StringBuilder sb = new StringBuilder();
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                if (sb.length() > 0)
                    sb.append("\n");
                sb.append(json.getString(key));
            }
            if (sb.length() > 0)
                return sb.toString();

        } catch (Exception ignored) {
        }

        if (responseCode == 409)
            return "An account with this email or phone number already exists.";
        if (responseCode == 400)
            return "Registration failed. Please check your input.";
        return "Registration failed. Error code: " + responseCode;
    }
}