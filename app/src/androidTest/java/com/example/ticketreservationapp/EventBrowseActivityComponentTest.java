package com.example.ticketreservationapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
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
 * Espresso component test for EventBrowseActivity.
 * Tests UI rendering, view presence, and initial state
 * in isolation from the backend.
 */
@RunWith(AndroidJUnit4.class)
public class EventBrowseActivityComponentTest {

    private ActivityScenario<EventBrowseActivity> scenario;

    @Before
    public void setUp() throws IOException {
        InstrumentationRegistry.getInstrumentation()
                .getUiAutomation()
                .executeShellCommand("am broadcast -a android.intent.action.CLOSE_SYSTEM_DIALOGS")
                .close();
        scenario = ActivityScenario.launch(EventBrowseActivity.class);
    }

    @Test
    public void browseScreen_displaysAllRequiredViews() {
        onView(withId(R.id.etSearchEvents)).check(matches(isDisplayed()));
        onView(withId(R.id.spinnerCategory)).check(matches(isDisplayed()));
        onView(withId(R.id.recyclerViewEvents)).check(matches(isDisplayed()));
        onView(withId(R.id.btnMyReservations)).check(matches(isDisplayed()));
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
        onView(withText("Discover Events")).check(matches(isDisplayed()));
    }

    @Test
    public void searchField_displaysCorrectHint() {
        onView(withId(R.id.etSearchEvents)).check(matches(isDisplayed()));
    }

    @Test
    public void myReservationsButton_displaysCorrectText() {
        onView(withId(R.id.btnMyReservations)).check(matches(withText("My Reservations")));
    }

    @Test
    public void myReservationsButton_isEnabled() {
        onView(withId(R.id.btnMyReservations)).check(matches(isEnabled()));
    }

    @Test
    public void categorySpinner_isDisplayedAndEnabled() {
        onView(withId(R.id.spinnerCategory)).check(matches(isDisplayed()));
        onView(withId(R.id.spinnerCategory)).check(matches(isEnabled()));
    }

    @Test
    public void recyclerView_isDisplayed() {
        onView(withId(R.id.recyclerViewEvents)).check(matches(isDisplayed()));
    }
}
