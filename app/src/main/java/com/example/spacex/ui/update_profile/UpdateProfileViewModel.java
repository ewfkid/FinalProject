package com.example.spacex.ui.update_profile;


import android.content.ContentResolver;
import android.net.Uri;

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

    public UpdateProfileViewModel(UserSessionManager userSessionManager) {
        this.userSessionManager = userSessionManager;
    }

    public void load(@NonNull String userId) {
        mutableStateLiveData.postValue(new State(null, null, true));

        getUserByIdUseCase.execute(userId, status -> {

            if (status.getStatusCode() == 200 && status.getValue() != null) {
                currentUser = status.getValue();

                mutableStateLiveData.postValue(new State(null, currentUser, false));
            } else {
                mutableStateLiveData.postValue(new State("Failed to load user", null, false));
            }
        });
    }


    public void changeName(String name) {
        this.newName = name;
    }

    public void changeEmail(String email) {
        this.newEmail = email;
    }

    public void changePhone(String phone) {
        this.newPhone = phone;
    }

    public void save(@NonNull String userId) {
        if (currentUser == null) {
            mutableStateLiveData.postValue(new State("Cannot save: user not loaded", null, false));
            return;
        }

        performUserUpdate(currentUser.getPhotoUrl());
    }


    private void performUserUpdate(String photoUrl) {
        if (currentUser == null) {
            mutableStateLiveData.postValue(new State("Cannot update: user not loaded", null, false));
            return;
        }

        String finalName = (newName == null || newName.isEmpty()) ? currentUser.getName() : newName;
        String finalEmail = (newEmail == null || newEmail.isEmpty()) ? currentUser.getEmail() : newEmail;
        String finalPhone = (newPhone == null || newPhone.isEmpty()) ? currentUser.getPhone() : newPhone;
        String finalUsername = currentUser.getUsername();

        if (finalUsername == null || finalUsername.isEmpty()) {
            mutableStateLiveData.postValue(new State("Username is null or empty before update", null, false));
            return;
        }

        updateUserUseCase.execute(
                currentUser.getId(),
                finalName,
                finalUsername,
                finalEmail,
                finalPhone,
                photoUrl,
                status -> {
                    if (status.getStatusCode() == 200) {

                        UserEntity updatedUser = new UserEntity(
                                currentUser.getId(),
                                finalPhone,
                                finalName,
                                finalUsername,
                                finalEmail,
                                photoUrl
                        );

                        userSessionManager.saveUser(updatedUser);
                        currentUser = updatedUser;

                        mutableOpenProfileLiveData.postValue(null);
                    } else if (status.getStatusCode() == 500) {
                        mutableStateLiveData.postValue(new State("Email or phone is already used", null, false));
                    } else {
                        mutableStateLiveData.postValue(new State("Failed to save. Try again later", null, false));
                    }
                }
        );
    }

    public void uploadAvatar(Uri croppedImageUri, ContentResolver contentResolver) {
        new Thread(() -> {
            try {
                InputStream inputStream = contentResolver.openInputStream(croppedImageUri);
                if (inputStream == null) return;
                byte[] imageData = getBytes(inputStream);
                Cloudinary cloudinary = CloudinaryConfig.getInstance();
                Map uploadResult = cloudinary.uploader().upload(imageData, ObjectUtils.emptyMap());
                String imageUrl = (String) uploadResult.get("secure_url");
                updateAvatarOnServer(imageUrl);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
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

    private void updateAvatarOnServer(String imageUrl) {
        performUserUpdate(imageUrl);
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