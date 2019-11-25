package net.hungryboys.letsyeat.ui;

import android.content.Intent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.test.espresso.IdlingPolicies;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.SearchCondition;
import androidx.test.uiautomator.UiCollection;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import net.hungryboys.letsyeat.R;
import net.hungryboys.letsyeat.data.RecipeID;
import net.hungryboys.letsyeat.login.LoginActivity;
import net.hungryboys.letsyeat.login.LoginRepository;
import net.hungryboys.letsyeat.recipe.RecipeActivity;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class NotificationsTest {

    private static final long DEFAULT_TIMEOUT = 5000;
    private static final String LOGIN_PACKAGE = "net.hungryboys.letsyeat.login";

    private static final String TEST_RECIPE_ID = "notification_test";

    private UiDevice device;

    @Rule
    public ActivityTestRule<RecipeActivity> rule = new ActivityTestRule<>(RecipeActivity.class, false, false);

    @Rule
    public  ActivityTestRule<LoginActivity> before = new ActivityTestRule<>(LoginActivity.class, false, false);

    @Before
    public void before() {
        // Initialize UiDevice instance
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Start from the home screen
        device.pressHome();
    }

    @Test
    public void sendsNotificationInTwoMinutes() {
        final int ONE_MINUTE_TIMEOUT = 60*2000;
        final int BUFFER_TIME = 10000;

        before.launchActivity(new Intent());
        onView(withId(R.id.google_login_button)).perform(click());

        device.wait(Until.findObject(By.pkg(Pattern.compile("net.hungryboys.letsyeat.(registration|browse)"))), DEFAULT_TIMEOUT);
        before.finishActivity();

        device.pressHome();

        Intent intent = new Intent();
        intent.putExtra(RecipeActivity.EXTRA_RECIPE_ID, new RecipeID(TEST_RECIPE_ID));
        rule.launchActivity(intent);

        // Make sure user is already logged in.
        assertTrue(LoginRepository.getInstance(rule.getActivity().getApplicationContext()).isLoggedIn());

        Calendar inOneMinute = Calendar.getInstance();
        inOneMinute.add(Calendar.MINUTE, 2);

        device.wait(Until.findObject(By.textContains("Cook")), DEFAULT_TIMEOUT);

        onView(withId(R.id.recipe_cook_button)).perform(click());

        device.wait(Until.findObject(By.textContains("Custom Time")), DEFAULT_TIMEOUT);

        onView(withText("Custom Time")).perform(click());

        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
            .perform(setDate(inOneMinute.get(Calendar.YEAR), inOneMinute.get(Calendar.MONTH), inOneMinute.get(Calendar.DAY_OF_MONTH)));
        onView(withText("OK")).perform(click());

        onView(withClassName(Matchers.equalTo(TimePicker.class.getName())))
                .perform(setTime(inOneMinute.get(Calendar.HOUR_OF_DAY), inOneMinute.get(Calendar.MINUTE)));
        onView(withText("OK")).perform(click());
        IdlingPolicies.setIdlingResourceTimeout(5, TimeUnit.SECONDS);
        onView(withText(R.string.cook_confirmed))
                .inRoot(withDecorView(not(rule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));

        device.openNotification();
        device.wait(Until.findObject(By.textContains("Let's Yeat")), ONE_MINUTE_TIMEOUT + BUFFER_TIME);
    }

    public static ViewAction setTime(final int hour, final int minute) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                TimePicker tp = (TimePicker) view;
                tp.setCurrentHour(hour);
                tp.setCurrentMinute(minute);
            }
            @Override
            public String getDescription() {
                return "Set the passed time into the TimePicker";
            }
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(TimePicker.class);
            }
        };
    }

    public static ViewAction setDate(final int year, final int month, final int day) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                DatePicker dp = (DatePicker) view;
                dp.updateDate(year, month, day);
            }
            @Override
            public String getDescription() {
                return "Set the passed date into the DatePicker";
            }
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(DatePicker.class);
            }
        };
    }
}
