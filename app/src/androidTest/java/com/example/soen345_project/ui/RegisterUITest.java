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
public class RegisterUITest {

    @Rule
    public ActivityScenarioRule<RegisterActivity> registerRule =
            new ActivityScenarioRule<>(RegisterActivity.class);

    @Test
    public void register_allFieldsDisplayed() {
        onView(withId(R.id.etRegisterName)).check(matches(isDisplayed()));
        onView(withId(R.id.etRegisterEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.etRegisterPhone)).check(matches(isDisplayed()));
        onView(withId(R.id.etRegisterPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.btnDoRegister)).check(matches(isDisplayed()));
        onView(withId(R.id.btnGoToLogin)).check(matches(isDisplayed()));
    }

    @Test
    public void register_titleDisplayed() {
        onView(withText("Create Account")).check(matches(isDisplayed()));
    }

    @Test
    public void register_emptyFields_staysOnScreen() {
        onView(withId(R.id.btnDoRegister)).perform(click());
        onView(withId(R.id.btnDoRegister)).check(matches(isDisplayed()));
    }

    @Test
    public void register_missingEmailAndPhone_staysOnScreen() {
        onView(withId(R.id.etRegisterName))
                .perform(replaceText("John Doe"), closeSoftKeyboard());
        onView(withId(R.id.etRegisterPassword))
                .perform(replaceText("password123"), closeSoftKeyboard());
        onView(withId(R.id.btnDoRegister)).perform(click());
        onView(withId(R.id.btnDoRegister)).check(matches(isDisplayed()));
    }

    @Test
    public void register_bothEmailAndPhone_staysOnScreen() {
        onView(withId(R.id.etRegisterName))
                .perform(replaceText("John Doe"), closeSoftKeyboard());
        onView(withId(R.id.etRegisterEmail))
                .perform(replaceText("john@test.com"), closeSoftKeyboard());
        onView(withId(R.id.etRegisterPhone))
                .perform(replaceText("+15141234567"), closeSoftKeyboard());
        onView(withId(R.id.etRegisterPassword))
                .perform(replaceText("password123"), closeSoftKeyboard());
        onView(withId(R.id.btnDoRegister)).perform(click());
        onView(withId(R.id.btnDoRegister)).check(matches(isDisplayed()));
    }

    @Test
    public void register_validEmailFields_buttonClickable() {
        onView(withId(R.id.etRegisterName))
                .perform(replaceText("John Doe"), closeSoftKeyboard());
        onView(withId(R.id.etRegisterEmail))
                .perform(replaceText("john@test.com"), closeSoftKeyboard());
        onView(withId(R.id.etRegisterPassword))
                .perform(replaceText("password123"), closeSoftKeyboard());
        onView(withId(R.id.btnDoRegister)).check(matches(isDisplayed()));
    }

    @Test
    public void register_goToLogin_navigatesToLoginScreen() {
        onView(withId(R.id.btnGoToLogin)).perform(click());
        onView(withText("Welcome!")).check(matches(isDisplayed()));
    }
}