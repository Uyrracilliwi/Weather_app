
package edu.uiuc.cs427app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

import android.net.Uri;
import android.os.StrictMode;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.util.TreeIterables;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.squareup.picasso.Picasso;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.uiuc.cs427app.weather.Weather;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@RunWith(AndroidJUnit4.class)
public class AllFeatureTest {
    public static final String WEATHER_API_KEY = "e1d1fba7061f402a95150220220312";
    public static final String WEATHER_BASE_URL = "https://api.weatherapi.com/v1";
    public static final String CURRENT = "/current.json";

    public double temp_c;
    public double temp_f;


    /* Slow down the test */
    public void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /** Add city
     * @param cityName the city you want to add
     */
    public void addCity(String cityName){
        onView(withId(R.id.action_add)).perform(click());
        sleep();
        onView(withId(R.id.etLocation)).perform(typeText(cityName));
        sleep();
        onView(withId(R.id.btnFind)).perform(click());
        sleep();
        onView(withId(R.id.btnAddLocation)).perform(click());
        sleep();
    }


    /* Add city test */
    @Test
    public void testAddCity() {
        ActivityScenario.launch(MainActivity.class);

        // add city Seattle
        addCity("Seattle");
        // add city Irvine
        addCity("Irvine");
        // add city Champaign
        addCity("Champaign");
        // add city New York
        addCity("New York");

        ViewAction itemViewAction = actionOnItemView(withId(R.id.btnDetail), click());
        int itemsCount = getCountFromRecyclerView(R.id.rvLocations);
        onView(withId(R.id.rvLocations)).perform(actionOnItemAtPosition(itemsCount-1, itemViewAction));
        sleep();

        // test the city is New York
        onView(withId(R.id.tvWeatherCityName)).check(matches((withText("New York"))));
        sleep();

        // back to main
        onView(withId(R.id.btnMain)).perform(click());
        sleep();
        onView(withId(R.id.rvLocations)).perform(actionOnItemAtPosition(itemsCount-2, itemViewAction));
        sleep();

        // test the city is Champaign
        onView(withId(R.id.tvWeatherCityName)).check(matches((withText("Champaign"))));
        sleep();
    }

