package com.example.spacex.ui.sign.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spacex.data.UserRepositoryImpl;
import com.example.spacex.domain.sign.IsUserExistUseCase;
import com.example.spacex.domain.sign.LoginUserUseCase;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<String> mutableErrorLiveData = new MutableLiveData<>();
    public final LiveData<String> errorLiveData = mutableErrorLiveData;

    private final MutableLiveData<Void> mutableOpenRegisterLiveData = new MutableLiveData<>();
    public final LiveData<Void> openRegisterLiveData = mutableOpenRegisterLiveData;

    private final MutableLiveData<Void> mutableOpenEventsLiveData = new MutableLiveData<>();
    public final LiveData<Void> openEventsLiveData = mutableOpenEventsLiveData;


    /* UseCases */

    private final IsUserExistUseCase isUserExistUseCase = new IsUserExistUseCase(
            UserRepositoryImpl.getInstance()
    );

    private final LoginUserUseCase loginUserUseCase = new LoginUserUseCase(
            UserRepositoryImpl.getInstance()
    );
    /* UseCases */


    @Nullable
    private String username = null;

    @Nullable
    private String password = null;


    public void changeUsername(@NonNull String username) {
        this.username = username;
    }

    public void changePassword(@NonNull String password) {
        this.password = password;
    }

    public void login() {
        final String currentUsername = username;
        final String currentPassword = password;

        if (currentUsername == null || currentUsername.isEmpty()) {
            mutableErrorLiveData.postValue("Никнейм не может быть пустым");
            return;
        }
        if (currentPassword == null || currentPassword.isEmpty()) {
            mutableErrorLiveData.postValue("Пароль не может быть пустым");
            return;
        }

        isUserExistUseCase.execute(currentUsername, status -> {
            if (status.getValue() == null || status.getError() != null) {
                mutableErrorLiveData.postValue("Что-то пошло не так. Попробуйте снова");
                return;
            }
            if (!status.getValue()) {
                mutableErrorLiveData.postValue("Вы не зарегистрированы. Пожалуйста зарегистрируйтесь!");
            } else {
                loginUser(currentUsername, currentPassword);
            }
        });
    }

    private void loginUser(@NonNull final String currentUsername, @NonNull final String currentPassword) {
        loginUserUseCase.execute(currentUsername, currentPassword, status -> {
            if (status.getStatusCode() == 200 && status.getError() == null) {
                mutableOpenEventsLiveData.postValue(null);
            } else {
                mutableErrorLiveData.postValue("Что-то пошло не так. Попробуйте снова");
            }
        });
    }
}
