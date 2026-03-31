package com.example.soen345_project.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.soen345_project.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginUITest {

    @Rule
    public ActivityScenarioRule<LoginActivity> loginRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void login_allFieldsDisplayed() {
        onView(withId(R.id.etEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.etPhone)).check(matches(isDisplayed()));
        onView(withId(R.id.etPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.btnGoToRegister)).check(matches(isDisplayed()));
    }

    @Test
    public void login_titleDisplayed() {
        onView(withText("Welcome!")).check(matches(isDisplayed()));
    }

    @Test
    public void login_emptyPassword_staysOnScreen() {
        onView(withId(R.id.etEmail))
                .perform(replaceText("test@example.com"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()));
    }

    @Test
    public void login_emptyEmailAndPhone_staysOnScreen() {
        onView(withId(R.id.etPassword))
                .perform(replaceText("password123"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()));
    }

    @Test
    public void login_bothEmailAndPhone_staysOnScreen() {
        onView(withId(R.id.etEmail))
                .perform(replaceText("test@example.com"), closeSoftKeyboard());
        onView(withId(R.id.etPhone))
                .perform(replaceText("+15141234567"), closeSoftKeyboard());
        onView(withId(R.id.etPassword))
                .perform(replaceText("password123"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()));
    }

    @Test
    public void login_goToRegister_navigatesToRegisterScreen() {
        onView(withId(R.id.btnGoToRegister)).perform(click());
        onView(withText("Create Account")).check(matches(isDisplayed()));
    }
}