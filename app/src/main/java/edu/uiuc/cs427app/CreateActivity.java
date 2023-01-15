package edu.uiuc.cs427app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/*
 * This class is responsible for creating a new account.
 */
public class CreateActivity extends AppCompatActivity {

    public static final String TAG = "CreateActivity";

    private EditText etNewUsername, etNewPassword, etNewEmail;
    private Button btnSignUp, btnBack;
    private Spinner spUISeletion;
    private String uiSelection;

   /**
    * This method is called after CreateActivity has launched but before it starts running. onCreate saves the InstanceState of the activity.
    **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set activity_create XML file as main layout.
        setContentView(R.layout.activity_create);

        // Navigate to MainActivity if current user exists. 
        if(ParseUser.getCurrentUser() != null){
            goMainActivity();
        }

        // Search for etNewUsername Id within activity_create.
        etNewUsername = findViewById(R.id.newUsername);

        // Search for etNewPassword Id within activity_create.
        etNewPassword = findViewById(R.id.newPassword);

        // Search for etNewEmail Id within activity_create.
        etNewEmail = findViewById(R.id.newEmail);

        // Search for btnBack Id within activity_create.
        btnBack = findViewById(R.id.btnBack);

        // Protocol for when user clicks on btnBack.
        btnBack.setOnClickListener(new View.OnClickListener() {
            
            // This method redirects the user back to login page.
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick Back button");
                goLoginActivity();
            }
        });

        // Search for btnBack Id within activity_create then setup the UI Selection spinner.
        spUISeletion = (Spinner) findViewById(R.id.spUISeletion);
        setUISeletionSpinner(spUISeletion);

        // Protocol for when the user is the process of setting a new theme in the spinner.
        spUISeletion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            
            // This method updates the UI to the selected theme name. 
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                uiSelection = Theme.getCurrentThemeName(i);
            }

            // Callback method that is invoked when the selection disappears from view.
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                uiSelection = "Light";
            }
        });
        
        // Search for btnSignUp Id within activity_create.
        btnSignUp = findViewById(R.id.btnSignUp);

        // Protocol for creating user account after clicking btnSignup.
        btnSignUp.setOnClickListener(new View.OnClickListener() {
        
            // This method creates the user account.
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick Sign Up button");
                createUser();
            }
        });
    }

    /**
    * This method is responsible for creating a new user account.
    **/ 
    private void createUser() {
        Log.i(TAG, "Attempting to create user " + etNewUsername.getText().toString());

        ParseUser user = new ParseUser();
        
        // Set user's username, password email in database.
        user.setUsername(etNewUsername.getText().toString());
        user.setPassword(etNewPassword.getText().toString());
        user.setEmail(etNewEmail.getText().toString());

        // Protocol for showing appropriate background message pop-up depending on success of sign-up.
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Show Account Created message if Parse Exception is null. 
                    Toast.makeText(CreateActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                    // Input user's UI Selection to database. 
                    user.put("uiSelection", uiSelection);
                    user.saveInBackground(error -> {
                        if (error == null) {
                            // if error is null, show background pop-up success message. 
                            Toast.makeText(CreateActivity.this, "Save Successful", Toast.LENGTH_SHORT).show();
                            // user is taken to main page. 
                            goMainActivity();
                        } else {
                            // Show background pop-up error message when error is not null. 
                            Toast.makeText(CreateActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            // user is taken to login page. 
                            goLoginActivity();
                        }
                    });
                } else {
                    // Parse Exception is not null, meaning Sign up didn't succeed. Show background message that account creation failed to user.
                    Toast.makeText(CreateActivity.this, "Failed to Create Account", Toast.LENGTH_SHORT).show();
                    // user is taken to login page.
                    goLoginActivity();
                }
            }
        });
    }
    
    /*
    * This method navigates the user to MainActivity.
    */
    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
    
    /*
    * This method navigates the user to LoginActivity.
    */
    private void goLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    /*
    * This method sets up the UI spinner with the available themes.
    */
    private void setUISeletionSpinner(Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.theme_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }
}
