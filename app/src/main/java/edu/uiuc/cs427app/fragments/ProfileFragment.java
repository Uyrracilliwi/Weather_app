package edu.uiuc.cs427app.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

import edu.uiuc.cs427app.LoginActivity;
import edu.uiuc.cs427app.MainActivity;
import edu.uiuc.cs427app.R;
import edu.uiuc.cs427app.ResetPasswordActivity;
import edu.uiuc.cs427app.Theme;

/*
 * This class creates a fragment that allows a user to see their profile information.
 */
public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";

    private TextView uNameDisp, emailDisp, themeDisp;
    private Button btnLogout, btnSaveTheme, btnGoResetPass;
    private Spinner spNewUISelection;
    private String newUISelection;

    // Retrieve representation of current user's data.
    ParseUser currentUser = ParseUser.getCurrentUser();

    /*
    * This method adds a fragment that allows a user to view the the profile screen.
    */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment_profile xml layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    /*
    * This method enables the given fragment to instantiate its user interface view.
    */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Search for uNameDisp Id within fragment_profile.
        uNameDisp = view.findViewById(R.id.uNameDisp);

        // Search for emailDisp Id within fragment_profile.
        emailDisp = view.findViewById(R.id.emailDisp);

        // Search for themeDisp Id within fragment_profile.
        themeDisp = view.findViewById(R.id.themeDisp);

        // Search for btnLogout Id within fragment_profile.
        btnLogout = view.findViewById(R.id.btnLogout);

        // Search for btnSaveTheme Id within fragment_profile.
        btnSaveTheme = view.findViewById(R.id.btnSaveTheme);

        // Search for btnGoResetPass Id within fragment_profile.
        btnGoResetPass = view.findViewById(R.id.btnGoResetPass);

        // Search for spNewUISelection Id within fragment_profile.
        spNewUISelection = view.findViewById(R.id.spNewUISeletion);
        setUISeletionSpinner(spNewUISelection);

        // Set text of uNameDisp to current user's username.
        uNameDisp.setText(currentUser.getUsername());

        // Set text of emailDisp to current user's email address.
        emailDisp.setText(currentUser.getEmail());

        // Set text of themeDisp to current user's Ui Selection.
        themeDisp.setText(currentUser.get("uiSelection").toString());

        // Protocol logs out user when btnLogout is clicked.
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        // Protocol for when the user is the process of setting a new theme in the spinner.
        spNewUISelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            
            // This method updates a user's theme selection
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                newUISelection = Theme.getCurrentThemeName(i);
            }

            // Callback method that is invoked when the selection disappears from view.
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Protocol for saving the user's theme selection after clicking on btsSaveTheme.
        btnSaveTheme.setOnClickListener(new View.OnClickListener() {
            
            // This method saves the user's theme selection
            @Override
            public void onClick(View view) {
                currentUser.put("uiSelection", newUISelection);
                currentUser.saveInBackground(error -> {
                    // Show successful background message pop-up when error is null.
                    if (error == null) {
                        Toast.makeText(getContext(), "Save Successful", Toast.LENGTH_SHORT).show();
                        // user is taken to main page.
                        goMainActivity();
                    } else {
                        // Show background error message pop-up when error is not null.
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Protcol for when user clicks on btnGoResetPass
        btnGoResetPass.setOnClickListener(new View.OnClickListener() {

            // This method resets the password for a user.
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick Reset Password 1 button");
                goResetPasswordActivity();
            }
        });
    }

    /*
    * This method logs out the user. 
    */
    private void logOut() {
        Log.i(TAG, "onClick Logout button");
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
        goLoginActivity();
    }

    /*
    * This method navigates the user to MainActivity.
    */
    private void goMainActivity() {
        Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);
    }

    /*
    * This method navigates the user to the login page.
    */
    private void goLoginActivity() {
        Intent i = new Intent(getActivity(), LoginActivity.class);
        startActivity(i);
    }
    
    /*
    * This method resets the password of the user.
    */
    private void goResetPasswordActivity() {
        Intent i = new Intent(getActivity(), ResetPasswordActivity.class);
        startActivity(i);
    }

    /*
    * This method sets up the UI spinner with the available themes.
    */
    private void setUISeletionSpinner(Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.theme_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }
}
