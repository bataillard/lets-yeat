package net.hungryboys.letsyeat.ui;


import android.content.Intent;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.SearchCondition;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import net.hungryboys.letsyeat.R;
import net.hungryboys.letsyeat.login.LoginActivity;
import net.hungryboys.letsyeat.login.LoginRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class LoginTest {

    private static final long DEFAULT_TIMEOUT = 1000;

    private UiDevice mUiDevice;

    @Before
    public void before() {
        mUiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }


    @Rule
    public IntentsTestRule<LoginActivity> intentsRule = new IntentsTestRule<>(LoginActivity.class);

    private void launchActivityAsLoggedOut() {
        LoginRepository.getInstance(intentsRule.getActivity()).removeUserCredentials();
        intentsRule.finishActivity();
        intentsRule.launchActivity(new Intent());
    }

    @Test
    public void googleButtonExists() {
        launchActivityAsLoggedOut();

        onView(withId(R.id.google_login_button)).check(matches(isDisplayed()));
    }

    @Test
    public void googleButtonLaunchesGoogleLoginActivity() {
        launchActivityAsLoggedOut();

        onView(withId(R.id.google_login_button)).perform(click());
        intended(toPackage("com.google.android.gms"));
        mUiDevice.wait(Until.findObject(By.text("Choose an account")), DEFAULT_TIMEOUT);
        mUiDevice.pressBack();
    }
}
