package com.example.ticketreservationapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
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

public class LoginActivity extends AppCompatActivity {

    private EditText user_id_input, password_input;
    private Button login_button;
    private TextView text_view_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user_id_input = findViewById(R.id.etLoginIdentifier);
        password_input = findViewById(R.id.etLoginPassword);
        login_button = findViewById(R.id.btnLogin);
        text_view_register = findViewById(R.id.tvGoToRegister);

        login_button.setOnClickListener(v -> validateAndLogin());

        text_view_register.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
    }

    private void validateAndLogin() {
        String user_id = user_id_input.getText().toString().trim();
        String password = password_input.getText().toString().trim();

        if (TextUtils.isEmpty(user_id)) {
            user_id_input.setError("Please enter your email or phone number");
            user_id_input.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            password_input.setError("Password is required");
            password_input.requestFocus();
            return;
        }

        loginUserViaApi(user_id, password);
    }

    private void loginUserViaApi(String user_id, String password) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/api/login");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("user_id", user_id);
                jsonParam.put("password", password);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonParam.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int response_code = connection.getResponseCode();

                String userId = null;
                if (response_code == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                        sb.append(line);
                    reader.close();
                    JSONObject responseJson = new JSONObject(sb.toString());
                    if (responseJson.has("userId") && !responseJson.isNull("userId")) {
                        userId = responseJson.getString("userId");
                    }
                }

                final String finalUserId = userId;
                handler.post(() -> {
                    if (response_code == 200) {
                        if (finalUserId != null) {
                            SharedPreferences prefs = getSharedPreferences("TicketApp", MODE_PRIVATE);
                            prefs.edit().putString("userId", finalUserId).apply();
                        }
                        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, EventBrowseActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid credentials.", Toast.LENGTH_LONG).show();
                    }
                });
                connection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast
                        .makeText(LoginActivity.this, "Network Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }
}