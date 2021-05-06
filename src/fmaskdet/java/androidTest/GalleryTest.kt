package com.example.fmaskdet

import android.app.Activity.RESULT_OK
import android.app.Instrumentation
import android.content.ContentResolver
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.provider.MediaStore
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction as hasAction

@RunWith(AndroidJUnit4ClassRunner::class)
class GalleryTest {
    @get: Rule
    val intentsTestRule = IntentsTestRule(MainActivity::class.java) // rule added to test intent

    @Test
    fun test_GalleryIntent() {
        val expectedResult: Matcher<Intent> = allOf( // what expected result will look like
            hasData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI) // want to pass what was done in the intent activity
        )
        val activityResult = createGalleryActivityResult() // call private function
        intending(expectedResult).respondWith(activityResult) // expect to see and what it should be responding with

        onView(withId(R.id.imageselect)).perform(click()) // verify with the Id of button that opens the gallery
        intended(expectedResult) // if correct thing is happening

    }
    private fun createGalleryActivityResult(): Instrumentation.ActivityResult { // function to return the expected result
        // mock out result of what is chosen from the gallery
        val values: Resources = InstrumentationRegistry.getInstrumentation() // get access to a drawable
            .context.resources // have access to context and resources
        val image = Uri.parse( // takes Uri of image
            ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + // creating content provider
                    values.getResourcePackageName(R.drawable.ic_launcher_background) + "/" +
                    values.getResourceTypeName(R.drawable.ic_launcher_background) + "/" +
                    values.getResourceEntryName(R.drawable.ic_launcher_background) // result of intent
        )
     val actualResult = Intent()
     actualResult.setData(image) // setting data to the intent
     return Instrumentation.ActivityResult(RESULT_OK, actualResult) // create the mock result we are looking for
    }
}