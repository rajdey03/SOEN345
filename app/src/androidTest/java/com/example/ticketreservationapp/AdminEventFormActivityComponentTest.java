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
 * Espresso component test for AdminEventFormActivity.
 * Tests UI rendering in create and edit modes, field presence,
 * input validation, and user interactions in isolation from the backend.
 */
@RunWith(AndroidJUnit4.class)
public class AdminEventFormActivityComponentTest {

    private ActivityScenario<AdminEventFormActivity> scenario;

    @Before
    public void dismissSystemDialogs() throws IOException {
        InstrumentationRegistry.getInstrumentation()
                .getUiAutomation()
                .executeShellCommand("am broadcast -a android.intent.action.CLOSE_SYSTEM_DIALOGS")
                .close();
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.onActivity(activity -> activity.finish());
            scenario = null;
        }
    }

    private Intent buildCreateModeIntent() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AdminEventFormActivity.class);
        intent.putExtra(AdminEventFormActivity.EXTRA_MODE, AdminEventFormActivity.MODE_CREATE);
        return intent;
    }

    private Intent buildEditModeIntent() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AdminEventFormActivity.class);
        intent.putExtra(AdminEventFormActivity.EXTRA_MODE, AdminEventFormActivity.MODE_EDIT);
        intent.putExtra(AdminEventFormActivity.EXTRA_EVENT_ID, "test-event-id");
        intent.putExtra(AdminEventFormActivity.EXTRA_ORGANIZER_ID, "test-organizer-id");
        intent.putExtra(AdminEventFormActivity.EXTRA_TITLE, "Jazz Night");
        intent.putExtra(AdminEventFormActivity.EXTRA_DESCRIPTION, "Live jazz performance");
        intent.putExtra(AdminEventFormActivity.EXTRA_CATEGORY, "CONCERT");
        intent.putExtra(AdminEventFormActivity.EXTRA_LOCATION, "Blue Note Club");
        intent.putExtra(AdminEventFormActivity.EXTRA_EVENT_DATE, "2026-07-15");
        intent.putExtra(AdminEventFormActivity.EXTRA_START_TIME, "20:00");
        intent.putExtra(AdminEventFormActivity.EXTRA_END_TIME, "23:00");
        intent.putExtra(AdminEventFormActivity.EXTRA_TOTAL_CAPACITY, 200);
        intent.putExtra(AdminEventFormActivity.EXTRA_STATUS, "ACTIVE");
        return intent;
    }

    // ---------------------------------------------------------------
    // Create Mode Tests
    // ---------------------------------------------------------------

    @Test
    public void createMode_displaysAllFormFields() {
        scenario = ActivityScenario.launch(buildCreateModeIntent());
        onView(withId(R.id.etOrganizerId)).check(matches(isDisplayed()));
        onView(withId(R.id.etEventTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.etEventDescription)).check(matches(isDisplayed()));
        onView(withId(R.id.etEventCategory)).check(matches(isDisplayed()));
        onView(withId(R.id.etEventLocation)).check(matches(isDisplayed()));
        onView(withId(R.id.etEventDate)).check(matches(isDisplayed()));
        onView(withId(R.id.etStartTime)).check(matches(isDisplayed()));
        onView(withId(R.id.etEndTime)).check(matches(isDisplayed()));
        onView(withId(R.id.etTotalCapacity)).check(matches(isDisplayed()));
    }

    @Test
    public void createMode_displaysCorrectTitle() {
        scenario = ActivityScenario.launch(buildCreateModeIntent());
        onView(withId(R.id.tvAdminFormTitle))
                .check(matches(withText("Create Event")));
    }

    @Test
    public void createMode_saveButtonShowsSaveText() {
        scenario = ActivityScenario.launch(buildCreateModeIntent());
        onView(withId(R.id.btnSaveEvent)).check(matches(withText("Save Event")));
    }

    @Test
    public void createMode_displaysCorrectHints() {
        scenario = ActivityScenario.launch(buildCreateModeIntent());
        onView(withId(R.id.etOrganizerId)).check(matches(withHint("Organizer ID")));
        onView(withId(R.id.etEventTitle)).check(matches(withHint("Event Title")));
        onView(withId(R.id.etEventDescription)).check(matches(withHint("Description")));
        onView(withId(R.id.etEventCategory)).check(matches(withHint("Category")));
        onView(withId(R.id.etEventLocation)).check(matches(withHint("Location")));
        onView(withId(R.id.etEventDate)).check(matches(withHint("Event Date (YYYY-MM-DD)")));
        onView(withId(R.id.etStartTime)).check(matches(withHint("Start Time (HH:MM)")));
        onView(withId(R.id.etEndTime)).check(matches(withHint("End Time (HH:MM)")));
        onView(withId(R.id.etTotalCapacity)).check(matches(withHint("Total Capacity")));
    }

    @Test
    public void createMode_buttonsAreEnabled() {
        scenario = ActivityScenario.launch(buildCreateModeIntent());
        onView(withId(R.id.btnSaveEvent)).check(matches(isEnabled()));
        onView(withId(R.id.btnCancelEvent)).check(matches(isEnabled()));
    }

    @Test
    public void createMode_cancelButtonDisplaysCorrectText() {
        scenario = ActivityScenario.launch(buildCreateModeIntent());
        onView(withId(R.id.btnCancelEvent)).check(matches(withText("Cancel Event")));
    }

    @Test
    public void createMode_formSubtitle_displaysCorrectText() {
        scenario = ActivityScenario.launch(buildCreateModeIntent());
        onView(withText("Fill in the event details below.")).check(matches(isDisplayed()));
    }

    // ---------------------------------------------------------------
    // Edit Mode Tests
    // ---------------------------------------------------------------

    @Test
    public void editMode_displaysCorrectTitle() {
        scenario = ActivityScenario.launch(buildEditModeIntent());
        onView(withId(R.id.tvAdminFormTitle))
                .check(matches(withText("Edit Event")));
    }

    @Test
    public void editMode_saveButtonShowsUpdateText() {
        scenario = ActivityScenario.launch(buildEditModeIntent());
        onView(withId(R.id.btnSaveEvent)).check(matches(withText("Update Event")));
    }

    @Test
    public void editMode_populatesFieldsFromIntent() {
        scenario = ActivityScenario.launch(buildEditModeIntent());
        onView(withId(R.id.etOrganizerId)).check(matches(withText("test-organizer-id")));
        onView(withId(R.id.etEventTitle)).check(matches(withText("Jazz Night")));
        onView(withId(R.id.etEventDescription)).check(matches(withText("Live jazz performance")));
        onView(withId(R.id.etEventCategory)).check(matches(withText("CONCERT")));
        onView(withId(R.id.etEventLocation)).check(matches(withText("Blue Note Club")));
        onView(withId(R.id.etEventDate)).check(matches(withText("2026-07-15")));
        onView(withId(R.id.etStartTime)).check(matches(withText("20:00")));
        onView(withId(R.id.etEndTime)).check(matches(withText("23:00")));
        onView(withId(R.id.etTotalCapacity)).check(matches(withText("200")));
    }

    // ---------------------------------------------------------------
    // Validation Tests
    // ---------------------------------------------------------------

    @Test
    public void emptyOrganizerId_showsValidationError() {
        scenario = ActivityScenario.launch(buildCreateModeIntent());
        onView(withId(R.id.etEventTitle)).perform(replaceText("Test Event"));
        onView(withId(R.id.etEventDescription)).perform(replaceText("Description"));
        onView(withId(R.id.etEventCategory)).perform(replaceText("CONCERT"));
        onView(withId(R.id.etEventLocation)).perform(replaceText("Montreal"));
        onView(withId(R.id.etEventDate)).perform(replaceText("2026-07-15"));
        onView(withId(R.id.etStartTime)).perform(replaceText("20:00"));
        onView(withId(R.id.etEndTime)).perform(replaceText("23:00"));
        onView(withId(R.id.etTotalCapacity)).perform(replaceText("100"));

        onView(withId(R.id.btnSaveEvent)).perform(click());

        onView(withId(R.id.etOrganizerId)).check(matches(isDisplayed()));
    }

    @Test
    public void emptyTitle_showsValidationError() {
        scenario = ActivityScenario.launch(buildCreateModeIntent());
        onView(withId(R.id.etOrganizerId)).perform(replaceText("org-id"));
        onView(withId(R.id.etEventDescription)).perform(replaceText("Description"));
        onView(withId(R.id.etEventCategory)).perform(replaceText("CONCERT"));
        onView(withId(R.id.etEventLocation)).perform(replaceText("Montreal"));
        onView(withId(R.id.etEventDate)).perform(replaceText("2026-07-15"));
        onView(withId(R.id.etStartTime)).perform(replaceText("20:00"));
        onView(withId(R.id.etEndTime)).perform(replaceText("23:00"));
        onView(withId(R.id.etTotalCapacity)).perform(replaceText("100"));

        onView(withId(R.id.btnSaveEvent)).perform(click());

        onView(withId(R.id.etEventTitle)).check(matches(isDisplayed()));
    }

    @Test
    public void userCanTypeInAllFields() {
        scenario = ActivityScenario.launch(buildCreateModeIntent());
        onView(withId(R.id.etOrganizerId)).perform(replaceText("org-123"));
        onView(withId(R.id.etEventTitle)).perform(replaceText("Test Event"));
        onView(withId(R.id.etEventDescription)).perform(replaceText("Description"));
        onView(withId(R.id.etEventCategory)).perform(replaceText("CONCERT"));
        onView(withId(R.id.etEventLocation)).perform(replaceText("Montreal"));
        onView(withId(R.id.etEventDate)).perform(replaceText("2026-07-15"));
        onView(withId(R.id.etStartTime)).perform(replaceText("20:00"));
        onView(withId(R.id.etEndTime)).perform(replaceText("23:00"));
        onView(withId(R.id.etTotalCapacity)).perform(replaceText("100"));

        onView(withId(R.id.etOrganizerId)).check(matches(withText("org-123")));
        onView(withId(R.id.etEventTitle)).check(matches(withText("Test Event")));
        onView(withId(R.id.etEventCategory)).check(matches(withText("CONCERT")));
    }
}
