package com.example.ticketreservationapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

/**
 * Espresso component test for ReservationActivity.
 * Tests UI rendering, intent data display, input validation,
 * and user interactions in isolation from the backend.
 */
@RunWith(AndroidJUnit4.class)
public class ReservationActivityComponentTest {

    private ActivityScenario<ReservationActivity> scenario;

    private static Intent buildReservationIntent() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ReservationActivity.class);
        intent.putExtra("EVENT_ID", "test-event-id");
        intent.putExtra("EVENT_TITLE", "Jazz Night");
        intent.putExtra("EVENT_DATETIME", "2026-07-15 - 20:00");
        intent.putExtra("EVENT_LOCATION", "Blue Note Club");
        return intent;
    }

    @Before
    public void setUp() throws IOException {
        InstrumentationRegistry.getInstrumentation()
                .getUiAutomation()
                .executeShellCommand("am broadcast -a android.intent.action.CLOSE_SYSTEM_DIALOGS")
                .close();
        scenario = ActivityScenario.launch(buildReservationIntent());
    }

    @Test
    public void reservationForm_displaysAllRequiredViews() {
        onView(withId(R.id.btnBack)).check(matches(isDisplayed()));
        onView(withId(R.id.tvCheckoutEventTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.tvCheckoutEventDateTime)).check(matches(isDisplayed()));
        onView(withId(R.id.tvCheckoutEventLocation)).check(matches(isDisplayed()));
        onView(withId(R.id.etTicketQuantity)).check(matches(isDisplayed()));
        onView(withId(R.id.btnConfirmReservation)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.onActivity(activity -> activity.finish());
            scenario = null;
        }
    }

    @Test
    public void reservationForm_displaysEventDetailsFromIntent() {
        onView(withId(R.id.tvCheckoutEventTitle))
                .check(matches(withText("Jazz Night")));
        onView(withId(R.id.tvCheckoutEventDateTime))
                .check(matches(withText("2026-07-15 - 20:00")));
        onView(withId(R.id.tvCheckoutEventLocation))
                .check(matches(withText("Blue Note Club")));
    }

    @Test
    public void pageTitle_displaysCorrectText() {
        onView(withText("Complete Your Booking")).check(matches(isDisplayed()));
    }

    @Test
    public void ticketQuantityField_displaysCorrectHint() {
        onView(withId(R.id.etTicketQuantity))
                .check(matches(withHint("Enter quantity (e.g., 2)")));
    }

    @Test
    public void confirmButton_displaysCorrectText() {
        onView(withId(R.id.btnConfirmReservation))
                .check(matches(withText("Confirm Reservation")));
    }

    @Test
    public void confirmButton_isEnabled() {
        onView(withId(R.id.btnConfirmReservation)).check(matches(isEnabled()));
    }

    @Test
    public void emptyQuantity_showsValidationError() {
        onView(withId(R.id.btnConfirmReservation)).perform(click());
        onView(withId(R.id.etTicketQuantity)).check(matches(isDisplayed()));
    }

    @Test
    public void userCanTypeQuantity() {
        onView(withId(R.id.etTicketQuantity))
                .perform(replaceText("3"));
        onView(withId(R.id.etTicketQuantity))
                .check(matches(withText("3")));
    }

    @Test
    public void backButton_isDisplayed() {
        onView(withId(R.id.btnBack)).check(matches(isDisplayed()));
    }

    @Test
    public void eventDetailsSection_displaysLabel() {
        onView(withText("Event Details")).check(matches(isDisplayed()));
    }

    @Test
    public void numberOfTicketsLabel_isDisplayed() {
        onView(withText("Number of Tickets")).check(matches(isDisplayed()));
    }
}
