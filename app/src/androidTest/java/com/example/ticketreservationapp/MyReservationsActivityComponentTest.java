package com.example.ticketreservationapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

/**
 * Espresso component test for MyReservationsActivity.
 * Tests UI rendering and view presence in isolation from the backend.
 */
@RunWith(AndroidJUnit4.class)
public class MyReservationsActivityComponentTest {

    private ActivityScenario<MyReservationsActivity> scenario;

    @Before
    public void setUp() throws IOException {
        InstrumentationRegistry.getInstrumentation()
                .getUiAutomation()
                .executeShellCommand("am broadcast -a android.intent.action.CLOSE_SYSTEM_DIALOGS")
                .close();
        scenario = ActivityScenario.launch(MyReservationsActivity.class);
    }

    @Test
    public void reservationsScreen_displaysAllRequiredViews() {
        onView(withId(R.id.btnBack)).check(matches(isDisplayed()));
        onView(withId(R.id.recyclerViewReservations)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.onActivity(activity -> activity.finish());
            scenario = null;
        }
    }

    @Test
    public void pageTitle_displaysCorrectText() {
        onView(withText("My Reservations")).check(matches(isDisplayed()));
    }

    @Test
    public void backButton_isDisplayed() {
        onView(withId(R.id.btnBack)).check(matches(isDisplayed()));
    }

    @Test
    public void recyclerView_isDisplayed() {
        onView(withId(R.id.recyclerViewReservations)).check(matches(isDisplayed()));
    }
}
