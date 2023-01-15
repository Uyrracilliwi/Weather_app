package edu.uiuc.cs427app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import edu.uiuc.cs427app.weather.Weather;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*
 * This class is responsible for the detail information about a location.
 */
public class LocationDetailsActivity extends AppCompatActivity {

    // Use WeatherApi to get the weather information for the location
    public static final String WEATHER_API_KEY = "e1d1fba7061f402a95150220220312";
    public static final String WEATHER_BASE_URL = "https://api.weatherapi.com/v1";
    public static final String CURRENT = "/current.json";
    public static final String FORECAST = "/forecast.json";

    private ImageView ivCondition;
    private TextView tvWeatherCityName, welcomeMessage, tvTemp, tvFeelslike, tvUV, tvCondition, tvWind, tvHumidity;
    private Button buttonMap, btnDelete, btnMain;
    private ProgressBar pbLoading;

    private Location location;
    private Weather weather;

    private ParseUser currentUser = ParseUser.getCurrentUser();

    /**
     * This method is called after LocationDetailsActivity Activity has launched but before it starts running. onCreate saves the InstanceState of the activity.
     **/
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the current theme to the user selected theme
        String themeName = currentUser.getString("uiSelection");
        setTheme(Theme.getTheme(themeName));

        // Set activity_details XML file as main layout.
        setContentView(R.layout.activity_details);

        // Search for welcomeText, ivCondition, GONE, pbLoading, tvTemp, tvFeelslike, tvUV, tvCondition, tvWind, tvHumidity, mapButton, btnDelete, btnMain within activity_details
        tvWeatherCityName = findViewById(R.id.tvWeatherCityName);
        welcomeMessage = findViewById(R.id.welcomeText);
        ivCondition = findViewById(R.id.ivCondition);
        ivCondition.setVisibility(View.GONE);
        pbLoading = findViewById(R.id.pbLoading);
        tvTemp = findViewById(R.id.tvTemp);
        tvFeelslike = findViewById(R.id.tvFeelslike);
        tvUV = findViewById(R.id.tvUV);
        tvCondition = findViewById(R.id.tvCondition);
        tvWind = findViewById(R.id.tvWind);
        tvHumidity = findViewById(R.id.tvHumidity);
        buttonMap = findViewById(R.id.mapButton);
        btnDelete = findViewById(R.id.btnDelete);
        btnMain = findViewById(R.id.btnMain);

        // Query for location in database.
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Location");

        weather = new Weather();

        // Retrieve locationID from intent.
        String locationId = getIntent().getStringExtra("LOCATION_ID");

