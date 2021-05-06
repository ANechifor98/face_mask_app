package com.example.fmaskdet;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.provider.MediaStore;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class camera {
    @Rule
    public IntentsTestRule<MainActivity> rule =
            new IntentsTestRule<MainActivity>(MainActivity.class);

    @Test
    public void testCameraIntent() throws Exception {
        // ActivityResult which returns from the camera app

        Intent data = new Intent();
        data.putExtra(MediaStore.EXTRA_OUTPUT, true);
        Instrumentation.ActivityResult answer = new Instrumentation.ActivityResult(Activity.RESULT_OK, data);
        // Copy the result of what the camera responds with,
        // Espresso responds with result of intent that is sent to camera in here
        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(answer);
        // click on button that opens camera
        onView(withId(R.id.photoselect)).perform(click());
        // checking if intent from camera has been sent from out
        intended(hasAction(MediaStore.ACTION_IMAGE_CAPTURE));
        // onActivityResult() called to check if imageview matches what is suppose to display
        onView(withId(R.id.viewimage)).check(matches(isDisplayed()));
    }
}
