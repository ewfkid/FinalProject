package com.example.spacex.ui.utils.config;

import android.app.Application;

import com.parse.Parse;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("L43NeMG70Avs4YPy6puzEGYt0ShdBVJGyASvVQGz")
                .clientKey("f3rzCtnUu2RzmKxAUKL6ZsVJWL0dlVncVZvcvfOe")
                .server("https://parseapi.back4app.com/")
                .build()
        );
    }
}

