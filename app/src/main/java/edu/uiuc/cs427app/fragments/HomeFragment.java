package edu.uiuc.cs427app.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import edu.uiuc.cs427app.Location;
import edu.uiuc.cs427app.LocationsAdapter;
import edu.uiuc.cs427app.R;
import edu.uiuc.cs427app.databinding.ActivityMainBinding;

/*
 * This class creates a fragment that allows a user see the home screen and get their location list. 
 */
public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";

    private RecyclerView rvLocations;
    protected LocationsAdapter adapter;
    protected List<Location> allLocations;

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    // Retrieve representation of current user's data.
    ParseUser currentUser = ParseUser.getCurrentUser();

    /*
    * This method adds a fragment that allows a user to view the the home screen.
    */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment_home xml layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    /*
    * This method enables the given fragment to instantiate its user interface view.
    */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Search for textViewUseraname Id within fragment_home.
        TextView textViewUsername = view.findViewById(R.id.textViewUsername);

        // Set text to given user's username.
        textViewUsername.setText(currentUser.getUsername());
        
        // Register subclass for given location 
        ParseObject.registerSubclass(Location.class);
        
        // Fetch location list from Parse database.
        queryLocations();

        // Search for rvLocations Id within fragment_home.
        rvLocations = view.findViewById(R.id.rvLocations);

        // Create new adapter for rvLocations by passing an ArrayList and with existing list of locations.
        allLocations = new ArrayList<>();
        adapter = new LocationsAdapter(getContext(), allLocations);
        rvLocations.setAdapter(adapter);

        // Measuring and position rvLocations in a linear format. 
        rvLocations.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    /*
     * This method fetches a user's location list from the Parse database.
     */
    protected void queryLocations() {

        // Query for Location data for current User.
        ParseQuery<Location> query = ParseQuery.getQuery("Location");
        query.include(Location.KEY_USER);
        query.whereEqualTo(Location.KEY_USER, ParseUser.getCurrentUser());
        
        /*
        * Protocol shows background message pop-ups to user dependent on success in retrieving their location list.
        */
        query.findInBackground(new FindCallback<Location>() {
            @Override
            public void done(List<Location> locations, ParseException e) {
                
                // If ParseException is not null, show error message to user.
                if (e != null) {
                    Log.e(TAG, "Issue with getting letters", e);
                    return;
                }

                // If ParseException is null, fetch each location name + username for a given user.
                for(Location loc: locations){
                    try {
                        Log.i(TAG, "Post: " + loc.getName() + ", username: " + loc.getUser().fetchIfNeeded().getUsername());
                    } catch (ParseException parseException) {
                        parseException.printStackTrace();
                    }
                }
                // Add all locations to existing list.
                allLocations.addAll(locations);

                // Notify adapter the change in dataset.
                adapter.notifyDataSetChanged();
            }
        });
    }
}
