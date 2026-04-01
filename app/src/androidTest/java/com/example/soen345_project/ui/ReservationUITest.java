package com.example.soen345_project.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.soen345_project.R;

import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ReservationUITest {


    @Test
    public void eventList_recyclerViewIsDisplayed() {
        try (ActivityScenario<EventListActivity> scenario =
                     ActivityScenario.launch(EventListActivity.class)) {
            onView(withId(R.id.rvEvents)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void eventList_searchBarIsDisplayed() {
        try (ActivityScenario<EventListActivity> scenario =
                     ActivityScenario.launch(EventListActivity.class)) {
            onView(withId(R.id.etSearch)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void eventList_filterDateFieldIsDisplayed() {
        try (ActivityScenario<EventListActivity> scenario =
                     ActivityScenario.launch(EventListActivity.class)) {
            onView(withId(R.id.etFilterDate)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void eventList_filterLocationFieldIsDisplayed() {
        try (ActivityScenario<EventListActivity> scenario =
                     ActivityScenario.launch(EventListActivity.class)) {
            onView(withId(R.id.etFilterLocation)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void eventList_filterCategoryFieldIsDisplayed() {
        try (ActivityScenario<EventListActivity> scenario =
                     ActivityScenario.launch(EventListActivity.class)) {
            onView(withId(R.id.etFilterCategory)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void eventList_applyFiltersButtonIsDisplayed() {
        try (ActivityScenario<EventListActivity> scenario =
                     ActivityScenario.launch(EventListActivity.class)) {
            onView(withId(R.id.btnApplyFilter)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void eventList_logoutButtonIsDisplayed() {
        try (ActivityScenario<EventListActivity> scenario =
                     ActivityScenario.launch(EventListActivity.class)) {
            onView(withId(R.id.btnLogout)).check(matches(isDisplayed()));
        }
    }



    @Test
    public void eventList_clickApplyFilter_activityRemainsDisplayed() {
        // Verifies filters (even empty ones) does not crash
        try (ActivityScenario<EventListActivity> scenario =
                     ActivityScenario.launch(EventListActivity.class)) {
            onView(withId(R.id.btnApplyFilter)).perform(click());
            onView(withId(R.id.rvEvents)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void eventList_clickLogout_navigatesToLoginScreen() {
        // Verifies the logout button routes to LoginActivity (shows etEmail)
        try (ActivityScenario<EventListActivity> scenario =
                     ActivityScenario.launch(EventListActivity.class)) {
            onView(withId(R.id.btnLogout)).perform(click());
            onView(withId(R.id.etEmail)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void eventList_applyFilterWithEmptyFields_doesNotCrash() {
        // Edge case: all filter fields left blank, apply is pressed
        try (ActivityScenario<EventListActivity> scenario =
                     ActivityScenario.launch(EventListActivity.class)) {
            onView(withId(R.id.btnApplyFilter)).perform(click());
            onView(withId(R.id.rvEvents)).check(matches(isDisplayed()));
        }
    }
}