package com.example.ticketreservationapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class AdminEventFormActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "TicketApp";
    private static final String PREF_ADMIN_USER_ID = "adminUserId";
    public static final String RESULT_EVENT_ID = "RESULT_EVENT_ID";
    public static final String RESULT_POSITION = "RESULT_POSITION";
    public static final String RESULT_ORGANIZER_ID = "RESULT_ORGANIZER_ID";
    public static final String RESULT_TITLE = "RESULT_TITLE";
    public static final String RESULT_DESCRIPTION = "RESULT_DESCRIPTION";
    public static final String RESULT_CATEGORY = "RESULT_CATEGORY";
    public static final String RESULT_LOCATION = "RESULT_LOCATION";
    public static final String RESULT_EVENT_DATE = "RESULT_EVENT_DATE";
    public static final String RESULT_START_TIME = "RESULT_START_TIME";
    public static final String RESULT_END_TIME = "RESULT_END_TIME";
    public static final String RESULT_TOTAL_CAPACITY = "RESULT_TOTAL_CAPACITY";
    public static final String RESULT_STATUS = "RESULT_STATUS";

    public static final String EXTRA_MODE = "ADMIN_MODE";
    public static final String EXTRA_EVENT_ID = "EVENT_ID";
    public static final String EXTRA_ORGANIZER_ID = "ORGANIZER_ID";
    public static final String EXTRA_TITLE = "EVENT_TITLE";
    public static final String EXTRA_DESCRIPTION = "EVENT_DESCRIPTION";
    public static final String EXTRA_CATEGORY = "EVENT_CATEGORY";
    public static final String EXTRA_LOCATION = "EVENT_LOCATION";
    public static final String EXTRA_EVENT_DATE = "EVENT_DATE";
    public static final String EXTRA_START_TIME = "START_TIME";
    public static final String EXTRA_END_TIME = "END_TIME";
    public static final String EXTRA_TOTAL_CAPACITY = "TOTAL_CAPACITY";
    public static final String EXTRA_STATUS = "EVENT_STATUS";

    public static final String MODE_CREATE = "create";
    public static final String MODE_EDIT = "edit";

    private EditText etOrganizerId;
    private EditText etEventTitle;
    private EditText etEventDescription;
    private EditText etEventCategory;
    private EditText etEventLocation;
    private EditText etEventDate;
    private EditText etStartTime;
    private EditText etEndTime;
    private EditText etTotalCapacity;
    private Button btnSaveEvent;

    private String formMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_event_form);

        TextView tvAdminFormTitle = findViewById(R.id.tvAdminFormTitle);
        btnSaveEvent = findViewById(R.id.btnSaveEvent);
        Button btnCancelEvent = findViewById(R.id.btnCancelEvent);

        etOrganizerId = findViewById(R.id.etOrganizerId);
        etEventTitle = findViewById(R.id.etEventTitle);
        etEventDescription = findViewById(R.id.etEventDescription);
        etEventCategory = findViewById(R.id.etEventCategory);
        etEventLocation = findViewById(R.id.etEventLocation);
        etEventDate = findViewById(R.id.etEventDate);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);
        etTotalCapacity = findViewById(R.id.etTotalCapacity);

        formMode = getIntent().getStringExtra(EXTRA_MODE);
        if (MODE_EDIT.equals(formMode)) {
            tvAdminFormTitle.setText("Edit Event");
            btnSaveEvent.setText("Update Event");
            populateFormFromIntent();
        }

        btnSaveEvent.setOnClickListener(v -> validateAndSubmit());
        btnCancelEvent.setOnClickListener(v -> finish());
    }

    private void populateFormFromIntent() {
        etOrganizerId.setText(getIntent().getStringExtra(EXTRA_ORGANIZER_ID));
        etEventTitle.setText(getIntent().getStringExtra(EXTRA_TITLE));
        etEventDescription.setText(getIntent().getStringExtra(EXTRA_DESCRIPTION));
        etEventCategory.setText(getIntent().getStringExtra(EXTRA_CATEGORY));
        etEventLocation.setText(getIntent().getStringExtra(EXTRA_LOCATION));
        etEventDate.setText(getIntent().getStringExtra(EXTRA_EVENT_DATE));
        etStartTime.setText(getIntent().getStringExtra(EXTRA_START_TIME));
        etEndTime.setText(getIntent().getStringExtra(EXTRA_END_TIME));
        etTotalCapacity.setText(String.valueOf(getIntent().getIntExtra(EXTRA_TOTAL_CAPACITY, 0)));
    }

    private void validateAndSubmit() {
        if (showValidationErrorIfNeeded()) return;

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String adminUserId = prefs.getString(PREF_ADMIN_USER_ID, null);
        if (TextUtils.isEmpty(adminUserId)) {
            Toast.makeText(this, "Admin access is missing. Open the Admin Portal again.", Toast.LENGTH_LONG).show();
            return;
        }

        submitEventToApi(adminUserId);
    }

    private boolean showValidationErrorIfNeeded() {
        String organizerId = etOrganizerId.getText().toString().trim();
        String title = etEventTitle.getText().toString().trim();
        String description = etEventDescription.getText().toString().trim();
        String category = etEventCategory.getText().toString().trim();
        String location = etEventLocation.getText().toString().trim();
        String eventDate = etEventDate.getText().toString().trim();
        String startTime = etStartTime.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();
        String totalCapacityText = etTotalCapacity.getText().toString().trim();

        int totalCapacity = 0;
        if (!TextUtils.isEmpty(totalCapacityText)) {
            try {
                totalCapacity = Integer.parseInt(totalCapacityText);
            } catch (NumberFormatException ignored) {
                totalCapacity = 0;
            }
        }

        String errorMessage = AdminEventFormLogic.validate(new AdminEventDraft(
                organizerId,
                title,
                description,
                category,
                location,
                eventDate,
                startTime,
                endTime,
                totalCapacity,
                "ACTIVE"
        ));

        if (errorMessage == null) {
            return false;
        }

        if ("Organizer ID is required".equals(errorMessage)) return setFieldError(etOrganizerId, errorMessage);
        if ("Event title is required".equals(errorMessage)) return setFieldError(etEventTitle, errorMessage);
        if ("Description is required".equals(errorMessage)) return setFieldError(etEventDescription, errorMessage);
        if ("Category is required".equals(errorMessage)) return setFieldError(etEventCategory, errorMessage);
        if ("Location is required".equals(errorMessage)) return setFieldError(etEventLocation, errorMessage);
        if ("Event date is required".equals(errorMessage)) return setFieldError(etEventDate, errorMessage);
        if ("Start time is required".equals(errorMessage)) return setFieldError(etStartTime, errorMessage);
        if ("End time is required".equals(errorMessage)) return setFieldError(etEndTime, errorMessage);

        return setFieldError(etTotalCapacity, errorMessage);
    }

    private boolean setFieldError(EditText editText, String errorMessage) {
        editText.setError(errorMessage);
        editText.requestFocus();
        return true;
    }

    private void submitEventToApi(String adminUserId) {
        btnSaveEvent.setEnabled(false);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            HttpURLConnection connection = null;
            try {
                boolean isEdit = MODE_EDIT.equals(formMode);
                String eventId = getIntent().getStringExtra(EXTRA_EVENT_ID);
                String endpoint = isEdit
                        ? "http://10.0.2.2:8080/api/admin/events/" + eventId
                        : "http://10.0.2.2:8080/api/admin/events";

                URL url = new URL(endpoint);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(isEdit ? "PUT" : "POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("X-Admin-User-Id", adminUserId);
                connection.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("organizer_id", etOrganizerId.getText().toString().trim());
                jsonParam.put("title", etEventTitle.getText().toString().trim());
                jsonParam.put("description", etEventDescription.getText().toString().trim());
                jsonParam.put("category", etEventCategory.getText().toString().trim());
                jsonParam.put("location", etEventLocation.getText().toString().trim());
                jsonParam.put("event_date", etEventDate.getText().toString().trim());
                jsonParam.put("start_time", etStartTime.getText().toString().trim());
                jsonParam.put("end_time", etEndTime.getText().toString().trim());
                jsonParam.put("total_capacity", Integer.parseInt(etTotalCapacity.getText().toString().trim()));

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonParam.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                String responseBody = readResponseBody(connection, responseCode);

                handler.post(() -> {
                    btnSaveEvent.setEnabled(true);
                    if (responseCode == 200 || responseCode == 201) {
                        Intent resultIntent = buildResultIntent(responseBody);
                        setResult(RESULT_OK, resultIntent);
                        Toast.makeText(
                                this,
                                MODE_EDIT.equals(formMode) ? "Event updated successfully." : "Event created successfully.",
                                Toast.LENGTH_SHORT
                        ).show();
                        finish();
                    } else {
                        Toast.makeText(this, parseErrorMessage(responseBody), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    btnSaveEvent.setEnabled(true);
                    Toast.makeText(this, "Network Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
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

    private Intent buildResultIntent(String responseBody) {
        Intent result = new Intent();
        AdminEventDraft fallbackDraft = new AdminEventDraft(
                etOrganizerId.getText().toString().trim(),
                etEventTitle.getText().toString().trim(),
                etEventDescription.getText().toString().trim(),
                etEventCategory.getText().toString().trim(),
                etEventLocation.getText().toString().trim(),
                etEventDate.getText().toString().trim(),
                etStartTime.getText().toString().trim(),
                etEndTime.getText().toString().trim(),
                Integer.parseInt(etTotalCapacity.getText().toString().trim()),
                "ACTIVE"
        );
        AdminEvent parsedEvent = AdminEventFormLogic.buildEventFromResponse(
                responseBody,
                getIntent().getStringExtra(EXTRA_EVENT_ID),
                fallbackDraft
        );

        result.putExtra(RESULT_EVENT_ID, parsedEvent.getEventId());
        result.putExtra(RESULT_ORGANIZER_ID, parsedEvent.getOrganizerId());
        result.putExtra(RESULT_TITLE, parsedEvent.getTitle());
        result.putExtra(RESULT_DESCRIPTION, parsedEvent.getDescription());
        result.putExtra(RESULT_CATEGORY, parsedEvent.getCategory());
        result.putExtra(RESULT_LOCATION, parsedEvent.getLocation());
        result.putExtra(RESULT_EVENT_DATE, parsedEvent.getEventDate());
        result.putExtra(RESULT_START_TIME, parsedEvent.getStartTime());
        result.putExtra(RESULT_END_TIME, parsedEvent.getEndTime());
        result.putExtra(RESULT_TOTAL_CAPACITY, parsedEvent.getTotalCapacity());
        result.putExtra(RESULT_STATUS, parsedEvent.getStatus());
        result.putExtra(RESULT_POSITION, getIntent().getIntExtra(RESULT_POSITION, -1));

        return result;
    }

    private String parseErrorMessage(String body) {
        return AdminEventFormLogic.parseErrorMessage(body);
    }
}
