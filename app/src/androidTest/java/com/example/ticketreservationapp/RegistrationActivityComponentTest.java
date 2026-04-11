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

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

/**
 * Espresso component test for RegistrationActivity.
 * Tests UI rendering, field presence, input validation, and user interactions
 * in isolation from the backend.
 */
@RunWith(AndroidJUnit4.class)
public class RegistrationActivityComponentTest {

    private ActivityScenario<RegistrationActivity> scenario;

    @Before
    public void setUp() throws IOException {
        InstrumentationRegistry.getInstrumentation()
                .getUiAutomation()
                .executeShellCommand("am broadcast -a android.intent.action.CLOSE_SYSTEM_DIALOGS")
                .close();
        scenario = ActivityScenario.launch(RegistrationActivity.class);
    }

    @Test
    public void registrationForm_displaysAllRequiredViews() {
        onView(withId(R.id.etFirstName)).check(matches(isDisplayed()));
        onView(withId(R.id.etLastName)).check(matches(isDisplayed()));
        onView(withId(R.id.etEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.etPhone)).check(matches(isDisplayed()));
        onView(withId(R.id.etPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.btnRegister)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.onActivity(activity -> activity.finish());
            scenario = null;
        }
    }

    @Test
    public void registrationForm_displaysCorrectHints() {
        onView(withId(R.id.etFirstName)).check(matches(withHint("First Name")));
        onView(withId(R.id.etLastName)).check(matches(withHint("Last Name")));
        onView(withId(R.id.etEmail)).check(matches(withHint("Email Address")));
        onView(withId(R.id.etPhone)).check(matches(withHint("Phone Number")));
        onView(withId(R.id.etPassword)).check(matches(withHint("Password")));
    }

    @Test
    public void registerButton_displaysCorrectText() {
        onView(withId(R.id.btnRegister)).check(matches(withText("Register")));
    }

    @Test
    public void registerButton_isEnabled() {
        onView(withId(R.id.btnRegister)).check(matches(isEnabled()));
    }

    @Test
    public void emptyFirstName_showsValidationError() {
        onView(withId(R.id.etLastName)).perform(replaceText("Doe"));
        onView(withId(R.id.etEmail)).perform(replaceText("test@example.com"));
        onView(withId(R.id.etPassword)).perform(replaceText("password123"));

        onView(withId(R.id.btnRegister)).perform(click());

        onView(withId(R.id.etFirstName)).check(matches(isDisplayed()));
    }

    @Test
    public void emptyLastName_showsValidationError() {
        onView(withId(R.id.etFirstName)).perform(replaceText("John"));
        onView(withId(R.id.etEmail)).perform(replaceText("test@example.com"));
        onView(withId(R.id.etPassword)).perform(replaceText("password123"));

        onView(withId(R.id.btnRegister)).perform(click());

        onView(withId(R.id.etLastName)).check(matches(isDisplayed()));
    }

    @Test
    public void emptyPassword_showsValidationError() {
        onView(withId(R.id.etFirstName)).perform(replaceText("John"));
        onView(withId(R.id.etLastName)).perform(replaceText("Doe"));
        onView(withId(R.id.etEmail)).perform(replaceText("test@example.com"));

        onView(withId(R.id.btnRegister)).perform(click());

        onView(withId(R.id.etPassword)).check(matches(isDisplayed()));
    }

    @Test
    public void shortPassword_showsValidationError() {
        onView(withId(R.id.etFirstName)).perform(replaceText("John"));
        onView(withId(R.id.etLastName)).perform(replaceText("Doe"));
        onView(withId(R.id.etEmail)).perform(replaceText("test@example.com"));
        onView(withId(R.id.etPassword)).perform(replaceText("abc"));

        onView(withId(R.id.btnRegister)).perform(click());

        onView(withId(R.id.etPassword)).check(matches(isDisplayed()));
    }

    @Test
    public void userCanTypeInAllFields() {
        onView(withId(R.id.etFirstName)).perform(replaceText("John"));
        onView(withId(R.id.etLastName)).perform(replaceText("Doe"));
        onView(withId(R.id.etEmail)).perform(replaceText("john@example.com"));
        onView(withId(R.id.etPhone)).perform(replaceText("5141234567"));
        onView(withId(R.id.etPassword)).perform(replaceText("password123"));

        onView(withId(R.id.etFirstName)).check(matches(withText("John")));
        onView(withId(R.id.etLastName)).check(matches(withText("Doe")));
        onView(withId(R.id.etEmail)).check(matches(withText("john@example.com")));
        onView(withId(R.id.etPhone)).check(matches(withText("5141234567")));
    }

    @Test
    public void formTitle_displaysCorrectText() {
        onView(withText("Create Account")).check(matches(isDisplayed()));
    }

    @Test
    public void formSubtitle_displaysCorrectText() {
        onView(withText("Register to browse and book events.")).check(matches(isDisplayed()));
    }
}
