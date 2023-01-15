package edu.uiuc.cs427app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.parse.ParseUser;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UserAuthenticationTest {
    String testUsername = "yueyin8";

    /* Slow down the test */
    public void sleep() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // user log out
    public void tapLogout() {
        onView(withId(R.id.action_profile)).perform(click());
        onView(withId(R.id.btnLogout)).perform(click());
    }

    // user log in
    public void userLogin() {
        ParseUser.logInInBackground(testUsername, "123456");

        ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.etUsername)).perform(typeText(testUsername));
        onView(withId(R.id.etPassword)).perform(typeText("123456"));
        onView(withId(R.id.btnLogin)).perform(click());

        ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.textViewUsername)).check(matches(withText(testUsername)));
    }

    /* user login testing */
    @Test
    public void testUserLogin() {
        userLogin();

        tapLogout();

        sleep();
    }

    /* user logout testing */
    @Test
    public void testUserLogout() {
        userLogin();
        tapLogout();

        ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()));

        sleep();
    }

    // set up Parse User
    public void setupParseUser(String testSignupEmail, String testSignupUsername) {
        ParseUser testUser = new ParseUser();
        testUser.setEmail(testSignupEmail);
        testUser.setUsername(testSignupUsername);
        testUser.setPassword("123456");
        testUser.put("uiSelection", "light");
        testUser.saveInBackground();
    }

    /* user signup testing */
    @Test
    public void testUserSignup() {
        Random rand = new Random();
        String testSignupUsername = "test" + rand.nextInt(10000);
        String testSignupEmail = testSignupUsername + "@test.com";
        setupParseUser(testSignupEmail, testSignupUsername);

        ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.btnCreate)).perform(click());

        ParseUser.logInInBackground(testSignupUsername, "123456");

        ActivityScenario.launch(CreateActivity.class);
        onView(withId(R.id.newEmail)).perform(typeText(testSignupEmail));
        onView(withId(R.id.newUsername)).perform(typeText(testSignupUsername));
        onView(withId(R.id.newPassword)).perform(typeText("123456"));
        onView(withId(R.id.btnSignUp)).perform(click());

        ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.textViewUsername)).check(matches(withText(testSignupUsername)));

        tapLogout();
        sleep();
    }
}
