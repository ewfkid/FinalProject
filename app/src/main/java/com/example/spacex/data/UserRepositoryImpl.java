package com.example.spacex.data;

import androidx.annotation.NonNull;

import com.example.spacex.data.dto.AccountDto;
import com.example.spacex.data.network.RetrofitFactory;
import com.example.spacex.data.source.CredentialsDataSource;
import com.example.spacex.data.source.UserApi;
import com.example.spacex.data.utils.CallToConsumer;
import com.example.spacex.domain.entity.Status;
import com.example.spacex.domain.sign.SignRepository;

import java.util.function.Consumer;

public class UserRepositoryImpl implements SignRepository {

    private static UserRepositoryImpl INSTANCE;

    private UserApi userApi = RetrofitFactory.getInstance().getUserApi();

    private final CredentialsDataSource credentialsDataSource = CredentialsDataSource.getInstance();

    private UserRepositoryImpl() {}

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
    public void login(@NonNull String username, @NonNull String password, Consumer<Status<Void>> callback) {
        credentialsDataSource.updateLogin(username, password);
        userApi = RetrofitFactory.getInstance().getUserApi();
        userApi.login().enqueue(new CallToConsumer<>(
                callback,
                dto -> null
        ));
    }

    @Override
    public void logout() {
        credentialsDataSource.logout();
    }
}
