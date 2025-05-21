package com.example.spacex.ui.update_profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spacex.data.repository.UserRepositoryImpl;
import com.example.spacex.domain.entity.UserEntity;
import com.example.spacex.domain.user.GetUserByIdUseCase;
import com.example.spacex.domain.user.UpdateUserUseCase;
import com.example.spacex.ui.utils.UserSessionManager;

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

    private final UserSessionManager userSessionManager;

    private UserEntity currentUser;

    private String newName;
    private String newEmail;
    private String newPhone;

    public UpdateProfileViewModel(UserSessionManager userSessionManager) {
        this.userSessionManager = userSessionManager;
    }

    public void load(@NonNull String userId) {
        Log.d("UpdateProfileViewModel", "Loading user with ID: " + userId);
        mutableStateLiveData.postValue(new State(null, null, true));

        getUserByIdUseCase.execute(userId, status -> {
            Log.d("UpdateProfileViewModel", "GetUserByIdUseCase status: " + status.getStatusCode());

            if (status.getStatusCode() == 200 && status.getValue() != null) {
                currentUser = status.getValue();

                Log.d("UpdateProfileViewModel", "Loaded user: "
                        + "id=" + currentUser.getId()
                        + ", name=" + currentUser.getName()
                        + ", username=" + currentUser.getUsername()
                        + ", email=" + currentUser.getEmail()
                        + ", phone=" + currentUser.getPhone()
                        + ", photoUrl=" + currentUser.getPhotoUrl()
                );

                mutableStateLiveData.postValue(new State(null, currentUser, false));
            } else {
                Log.e("UpdateProfileViewModel", "Failed to load user. Status code: " + status.getStatusCode());
                mutableStateLiveData.postValue(new State("Failed to load user", null, false));
            }
        });
    }


    public void changeName(String name) {
        Log.d("UpdateProfileViewModel", "New name input: " + name);
        this.newName = name;
    }

    public void changeEmail(String email) {
        Log.d("UpdateProfileViewModel", "New email input: " + email);
        this.newEmail = email;
    }

    public void changePhone(String phone) {
        Log.d("UpdateProfileViewModel", "New phone input: " + phone);
        this.newPhone = phone;
    }

    public void save(@NonNull String userId) {
        if (currentUser == null) {
            Log.e("UpdateProfileViewModel", "Cannot save: user not loaded");
            mutableStateLiveData.postValue(new State("Cannot save: user not loaded", null, false));
            return;
        }

        String finalName = (newName == null || newName.isEmpty()) ? currentUser.getName() : newName;
        String finalEmail = (newEmail == null || newEmail.isEmpty()) ? currentUser.getEmail() : newEmail;
        String finalPhone = (newPhone == null || newPhone.isEmpty()) ? currentUser.getPhone() : newPhone;
        String finalUsername = currentUser.getUsername(); // Ник нельзя менять

        if (finalUsername == null || finalUsername.isEmpty()) {
            Log.e("UpdateProfileViewModel", "Error: Username is null or empty before saving.");
            mutableStateLiveData.postValue(new State("Ошибка: имя пользователя не загружено", null, false));
            return;
        }

        Log.d("UpdateProfileViewModel", "Saving user:"
                + " id=" + userId
                + ", name=" + finalName
                + ", username=" + finalUsername
                + ", email=" + finalEmail
                + ", phone=" + finalPhone
                + ", photoUrl=" + currentUser.getPhotoUrl());

        updateUser(
                userId,
                finalName,
                finalUsername,
                finalEmail,
                finalPhone,
                currentUser.getPhotoUrl()
        );
    }

    private void updateUser(String userId, String name, String username, String email, String phone, String photoUrl) {
        updateUserUseCase.execute(
                userId,
                name,
                username,
                email,
                phone,
                photoUrl,
                status -> {
                    if (status.getStatusCode() == 200) {
                        Log.d("UpdateProfileViewModel", "User successfully updated on server");

                        UserEntity updatedUser = new UserEntity(
                                userId,
                                phone,
                                name,
                                username,
                                email,
                                photoUrl
                        );

                        userSessionManager.saveUser(updatedUser);
                        currentUser = updatedUser;

                        Log.d("UpdateProfileViewModel", "Updated user object:"
                                + " id=" + updatedUser.getId()
                                + ", name=" + updatedUser.getName()
                                + ", username=" + updatedUser.getUsername()
                                + ", email=" + updatedUser.getEmail()
                                + ", phone=" + updatedUser.getPhone()
                                + ", photoUrl=" + updatedUser.getPhotoUrl());

                        mutableStateLiveData.postValue(new State(null, updatedUser, false));
                    } else {
                        Log.e("UpdateProfileViewModel", "Failed to save user. Status code: " + status.getStatusCode());
                        mutableStateLiveData.postValue(new State("Failed to save. Try again later", null, false));
                    }
                }
        );
    }


    public static class State {

        @Nullable private final String errorMessage;
        @Nullable private final UserEntity user;
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
