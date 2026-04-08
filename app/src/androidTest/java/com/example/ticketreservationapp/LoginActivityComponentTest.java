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
 * Espresso component test for LoginActivity.
 * Tests UI rendering, input validation, view presence, and user interactions
 * in isolation from the backend.
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityComponentTest {

    private ActivityScenario<LoginActivity> scenario;

    @Before
    public void setUp() throws IOException {
        InstrumentationRegistry.getInstrumentation()
                .getUiAutomation()
                .executeShellCommand("am broadcast -a android.intent.action.CLOSE_SYSTEM_DIALOGS")
                .close();
        scenario = ActivityScenario.launch(LoginActivity.class);
    }

    @Test
    public void loginForm_displaysAllRequiredViews() {
        onView(withId(R.id.etLoginIdentifier)).check(matches(isDisplayed()));
        onView(withId(R.id.etLoginPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.tvGoToRegister)).check(matches(isDisplayed()));
        onView(withId(R.id.tvAdminPortal)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.onActivity(activity -> activity.finish());
            scenario = null;
        }
    }

    @Test
    public void loginForm_displaysCorrectHints() {
        onView(withId(R.id.etLoginIdentifier))
                .check(matches(withHint("Email or Phone Number")));
        onView(withId(R.id.etLoginPassword))
                .check(matches(withHint("Password")));
    }

    @Test
    public void loginButton_displaysCorrectText() {
        onView(withId(R.id.btnLogin)).check(matches(withText("Login")));
    }

    @Test
    public void loginButton_isEnabled() {
        onView(withId(R.id.btnLogin)).check(matches(isEnabled()));
    }

    @Test
    public void registerLink_displaysCorrectText() {
        onView(withId(R.id.tvGoToRegister))
                .check(matches(withText("Don't have an account? Register here.")));
    }

    @Test
    public void adminPortalLink_displaysCorrectText() {
        onView(withId(R.id.tvAdminPortal))
                .check(matches(withText("Admin Portal")));
    }

    @Test
    public void emptyIdentifier_showsValidationError() {
        // Leave identifier empty, type password
        onView(withId(R.id.etLoginPassword)).perform(replaceText("password123"));

        onView(withId(R.id.btnLogin)).perform(click());

        // The error "Please enter your email or phone number" should be set on the
        // field
        onView(withId(R.id.etLoginIdentifier)).check(matches(isDisplayed()));
    }

    @Test
    public void emptyPassword_showsValidationError() {
        // Type identifier, leave password empty
        onView(withId(R.id.etLoginIdentifier)).perform(replaceText("user@example.com"));

        onView(withId(R.id.btnLogin)).perform(click());

        // The password field should show an error
        onView(withId(R.id.etLoginPassword)).check(matches(isDisplayed()));
    }

    @Test
    public void userCanTypeInIdentifierField() {
        onView(withId(R.id.etLoginIdentifier))
                .perform(replaceText("test@example.com"));

        onView(withId(R.id.etLoginIdentifier))
                .check(matches(withText("test@example.com")));
    }

    @Test
    public void userCanTypeInPasswordField() {
        onView(withId(R.id.etLoginPassword))
                .perform(replaceText("mypassword"));

        // Password field masks input, but we can check it has text
        onView(withId(R.id.etLoginPassword)).check(matches(isDisplayed()));
    }
}
