package com.example.spacex.ui.update_profile;


import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.spacex.data.repository.UserRepositoryImpl;
import com.example.spacex.domain.entity.UserEntity;
import com.example.spacex.domain.user.GetUserByIdUseCase;
import com.example.spacex.domain.user.UpdateUserUseCase;
import com.example.spacex.ui.utils.CloudinaryConfig;
import com.example.spacex.ui.utils.UserSessionManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;


public class UpdateProfileViewModel extends ViewModel {

    //* UseCases *//
    private final UpdateUserUseCase updateUserUseCase = new UpdateUserUseCase(
            UserRepositoryImpl.getInstance()
    );
    private final GetUserByIdUseCase getUserByIdUseCase = new GetUserByIdUseCase(
            UserRepositoryImpl.getInstance()
    );
    //* UseCases *//

    private final MutableLiveData<State> mutableStateLiveData = new MutableLiveData<>();
    public final LiveData<State> stateLiveData = mutableStateLiveData;

    private final MutableLiveData<Void> mutableOpenProfileLiveData = new MutableLiveData<>();
    public final LiveData<Void> openProfileLiveData = mutableOpenProfileLiveData;

    private final UserSessionManager userSessionManager;

    private UserEntity currentUser;

    private String newName;
    private String newEmail;
    private String newPhone;
    private String newPhotoUrl = null;

    public UpdateProfileViewModel(UserSessionManager userSessionManager) {
        this.userSessionManager = userSessionManager;
    }

    public void load(@NonNull String userId) {
        Log.d("UpdateProfileVM", "Loading user with ID: " + userId);
        mutableStateLiveData.postValue(new State(null, null, true));

        getUserByIdUseCase.execute(userId, status -> {
            if (status.getStatusCode() == 200 && status.getValue() != null) {
                currentUser = status.getValue();
                Log.d("UpdateProfileVM", "User loaded: " + currentUser.getName() + ", photoUrl: " + currentUser.getPhotoUrl());

                mutableStateLiveData.postValue(new State(null, currentUser, false));
            } else {
                Log.e("UpdateProfileVM", "Failed to load user with ID: " + userId + ". Status code: " + status.getStatusCode());
                mutableStateLiveData.postValue(new State("Failed to load user", null, false));
            }
        });
    }

    public void changeName(String name) {
        Log.d("UpdateProfileVM", "Name changed to: " + name);
        this.newName = name;
    }

    public void changeEmail(String email) {
        Log.d("UpdateProfileVM", "Email changed to: " + email);
        this.newEmail = email;
    }

    public void changePhone(String phone) {
        Log.d("UpdateProfileVM", "Phone changed to: " + phone);
        this.newPhone = phone;
    }

    public void save(@NonNull String userId) {
        Log.d("UpdateProfileVM", "Save button clicked for user: " + userId);
        if (currentUser == null) {
            Log.e("UpdateProfileVM", "Cannot save: currentUser is null");
            mutableStateLiveData.postValue(new State("Cannot save: user not loaded", null, false));
            return;
        }

        performUserUpdate(currentUser.getPhotoUrl());
    }

