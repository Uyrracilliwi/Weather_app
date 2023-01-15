package edu.uiuc.cs427app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/*
 * This class is responsible for resetting an account's password.
 */
public class ResetPasswordActivity extends AppCompatActivity {

    public static final String TAG = "CreateActivity";

    private EditText etUserPass, etNewPass;
    private Button btnResetPassword, btnBackProfile;

    // Retrieve representation of current user's data.
    ParseUser currentUser = ParseUser.getCurrentUser();

   /**
    * This method is called after ResetPasswordActivity has launched but before it starts running. onCreate saves the InstanceState of the activity.
    **/
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve user's UI Selection.
        String themeName = currentUser.getString("uiSelection");

        // Set theme as user's selection.
        setTheme(Theme.getTheme(themeName));

        // Set activity_reset_password XML file as main layout.
        setContentView(R.layout.activity_reset_password);

        // Search for etUserPass Id within activity_reset_password.
        etUserPass = findViewById(R.id.etUserPass);

        // Search for etNewPass Id within activity_reset_password.
        etNewPass = findViewById(R.id.etNewPass);

        // Search for btnResetPassword Id within activity_reset_password.
        btnResetPassword = findViewById(R.id.btnResetPassword);

        // Search for btnBackProfile Id within activity_reset_password.
        btnBackProfile = findViewById(R.id.btnBackProfile);

        //Protocol for when user clicks the password reset btn
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            
            // This method resets the user's password
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick Sign Up button");

                // Convert text inputs in search boxes etUserPass and etNewPass to strings.
                String currPass = etUserPass.getText().toString();
                String newPass = etNewPass.getText().toString();

                // Protocol for handling background message pop-up when password change is successful/unsuccessful
                ParseUser.logInInBackground(currentUser.getUsername(), currPass, new LogInCallback() {
                    @Override
                    // This method is responsible for displaying the background message pop-up depending on a successful/unsuccessful password change. 
                    public void done(ParseUser user, ParseException e) {
                        if (e != null) {
                            // When ParseException exists, send user background message pop-up indicating wrong password inputted.
                            Log.e(TAG, "Wrong Password", e);
                            Toast.makeText(ResetPasswordActivity.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // Reset the password since there is no ParseException
                        resetPassword(newPass);
                        // Send user background message pop-up indicating successful password change.
                        Toast.makeText(ResetPasswordActivity.this, "Password Successful Changed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Protocol for when user clicks on btnBanckProfile
        btnBackProfile.setOnClickListener(new View.OnClickListener() {
            
            // This method takes the user to previous page.
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
    }

    
    /**
    * This method sets the user's password to the newPassword. It saves in background and directs the user to the previous page.
    **/ 
    private void resetPassword(String newPass) {
        currentUser.setPassword(newPass);
        currentUser.saveInBackground();
        // Calling indicates activity is done and should be closed
        finish();
    }

    /**
    * This method directs the user to the previous page
    **/ 
    private void goBack() {
        finish();
    }
}
