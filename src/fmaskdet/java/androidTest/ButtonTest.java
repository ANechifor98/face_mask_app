package com.example.fmaskdet;

import androidx.annotation.ContentView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class ButtonTest {
    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule =
            new ActivityTestRule<MainActivity>(MainActivity.class);
    @Test
    public void clickButton() throws Exception {
        onView(withId(R.id.photoselect)).perform(click());
        //.check(matches(isDisplayed()));
        //onView(withId(R.id.imageselect)).perform(click());

    }

    @Test
    public void clickButton1() throws Exception {
        onView(withId(R.id.imageselect)).perform(click());
        //.check(matches(isDisplayed()));
    }
}