        query.getInBackground(locationId, (loc, error) -> {
            if (error != null) {
                // Something went wrong and display background message pop-up to user.
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
            else {
                // If nothing went wrong, set the new location with its name, coordinates and current user.
                location = new Location();
                location.setName(loc.getString("name"));
                location.setCoord(loc.getParseGeoPoint("coordinate"));
                location.setUser(currentUser);

                // Retrieve cityName and display welcome message to user along with text that shows info related to the city's weather.
                String cityName = location.getName();
                String welcome = "Welcome to the " + cityName;
                String cityWeatherInfo = "Detailed information about the weather of " + cityName;
                welcomeMessage.setText(welcome);
                tvTemp.setText(cityWeatherInfo);

                String url = WEATHER_BASE_URL + CURRENT + "?key=" + WEATHER_API_KEY + "&q=" + location.getName();
                fetchWeatherInfo(url);

                // User clicked the buttonMap => get the weather information from a Service that connects to a weather server and show the results
                buttonMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goMapActicvity(loc.getParseGeoPoint("coordinate"));
                    }
                });
                // User clicked the delete button => run deleteLocation which will remove the location from user's location list
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteLocation(locationId);
                    }
                });
                //User clicked the main button => it directs to the main activity page
                btnMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goMainActivity();
                    }
                });
            }
        });
    }


    /**
     * This method is responsible for deleting a location from the current user's location list
     **/
    private void deleteLocation(String locId) {
        ParseQuery<Location> query = ParseQuery.getQuery("Location");
        query.getInBackground(locId, (object, error) -> {
            if (error == null) {
                // Deletes the fetched ParseObject from the database if an error iexists
                object.deleteInBackground(e -> {
                    if(e == null) {
                        // if no error exists, then navigate user to main activity page.
                        goMainActivity();
                    } else {
                        //Something went wrong while deleting the Object
                    }
                });
            } else {
                //Something went wrong while retrieving the Object
            }
        });
    }

    /**
     * This method is responsible for directing the user to the main actiivty page.
     **/
    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        // Start MainActivity
        startActivity(i);
        // Calling this indicates current activity is done and should be closed
        finish();
    }

    /**
     * This method directs the user to the MapActivity page, inputting given location, lattitude and longitude coordinates.
     **/
    private void goMapActicvity(ParseGeoPoint coord) {
        Intent i = new Intent(this, MapActivity.class);
        i.putExtra("LOCATION", location.getName());
        i.putExtra("LATITUDE", coord.getLatitude());
        i.putExtra("LONGITUDE", coord.getLongitude());
        startActivity(i);
    }

    /**
     * This method retrieves the current weather information.
     * @param url use to get weather info from WeatherApi
     */
    private void fetchWeatherInfo(String url) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //OkHttp Asynchronous calling
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();

            pbLoading.setVisibility(View.GONE);

            //Get JSON object and get the values that we need by using loadCurrentWeather()
            try {
                loadCurrentWeather(new JSONObject(response.body().string()));
            } catch (JSONException e) {
                return;
            }

            // Update TextView to display all weather-related information below.
            tvWeatherCityName.setText(location.getName());
            welcomeMessage.setText(String.format("%s\nLocal time: %s", location.getName(), weather.local_time));
            tvTemp.setText(String.format("Temparature: %sC / %sF", weather.temp_c.toString(), weather.temp_f));
            tvFeelslike.setText(String.format("Feels like: %sC / %sF", weather.feelslike_c.toString(), weather.feelslike_f));
            tvUV.setText(String.format("UV: %s", weather.uv));
            tvWind.setText(String.format(
                    "Wind:\n\tDirection: %s\n\tDegree: %s\n\tSpeed(mph): %s",
                    weather.wind.wind_dir,
                    weather.wind.wind_degree.toString(),
                    weather.wind.wind_mph.toString()
            ));
            tvCondition.setText(String.format("Condition: %s", weather.condition.text));
            tvHumidity.setText(String.format("Humidity: %s", weather.humidity));
            Picasso.with(this).load(Uri.parse("https:" + weather.condition.icon)).into(ivCondition);
            ivCondition.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method retrieves the necessary data from the given JSON object and assign it to an attribute of Weather (e.g. temperature, feels like, cloud, humidity, wind, etc).
     */
    private void loadCurrentWeather(JSONObject json) throws JSONException {
        JSONObject currentWeather =  json.getJSONObject("current");
        weather.temp_c = currentWeather.getDouble("temp_c");
        weather.temp_f = currentWeather.getDouble("temp_f");
        weather.feelslike_c = currentWeather.getDouble("feelslike_c");
        weather.feelslike_f = currentWeather.getDouble("feelslike_f");
        weather.cloud = currentWeather.getInt("cloud");
        weather.uv = currentWeather.getDouble("uv");
        weather.humidity = currentWeather.getInt("humidity");
        weather.wind = new Weather.Wind(
                currentWeather.getDouble("wind_mph"),
                currentWeather.getInt("wind_degree"),
                currentWeather.getString("wind_dir")
        );
        weather.condition = new Weather.Condition(
                currentWeather.getJSONObject("condition").getString("text"),
                currentWeather.getJSONObject("condition").getString("icon"),
                currentWeather.getJSONObject("condition").getString("code")
        );
        weather.local_time = json.getJSONObject("location").getString("localtime");
    }
}