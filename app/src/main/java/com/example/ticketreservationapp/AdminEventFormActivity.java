package com.example.ticketreservationapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminEventFormActivity extends AppCompatActivity {

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

    private String formMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_event_form);

        TextView tvAdminFormTitle = findViewById(R.id.tvAdminFormTitle);
        Button btnSaveEvent = findViewById(R.id.btnSaveEvent);
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
        if (isBlank(etOrganizerId, "Organizer ID is required")) return;
        if (isBlank(etEventTitle, "Event title is required")) return;
        if (isBlank(etEventDescription, "Description is required")) return;
        if (isBlank(etEventCategory, "Category is required")) return;
        if (isBlank(etEventLocation, "Location is required")) return;
        if (isBlank(etEventDate, "Event date is required")) return;
        if (isBlank(etStartTime, "Start time is required")) return;
        if (isBlank(etEndTime, "End time is required")) return;
        if (isBlank(etTotalCapacity, "Total capacity is required")) return;

        String action = MODE_EDIT.equals(formMode) ? "updated" : "created";
        Toast.makeText(
                this,
                "Event ready to be " + action + ". API wiring is next.",
                Toast.LENGTH_SHORT
        ).show();
    }

    private boolean isBlank(EditText editText, String errorMessage) {
        if (TextUtils.isEmpty(editText.getText().toString().trim())) {
            editText.setError(errorMessage);
            editText.requestFocus();
            return true;
        }
        return false;
    }
}