    private void performUserUpdate(String existingPhotoUrl) {
        if (currentUser == null) {
            Log.e("UpdateProfileVM", "Cannot update: currentUser is null");
            mutableStateLiveData.postValue(new State("Cannot update: user not loaded", null, false));
            return;
        }

        String finalName = (newName == null || newName.isEmpty()) ? currentUser.getName() : newName;
        String finalEmail = (newEmail == null || newEmail.isEmpty()) ? currentUser.getEmail() : newEmail;
        String finalPhone = (newPhone == null || newPhone.isEmpty()) ? currentUser.getPhone() : newPhone;
        String finalUsername = currentUser.getUsername();

        String finalPhotoUrl = (newPhotoUrl != null) ? newPhotoUrl : existingPhotoUrl;

        Log.d("UpdateProfileVM", "Updating user with data:");
        Log.d("UpdateProfileVM", "ID: " + currentUser.getId());
        Log.d("UpdateProfileVM", "Name: " + finalName);
        Log.d("UpdateProfileVM", "Username: " + finalUsername);
        Log.d("UpdateProfileVM", "Email: " + finalEmail);
        Log.d("UpdateProfileVM", "Phone: " + finalPhone);
        Log.d("UpdateProfileVM", "PhotoUrl: " + finalPhotoUrl);

        if (finalUsername == null || finalUsername.isEmpty()) {
            Log.e("UpdateProfileVM", "Username is null or empty before update");
            mutableStateLiveData.postValue(new State("Username is null or empty before update", null, false));
            return;
        }

        updateUserUseCase.execute(
                currentUser.getId(),
                finalName,
                finalUsername,
                finalEmail,
                finalPhone,
                finalPhotoUrl,
                status -> {
                    if (status.getStatusCode() == 200) {
                        UserEntity updatedUser = new UserEntity(
                                currentUser.getId(),
                                finalPhone,
                                finalName,
                                finalUsername,
                                finalEmail,
                                finalPhotoUrl
                        );
                        Log.d("UpdateProfileVM", "User update successful for ID: " + currentUser.getId() + ", photoUrl: " + finalPhotoUrl);
                        userSessionManager.saveUser(updatedUser);
                        currentUser = updatedUser;
                        newPhotoUrl = null;

                        mutableOpenProfileLiveData.postValue(null);
                    } else if (status.getStatusCode() == 500) {
                        Log.e("UpdateProfileVM", "Update failed: Email or phone already used");
                        mutableStateLiveData.postValue(new State("Email or phone is already used", null, false));
                    } else {
                        Log.e("UpdateProfileVM", "Update failed with status code: " + status.getStatusCode());
                        mutableStateLiveData.postValue(new State("Failed to save. Try again later", null, false));
                    }
                }
        );

    }

    public void uploadAvatar(Uri croppedImageUri, ContentResolver contentResolver) {
        Log.d("UpdateProfileVM", "Uploading avatar...");
        mutableStateLiveData.postValue(new State(null, currentUser, true)); // loading = true

        new Thread(() -> {
            try {
                InputStream inputStream = contentResolver.openInputStream(croppedImageUri);
                if (inputStream == null) {
                    Log.e("UpdateProfileVM", "Failed to open image stream");
                    postError("Failed to open image stream");
                    return;
                }

                byte[] imageData = getBytes(inputStream);
                Log.d("UpdateProfileVM", "Image data size: " + imageData.length + " bytes");

                Cloudinary cloudinary = CloudinaryConfig.getInstance();

                Map uploadResult = cloudinary.uploader().upload(imageData, ObjectUtils.emptyMap());
                String imageUrl = (String) uploadResult.get("secure_url");

                if (imageUrl == null || imageUrl.isEmpty()) {
                    Log.e("UpdateProfileVM", "Failed to upload image to Cloudinary: empty URL");
                    postError("Failed to upload image to Cloudinary");
                    return;
                }

                Log.d("UpdateProfileVM", "Cloudinary upload successful: " + imageUrl);

                newPhotoUrl = imageUrl;

                if (currentUser != null) {
                    UserEntity updatedUser = new UserEntity(
                            currentUser.getId(),
                            currentUser.getPhone(),
                            currentUser.getName(),
                            currentUser.getUsername(),
                            currentUser.getEmail(),
                            imageUrl
                    );

                    currentUser = updatedUser;

                    mutableStateLiveData.postValue(new State(null, updatedUser, false)); // loading = false
                }

            } catch (Exception e) {
                Log.e("UpdateProfileVM", "Failed to upload avatar", e);
                postError("Failed to upload avatar: " + e.getMessage());
            }
        }).start();
    }


    private void postError(String message) {
        Log.e("UpdateProfileVM", "Error: " + message);
        mutableStateLiveData.postValue(new State(message, currentUser, false));
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    public static class State {

        @Nullable
        private final String errorMessage;
        @Nullable
        private final UserEntity user;
        private final boolean isLoading;

        public State(@Nullable String errorMessage, @Nullable UserEntity user, boolean isLoading) {
            this.errorMessage = errorMessage;
            this.user = user;
            this.isLoading = isLoading;
        }

        @Nullable
        public String getErrorMessage() {
            return errorMessage;
        }

        @Nullable
        public UserEntity getUser() {
            return user;
        }

        public boolean isLoading() {
            return isLoading;
        }
    }
}
