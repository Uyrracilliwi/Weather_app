package edu.uiuc.cs427app.fragments;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.List;

import edu.uiuc.cs427app.Location;
import edu.uiuc.cs427app.MainActivity;
import edu.uiuc.cs427app.R;

/*
 * This class creates a fragment that allows a given user to add a new location to the list.
 */
public class AddLocationFragment extends Fragment {

    public static final String TAG = "AddLocationFragment";

    private EditText etLocation;
    private Button btnFind, btnAddLocation;
    private TextView tvLocationInfo;

    private Geocoder geocoder;
    private Address address;

    // Retrieve representation of current user's data.
    private ParseUser currentUser = ParseUser.getCurrentUser();

    /*
    * This method adds a fragment that allows a user to add locations to the list.
    */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment_add_location xml layout for this fragment
        return inflater.inflate(R.layout.fragment_add_location, container, false);
    }

    /*
    * This method enables the given fragment to instantiate its user interface view.
    */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Transforms description of a location into a coordinate (latitude, longitude).
        geocoder = new Geocoder(getContext());

        // Search for Location Id within fragment_add_location.
        etLocation = view.findViewById(R.id.etLocation);

        // Search for btnFind Id within fragment_add_location.
        btnFind = view.findViewById(R.id.btnFind);

        // Search for btnAddLocation Id within fragment_add_location.
        btnAddLocation = view.findViewById(R.id.btnAddLocation);

        // Search for tvLocationInfo Id within fragment_add_location.
        tvLocationInfo = view.findViewById(R.id.tvLocationInfo);

        /*
        * Protocol for finding address information for a given location when btnFind button is clicked
        */
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            
            public void onClick(View view) {
                try {
                    // Get Address information from the location name that user entered
                    List<Address> addresses = geocoder.getFromLocationName(etLocation.getText().toString(), 1);
                    for (Address addr : addresses) {
                        String locInfo = addr.getLocality() + ", " +
                                addr.getAdminArea() + ", " +
                                addr.getCountryName();
                        address = addr;
                        tvLocationInfo.setText(locInfo);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

       /*
        * Protocol for saving the address information for a given location when btnAddLocation button is clicked
        */
        btnAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveLocation(address);
            }
        });
    }

    /*
     * This method saves the locality info of a given address to the location list and if successful, goes back to home screen.
     */
    private void saveLocation(Address address) {
        Location location = new Location();

        // This if else statement checks if the locality info of a given address is null.  
        if (address.getLocality() == null) { location.setName(address.getSubAdminArea()); }
        else { location.setName(address.getLocality()); }

        // Create a new ParseGeoPoint with given address lattitude and longitutde.
        ParseGeoPoint geoPoint = new ParseGeoPoint(address.getLatitude(), address.getLongitude());

        // Set coordinates of location given geoPoint's coordinates.
        location.setCoord(geoPoint);

        // Set the User associated with the location to currentUser.
        location.setUser(currentUser);

        location.saveInBackground(new SaveCallback() {
            /*
            * This method shows background message pop-ups to user dependent on success in saving locality information. 
            */
            @Override
            public void done(ParseException e) {

                // If ParseException is not null, show error message to user.
                if(e != null){
                    Log.e(TAG,"Error while saving",e);
                    Toast.makeText(getContext(),"Error while saving", Toast.LENGTH_SHORT).show();
                }
                // If ParseException is null, show success message to user. 
                Toast.makeText(getContext(), "Post save was successful", Toast.LENGTH_SHORT).show();

                // Set etLocation search box text to be blank.
                etLocation.setText("");

                // Set tvLocationInfo text to be blank.
                tvLocationInfo.setText("");

                // Clear focus of etLocation search box
                etLocation.clearFocus();

                // Return user to home screen.
                goHomeFragment();
            }
        });
    }

    // This method navigates the user to the Home screen.
    private void goHomeFragment() {
        Intent i = new Intent(getContext(), MainActivity.class);
        startActivity(i);
    }
}