    /* Delete city test */
    @Test
    public void testDeleteCity() {
        ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.action_home)).perform(click());
        sleep();

        ActivityScenario.launch(MainActivity.class);
        ViewAction itemViewAction = actionOnItemView(withId(R.id.btnDetail), click());
        int itemsCount = getCountFromRecyclerView(R.id.rvLocations);

        // delete New York from the list
        onView(withId(R.id.rvLocations)).perform(actionOnItemAtPosition(itemsCount-1, itemViewAction));
        sleep();
        onView(withId(R.id.btnDelete)).perform(click());
        sleep();
        onView(withId(R.id.rvLocations)).perform(actionOnItemAtPosition(itemsCount-2, itemViewAction));
        sleep();

        // check if new york is deleted
        onView(allOf(not(withText("Welcome to the New York")),withId(R.id.welcomeText))).check(matches(isDisplayed()));
        sleep();

        // back to main, delete Champaign from the list
        onView(withId(R.id.btnMain)).perform(click());
        sleep();
        onView(withId(R.id.rvLocations)).perform(actionOnItemAtPosition(itemsCount-2, itemViewAction));
        sleep();
        onView(withId(R.id.btnDelete)).perform(click());
        sleep();
        onView(withId(R.id.rvLocations)).perform(actionOnItemAtPosition(itemsCount-3, itemViewAction));
        sleep();

        // check if champaign is deleted
        onView(allOf(not(withText("Welcome to the Champaign")),withId(R.id.welcomeText))).check(matches(isDisplayed()));
        sleep();
    }

    /* Weather Feature testing */
    @Test
    public void testWeatherFeature() {
        ActivityScenario.launch(MainActivity.class);
        addCity("New York");
        addCity("Champaign");
        onView(withId(R.id.action_home)).perform(click());
        sleep();

        ActivityScenario.launch(MainActivity.class);
        ViewAction itemViewAction = actionOnItemView(withId(R.id.btnDetail), click());
        int itemsCount = getCountFromRecyclerView(R.id.rvLocations);

        // test 1st city weather feature
        // check the city name
        onView(withId(R.id.rvLocations)).perform(actionOnItemAtPosition(itemsCount-1, itemViewAction));
        onView(withId(R.id.tvWeatherCityName)).check(matches((withText("Champaign"))));

        /** region BONUS: Mocking location with city name "Champaign"
          * Fetch the real time weather data from WeatherApi and compare with viewable city temperature
          */
        String url1 = WEATHER_BASE_URL + CURRENT + "?key=" + WEATHER_API_KEY + "&q=" + "Champaign";
        fetchWeatherInfo(url1);
        onView(withId(R.id.tvTemp)).check(matches((withText(String.format("Temparature: %sC / %sF", String.valueOf(temp_c), (temp_f))))));
        sleep();
        /* endregion */

        // back to main
        onView(withId(R.id.btnMain)).perform(click());
        sleep();

        // test 2nd city weather feature
        // check the city name
        onView(withId(R.id.rvLocations)).perform(actionOnItemAtPosition(itemsCount-2, itemViewAction));
        onView(withId(R.id.tvWeatherCityName)).check(matches((withText("New York"))));

        /** region BONUS: Mocking location with city name "New York"
          * Fetch the real time weather data from WeatherApi and compare with viewable city temperature
          */
        String url2 = WEATHER_BASE_URL + CURRENT + "?key=" + WEATHER_API_KEY + "&q=" + "New York";
        fetchWeatherInfo(url2);
        onView(withId(R.id.tvTemp)).check(matches((withText(String.format("Temparature: %sC / %sF", String.valueOf(temp_c), (temp_f))))));
        sleep();
        /* endregion */
    }

    /* Location Feature testing */
    @Test
    public void testLocationFeature() {
        ActivityScenario.launch(MainActivity.class);
        addCity("New York");
        addCity("Champaign");
        onView(withId(R.id.action_home)).perform(click());
        sleep();

        ActivityScenario.launch(MainActivity.class);
        ViewAction itemViewAction = actionOnItemView(withId(R.id.btnDetail), click());
        int itemsCount = getCountFromRecyclerView(R.id.rvLocations);

        // test 1st city location feature
        // check the city name and coordinate
        onView(withId(R.id.rvLocations)).perform(actionOnItemAtPosition(itemsCount-2, itemViewAction));
        onView(withId(R.id.mapButton)).perform(click());
        onView(withId(R.id.tvCoord)).check(matches((withText("New York\n(40.7127753, -74.0059728)"))));
        sleep();

        onView(withId(R.id.btnBackDetail)).perform(click());
        onView(withId(R.id.btnMain)).perform(click());
        sleep();

        // test 2nd city location feature
        // check the city name and coordinate
        onView(withId(R.id.rvLocations)).perform(actionOnItemAtPosition(itemsCount-1, itemViewAction));
        onView(withId(R.id.mapButton)).perform(click());
        onView(withId(R.id.tvCoord)).check(matches((withText("Champaign\n(40.116420399999996, -88.2433829)"))));
        sleep();
    }


    // allow to access the button in the recycler view
    public static ViewAction actionOnItemView(Matcher<View> matcher, ViewAction action) {

        return new ViewAction() {

            @Override public String getDescription() {
                return String.format("performing ViewAction: %s on item matching: %s", action.getDescription(), StringDescription.asString(matcher));
            }

            @Override public Matcher<View> getConstraints() {
                return allOf(withParent(isAssignableFrom(RecyclerView.class)), isDisplayed());
            }

            @Override public void perform(UiController uiController, View view) {
                List<View> results = new ArrayList<>();
                for (View v : TreeIterables.breadthFirstViewTraversal(view)) {
                    if (matcher.matches(v)) results.add(v);
                }
                if (results.isEmpty()) {
                    throw new RuntimeException(String.format("No view found %s", StringDescription.asString(matcher)));
                } else if (results.size() > 1) {
                    throw new RuntimeException(String.format("Ambiguous views found %s", StringDescription.asString(matcher)));
                }
                action.perform(uiController, results.get(0));
            }
        };
    }


    // return the number of items in the recycler view
    public static int getCountFromRecyclerView(@IdRes int RecyclerViewId) {
        final int[] COUNT = {0};
        Matcher matcher = new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                COUNT[0] = ((RecyclerView) item).getAdapter().getItemCount();
                return true;
            }
            @Override
            public void describeTo(Description description) {
            }
        };
        onView(allOf(withId(RecyclerViewId),isDisplayed())).check(matches(matcher));
        int result = COUNT[0];
        COUNT[0] = 0;
        return result;
    }


    //help to check the item in the recycler view
    public static Matcher<View> hasItem(Matcher<View> matcher) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override public void describeTo(Description description) {
                description.appendText("has item: ");
                matcher.describeTo(description);
            }
            @Override protected boolean matchesSafely(RecyclerView view) {
                RecyclerView.Adapter adapter = view.getAdapter();
                for (int position = 0; position < adapter.getItemCount(); position++) {
                    int type = adapter.getItemViewType(position);
                    RecyclerView.ViewHolder holder = adapter.createViewHolder(view, type);
                    adapter.onBindViewHolder(holder, position);
                    if (matcher.matches(holder.itemView)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    private void fetchWeatherInfo(String url) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //OkHttp Asynchronous calling
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();

            //Get JSON object and get the values that we need by using loadCurrentWeather()
            try {
                loadCurrentWeather(new JSONObject(response.body().string()));
            } catch (JSONException e) {
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method retrieves the necessary data from the given JSON object and assign it to an attribute of Weather (e.g. temperature, feels like, cloud, humidity, wind, etc).
     */
    private void loadCurrentWeather(JSONObject json) throws JSONException {

        JSONObject currentWeather =  json.getJSONObject("current");
        temp_c = currentWeather.getDouble("temp_c");
        temp_f = currentWeather.getDouble("temp_f");

    }
}
