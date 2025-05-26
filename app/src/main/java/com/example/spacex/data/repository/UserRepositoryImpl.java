package com.example.spacex.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.spacex.data.dto.AccountDto;
import com.example.spacex.data.dto.UpdatedUserDto;
import com.example.spacex.data.network.RetrofitFactory;
import com.example.spacex.data.source.CredentialsDataSource;
import com.example.spacex.data.source.UserApi;
import com.example.spacex.data.utils.CallToConsumer;
import com.example.spacex.data.utils.mapper.UserMapper;
import com.example.spacex.domain.entity.Status;
import com.example.spacex.domain.entity.UserEntity;
import com.example.spacex.domain.sign.SignRepository;
import com.example.spacex.domain.user.UserRepository;

import java.io.IOException;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepositoryImpl implements SignRepository, UserRepository {

    private static UserRepositoryImpl INSTANCE;

    private UserApi userApi = RetrofitFactory.getInstance().getUserApi();

    private final CredentialsDataSource credentialsDataSource = CredentialsDataSource.getInstance();

    private UserRepositoryImpl() {
    }

    public static synchronized UserRepositoryImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserRepositoryImpl();
        }
        return INSTANCE;
    }


    @Override
    public void isUserExist(@NonNull String username, Consumer<Status<Void>> callback) {
        userApi.isExist(username).enqueue(new CallToConsumer<>(
                callback,
                dto -> null
        ));
    }

    @Override
    public void createAccount(
            @NonNull String name,
            @NonNull String username,
            @NonNull String email,
            @NonNull String password,
            Consumer<Status<Void>> callback
    ) {
        userApi.register(new AccountDto(name, username, email, password)).enqueue(new CallToConsumer<>(
                callback,
                dto -> null
        ));
    }

    @Override
    public void login(@NonNull String username, @NonNull String password, Consumer<Status<UserEntity>> callback) {
        credentialsDataSource.updateLogin(username, password);
        userApi = RetrofitFactory.getInstance().getUserApi();
        userApi.login().enqueue(new CallToConsumer<>(
                callback,
                UserMapper::toUserEntity
        ));
    }

    @Override
    public void logout() {
        credentialsDataSource.logout();
    }

    @Override
    public void getUser(@NonNull String id, Consumer<Status<UserEntity>> callback) {
        userApi.getUserById(id).enqueue(new CallToConsumer<>(
                callback,
                UserMapper::toUserEntity
        ));
    }

    @Override
    public void updateUser(@NonNull String id, @NonNull String name, @NonNull String username,
                           @NonNull String email, @Nullable String phone, @Nullable String photoUrl,
                           Consumer<Status<Void>> callback) {

        UpdatedUserDto updatedUserDto = new UpdatedUserDto(id, name, username, photoUrl, phone, email);

        Log.d("UserRepositoryImpl", "Updating user with ID: " + id);
        Log.d("UserRepositoryImpl", "Name: " + name);
        Log.d("UserRepositoryImpl", "Username: " + username);
        Log.d("UserRepositoryImpl", "Email: " + email);
        Log.d("UserRepositoryImpl", "Phone: " + (phone != null ? phone : "null"));
        Log.d("UserRepositoryImpl", "PhotoUrl: " + (photoUrl != null ? photoUrl : "null"));
        Log.d("UserRepositoryImpl", "Request payload: " + updatedUserDto.toString());

        userApi.updateUserById(id, updatedUserDto)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d("UserRepositoryImpl", "User update successful for ID: " + id);
                            callback.accept(new Status<>(200, null, null));
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "empty error body";
                                Log.e("UserRepositoryImpl", "Update user failed. Code: " + response.code() + ", Error: " + errorBody);
                            } catch (IOException e) {
                                Log.e("UserRepositoryImpl", "Failed to read error body", e);
                            }
                            callback.accept(new Status<>(response.code(), null, null));
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("UserRepositoryImpl", "Update user request failed: " + t.getMessage(), t);
                        callback.accept(new Status<>(-1, null, t));
                    }
                });
    }

//    @Override
//    public void updateUser(
//            @NonNull String id,
//            @NonNull String name,
//            @NonNull String username,
//            @NonNull String email,
//            @Nullable String phone,
//            @Nullable String photoUrl,
//            Consumer<Status<Void>> callback
//    ) {
//        userApi.updateUserById(id, new UpdatedUserDto(id, name, username, photoUrl, phone, email)).enqueue(new CallToConsumer<>(
//                callback,
//                dto -> null
//        ));
//    }
}
