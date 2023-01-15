package edu.uiuc.cs427app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

/*
 * This class is responsible for the situation when a user interact with the map.
 */
public class MapActivity extends AppCompatActivity {

    private Button btnBackDetail;
    private TextView tvCoord;
    private MapView mapView;

    private IMapController mapController;
    private ParseGeoPoint coord;

    // Retrieve representation of current user's data.
    ParseUser currentUser = ParseUser.getCurrentUser();

   /**
    * This method is called after MapActivity has launched but before it starts running. onCreate saves the InstanceState of the activity.
    **/
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load/initialize the osmdroid configuration
        Context context = getApplicationContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        //set the current theme to the user selected theme
        String themeName = currentUser.getString("uiSelection");
        setTheme(Theme.getTheme(themeName));

        // Set activity_map XML file as main layout.
        setContentView(R.layout.activity_map);
        
        // Search for btnBackDetail Id within activity_map.
        btnBackDetail = findViewById(R.id.btnBackDetail);

        // Protocol for when user clicks on btnBackDetail
        btnBackDetail.setOnClickListener(new View.OnClickListener() {
            // This 
            @Override
            public void onClick(View view) {
                // Calling this indicates current activity is done and should be closed
                finish();
            }
        });
        
        // Retrieve lattitude and longitude coordinates from Intent.
        coord = new ParseGeoPoint(
                getIntent().getDoubleExtra("LATITUDE", 0.0),
                getIntent().getDoubleExtra("LONGITUDE", 0.0)
        );

        // Search for tvCoord Id within activity_map.
        tvCoord = findViewById(R.id.tvCoord);

        // Display location, lattitude and longitude in textbox.
        tvCoord.setText(String.format("%s\n(%s, %s)", getIntent().getStringExtra("LOCATION"), coord.getLatitude(), coord.getLongitude()));

        // Search for tvCoord Id within mapView.
        mapView = (MapView) findViewById(R.id.mapView);

        // Set custom map server (TileSource) to Mapnik
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        // Invoke multi-touch gestures in mapView
        mapView.setMultiTouchControls(true);

        // Retrieve the controller for mapView
        mapController = mapView.getController();

        // Set the animation of the map to the given latitude nad longitude with a zoom of 15.
        GeoPoint point = new GeoPoint(coord.getLatitude(), coord.getLongitude());
        mapController.setZoom(15.0);
        mapController.animateTo(point);
    }
}
