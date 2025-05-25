package com.example.spacex.ui.utils;

import android.content.Context;
import android.net.Uri;

import com.parse.ParseFile;

import java.io.IOException;
import java.io.InputStream;

public class ImageUploader {

//    public interface UploadCallback {
//        void onSuccess(String url);
//        void onError(String message);
//    }
//
//    public static void uploadImageToBack4App(Context context, Uri imageUri, UploadCallback callback) {
//        try {
//            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
//            byte[] imageData = new byte[inputStream.available()];
//            inputStream.read(imageData);
//            inputStream.close();
//
//            ParseFile file = new ParseFile("avatar.png", imageData);
//            file.saveInBackground(e -> {
//                if (e == null) {
//                    callback.onSuccess(file.getUrl());
//                } else {
//                    callback.onError("Upload failed: " + e.getMessage());
//                }
//            });
//
//        } catch (IOException e) {
//            callback.onError("IO Error: " + e.getMessage());
//        }
//    }
}
