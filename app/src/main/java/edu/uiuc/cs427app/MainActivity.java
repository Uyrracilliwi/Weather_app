package edu.uiuc.cs427app;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import edu.uiuc.cs427app.fragments.AddLocationFragment;
import edu.uiuc.cs427app.fragments.HomeFragment;
import edu.uiuc.cs427app.fragments.ProfileFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;


/** 
* This class begins immediately after the user logs in. This is the first screen to appear when user launches app. 
**/
public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    // Create an instance of FragmentManager for interacting with fragments associated with this activity.
    final FragmentManager fragmentManager = getSupportFragmentManager();

    private BottomNavigationView bottomNavigationView;

    // Retrieve representation of current user's data.
    ParseUser currentUser = ParseUser.getCurrentUser();

    /**
    * This method is called after Main Activity has launched but before it starts running. onCreate saves the InstanceState of the activity.
    **/ 
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve UI Selection associated with current user
        String themeName = currentUser.getString("uiSelection");

        // Set the theme of Main Activity to the UI selected by the user.
        setTheme(Theme.getTheme(themeName));

        // Set activity_main XML file as main layout.
        setContentView(R.layout.activity_main);

        // Search for bottomNavigation Id within activity_main 
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Listener for handling selection events on bottom navigation items.
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            
            /**
            * This method handles the logic for alternating between Home, AddLocation and Profile fragments within the application. It includes a switch statement that updates the given fragment shown to a user based on their selected MenuItem and returns a boolean.
            **/ 
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;

                // Retrieve the MenuItem that is selected by user. 
                switch (item.getItemId()) {
                    case R.id.action_home:
                        // update UI to Home screen.
                        fragment = new HomeFragment();
                        break;
                    case R.id.action_add:
                        // update UI to Add Location screen.
                        fragment = new AddLocationFragment();
                        break;
                    case R.id.action_profile:
                    default:
                        // update UI to Profile screen.
                        fragment = new ProfileFragment();
                        break;
                }

                // Create instance of FragmentTransaction, then add updated fragment to the transaction.
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        // Set default fragment selection for bottom navigation bar.
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }
}

