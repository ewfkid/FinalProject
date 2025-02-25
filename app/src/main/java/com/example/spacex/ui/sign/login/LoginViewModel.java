package com.example.spacex.ui.sign.login;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.spacex.data.UserRepositoryImpl;
import com.example.spacex.domain.sign.IsUserExistUseCase;
import com.example.spacex.domain.sign.LoginUserUseCase;

public class LoginViewModel extends AndroidViewModel {

    private final MutableLiveData<String> mutableErrorLiveData = new MutableLiveData<>();
    public final LiveData<String> errorLiveData = mutableErrorLiveData;

    private final MutableLiveData<Void> mutableOpenRegisterLiveData = new MutableLiveData<>();
    public final LiveData<Void> openRegisterLiveData = mutableOpenRegisterLiveData;

    private final MutableLiveData<Boolean> mutableOpenEventsLiveData = new MutableLiveData<>();
    public final LiveData<Boolean> openEventsLiveData = mutableOpenEventsLiveData;

    /* UseCases */

    private final IsUserExistUseCase isUserExistUseCase = new IsUserExistUseCase(
            UserRepositoryImpl.getInstance()
    );

    private final LoginUserUseCase loginUserUseCase = new LoginUserUseCase(
            UserRepositoryImpl.getInstance()
    );

    /* UseCases */


    private String username;

    private String password;

    private final SharedPreferences sharedPreferences;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        sharedPreferences = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
    }

    public void changeUsername(@NonNull String username) {
        this.username = username;
    }

    public void changePassword(@NonNull String password) {
        this.password = password;
    }

    public void login() {
        if (username == null || username.isEmpty()) {
            mutableErrorLiveData.postValue("Никнейм не может быть пустым");
            return;
        }
        if (password == null || password.isEmpty()) {
            mutableErrorLiveData.postValue("Пароль не может быть пустым");
            return;
        }

        isUserExistUseCase.execute(username, status -> {
            if (status.getValue() == null || status.getError() != null) {
                mutableErrorLiveData.postValue("Ошибка входа. Попробуйте снова.");
                return;
            }
            if (!status.getValue()) {
                mutableErrorLiveData.postValue("Вы не зарегистрированы. Пожалуйста, зарегистрируйтесь!");
            } else {
                loginUser(username, password);
            }
        });
    }

    private void loginUser(@NonNull final String currentUsername, @NonNull final String currentPassword) {
        loginUserUseCase.execute(currentUsername, currentPassword, status -> {
            if (status.getStatusCode() == 200 && status.getError() == null) {
                saveUserData(currentUsername, currentPassword);
                saveUserLoggedIn(true); // Помечаем, что пользователь вошел
                mutableOpenEventsLiveData.postValue(true);
            } else {
                mutableErrorLiveData.postValue("Ошибка входа. Попробуйте снова.");
            }
        });
    }

    private void saveUserData(String username, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }

    private void saveUserLoggedIn(boolean isLoggedIn) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply();
    }

    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }
}