package com.example.soen345_project.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.soen345_project.R;
import com.example.soen345_project.ui.AddEditEventActivity;
import com.example.soen345_project.ui.AdminManagementActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminUITest {

    // AdminManagementActivity

    @Test
    public void adminManagement_headerIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                AdminManagementActivity.class);
        intent.putExtra("adminId", "testAdminId");

        try (ActivityScenario<AdminManagementActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withText("Event Management")).check(matches(isDisplayed()));
        }
    }

    @Test
    public void adminManagement_addEventButtonIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                AdminManagementActivity.class);
        intent.putExtra("adminId", "testAdminId");

        try (ActivityScenario<AdminManagementActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnAddEvent)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void adminManagement_clickAddEvent_navigatesToAddEditScreen() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                AdminManagementActivity.class);
        intent.putExtra("adminId", "testAdminId");

        try (ActivityScenario<AdminManagementActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnAddEvent)).perform(click());
            onView(withId(R.id.tvAddEditTitle)).check(matches(withText("Add Event")));
        }
    }

    @Test
    public void adminManagement_recyclerViewIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                AdminManagementActivity.class);
        intent.putExtra("adminId", "testAdminId");

        try (ActivityScenario<AdminManagementActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.rvAdminEvents)).check(matches(isDisplayed()));
        }
    }

    // AddEditEventActivity

    @Test
    public void addEvent_allFieldsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                AddEditEventActivity.class);
        intent.putExtra("adminId", "testAdminId");
        intent.putExtra("isEdit", false);

        try (ActivityScenario<AddEditEventActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.etEventTitle)).check(matches(isDisplayed()));
            onView(withId(R.id.etEventLocation)).check(matches(isDisplayed()));
            onView(withId(R.id.etEventCategory)).check(matches(isDisplayed()));
            onView(withId(R.id.etEventDate)).check(matches(isDisplayed()));
            onView(withId(R.id.etEventSeats)).check(matches(isDisplayed()));
            onView(withId(R.id.btnSaveEvent)).check(matches(isDisplayed()));
            onView(withId(R.id.btnCancelAddEdit)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void addEvent_titleShowsAddEvent() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                AddEditEventActivity.class);
        intent.putExtra("adminId", "testAdminId");
        intent.putExtra("isEdit", false);

        try (ActivityScenario<AddEditEventActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.tvAddEditTitle)).check(matches(withText("Add Event")));
        }
    }

    @Test
    public void editEvent_titleShowsEditEvent() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                AddEditEventActivity.class);
        intent.putExtra("adminId", "testAdminId");
        intent.putExtra("isEdit", true);
        intent.putExtra("eventId", "testEventId");

        try (ActivityScenario<AddEditEventActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.tvAddEditTitle)).check(matches(withText("Edit Event")));
        }
    }

    @Test
    public void addEvent_emptyFields_showsToast() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                AddEditEventActivity.class);
        intent.putExtra("adminId", "testAdminId");
        intent.putExtra("isEdit", false);

        try (ActivityScenario<AddEditEventActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnSaveEvent)).perform(click());
            // toast should appear — fields are empty
            onView(withId(R.id.etEventTitle)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void addEvent_fillAllFields_saveButtonClickable() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                AddEditEventActivity.class);
        intent.putExtra("adminId", "testAdminId");
        intent.putExtra("isEdit", false);

        try (ActivityScenario<AddEditEventActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.etEventTitle))
                    .perform(replaceText("Comedy Show"), closeSoftKeyboard());
            onView(withId(R.id.etEventLocation))
                    .perform(replaceText("Montreal"), closeSoftKeyboard());
            onView(withId(R.id.etEventCategory))
                    .perform(replaceText("Comedy"), closeSoftKeyboard());
            onView(withId(R.id.etEventSeats))
                    .perform(replaceText("100"), closeSoftKeyboard());
            onView(withId(R.id.btnSaveEvent)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void addEvent_cancelButton_closesActivity() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                AddEditEventActivity.class);
        intent.putExtra("adminId", "testAdminId");
        intent.putExtra("isEdit", false);

        try (ActivityScenario<AddEditEventActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnCancelAddEdit)).perform(click());
        }
    }
}