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
 * Espresso component test for AdminEventListActivity.
 * Tests UI rendering, view presence, and button state
 * in isolation from the backend.
 */
@RunWith(AndroidJUnit4.class)
public class AdminEventListActivityComponentTest {

    private ActivityScenario<AdminEventListActivity> scenario;

    @Before
    public void setUp() throws IOException {
        InstrumentationRegistry.getInstrumentation()
                .getUiAutomation()
                .executeShellCommand("am broadcast -a android.intent.action.CLOSE_SYSTEM_DIALOGS")
                .close();
        scenario = ActivityScenario.launch(AdminEventListActivity.class);
    }

    @Test
    public void adminEventList_displaysAllRequiredViews() {
        onView(withId(R.id.btnAddEvent)).check(matches(isDisplayed()));
        onView(withId(R.id.recyclerViewAdminEvents)).check(matches(isDisplayed()));
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
        onView(withText("Manage Events")).check(matches(isDisplayed()));
    }

    @Test
    public void pageSubtitle_displaysCorrectText() {
        onView(withText("Create, edit, and manage event listings."))
                .check(matches(isDisplayed()));
    }

    @Test
    public void addEventButton_displaysCorrectText() {
        onView(withId(R.id.btnAddEvent)).check(matches(withText("Add New Event")));
    }

    @Test
    public void addEventButton_isEnabled() {
        onView(withId(R.id.btnAddEvent)).check(matches(isEnabled()));
    }

    @Test
    public void recyclerView_isDisplayed() {
        onView(withId(R.id.recyclerViewAdminEvents)).check(matches(isDisplayed()));
    }
}
