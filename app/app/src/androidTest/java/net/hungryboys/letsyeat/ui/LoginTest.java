package net.hungryboys.letsyeat.ui;


import android.content.Intent;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import net.hungryboys.letsyeat.R;
import net.hungryboys.letsyeat.login.LoginActivity;
import net.hungryboys.letsyeat.login.LoginRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.regex.Pattern;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class LoginTest {

    private static final long DEFAULT_TIMEOUT = 5000;
    private static final String LOGIN_PACKAGE = "net.hungryboys.letsyeat.login";

    private UiDevice device;

    @Before
    public void before() {
        // Initialize UiDevice instance
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Start from the home screen
        device.pressHome();

        // Wait for launcher
        final String launcherPackage = device.getLauncherPackageName();
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), DEFAULT_TIMEOUT);

    }


    @Rule
    public IntentsTestRule<LoginActivity> intentsRule = new IntentsTestRule<>(LoginActivity.class, false, false);

    private void launchActivityAsLoggedOut() {
        intentsRule.launchActivity(new Intent());
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
        device.wait(Until.findObject(By.pkg("com.google.android.gms")), DEFAULT_TIMEOUT);
        device.pressBack();
    }

    @Test
    public void logsInWithGoogleAndChangesActivity() {
        launchActivityAsLoggedOut();

        onView(withId(R.id.google_login_button)).perform(click());
        device.wait(Until.findObject(By.pkg("com.google.android.gms").depth(0)), DEFAULT_TIMEOUT);
        device.wait(Until.findObject(By.textContains("@")), DEFAULT_TIMEOUT);

        if (device.hasObject(By.textContains("@"))) {
            device.findObject(By.textContains("@")).click();
        }

        device.wait(Until.findObject(By.pkg(Pattern.compile("net.hungryboys.letsyeat.(registration|browse)"))), DEFAULT_TIMEOUT);

        if (device.hasObject(By.pkg("net.hungryboys.letsyeat.browse"))) {
            LoginRepository repo = LoginRepository.getInstance(intentsRule.getActivity().getApplicationContext());
            assertTrue(repo.isLoggedIn());
        }
    }

    @Test
    public void doesNotLoginWithGoogleWhenCanceled() {
        launchActivityAsLoggedOut();

        onView(withId(R.id.google_login_button)).perform(click());
        device.wait(Until.findObject(By.pkg("com.google.android.gms").depth(0)), DEFAULT_TIMEOUT);
        device.pressBack();

        // Check toast appears
        onView(withText(R.string.login_failed))
                .inRoot(withDecorView(not(intentsRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }
}
