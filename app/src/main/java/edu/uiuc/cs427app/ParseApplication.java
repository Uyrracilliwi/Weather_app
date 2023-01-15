package edu.uiuc.cs427app;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {

    // This method initializes the Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("QrSLqfe9kl96pkIbjFOBP20OyIiQ2W9r6K461H1v")
                .clientKey("df4AFre8NLryyQdBbJ4aA4hZgRTrBGUFsa8IoGGz")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
