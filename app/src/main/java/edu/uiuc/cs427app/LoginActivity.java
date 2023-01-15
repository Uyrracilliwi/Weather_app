package edu.uiuc.cs427app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/*
 * This class is responsible for the situation when a user logs into the application.
 */

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnCreate;

    /**
    * This method is called after LoginActivity has launched but before it starts running. onCreate saves the InstanceState of the activity.
    **/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set login_main XML file as main layout.
        setContentView(R.layout.activity_login);

        // if the current user exists, direct them to the main activity page
        if ( ParseUser.getCurrentUser() != null ) {
            goMainActivity();
        }

        // Search for etUsername Id within activity_main 
        etUsername = findViewById(R.id.etUsername);

        // Search for etPassword Id within activity_main 
        etPassword = findViewById(R.id.etPassword);

        // Search for btnLogin Id within activity_main 
        btnLogin = findViewById(R.id.btnLogin);

        // Search for btnCreate Id within activity_main 
        btnCreate = findViewById(R.id.btnCreate);

        // Protocol for when a user clicks on btnLogin.
        btnLogin.setOnClickListener(new View.OnClickListener() {

            // This method converts the inputted Username and Password to strings and logins in the user.
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick login button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password);
            }
        });
        
        // Protocol for when a user clicks on btnCreate.
        btnCreate.setOnClickListener(new View.OnClickListener() {
            // This method directs the user to the CreateActivity page so they can create a new account.
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick signup button");
                goCreateActivity();
            }
        });
    }
    
    /**
    * This method logs in the user and handles both success and failure scenarios. 
    **/ 
    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user" + username);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                // If parse exception exists, then display background message pop-up indicating failure in login.
                if ( e != null) {
                    Log.e(TAG, "Issue with Login", e);
                    return;
                }
                // Navigate to the main activity if the user has signed in successfully and display background message pop-up
                goMainActivity();
                Toast.makeText(LoginActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
    * This method directs the user to the Main Activity page.
    **/
    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        // Start MainActivity
        startActivity(i);
        // Calling this indicates current activity is done and should be closed
        finish();
    }
    
    /**
    * This method directs the user to the Create Activity page.
    **/
    private void goCreateActivity() {
        Intent i = new Intent(this, CreateActivity.class);
        // Start CreateActivity
        startActivity(i);
        // Calling this indicates current activity is done and should be closed
        finish();
    }
}
