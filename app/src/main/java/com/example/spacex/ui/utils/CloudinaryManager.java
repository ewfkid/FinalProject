package com.example.spacex.ui.utils;

import android.content.Context;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class CloudinaryManager {

    private static boolean isInitialized = false;

    public static void init(Context context) {
        if (isInitialized) return;

        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", "dyp8ikyqu");
        config.put("api_key", "141177131558868");
        config.put("api_secret", "yi-CT6D5awMVZWGQ1IZUmXzweOY");

        MediaManager.init(context.getApplicationContext(), config);
        isInitialized = true;
    }
}
