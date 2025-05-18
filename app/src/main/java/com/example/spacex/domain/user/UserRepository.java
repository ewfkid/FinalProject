package com.example.spacex.domain.user;

import androidx.annotation.NonNull;

import com.example.spacex.domain.entity.Status;
import com.example.spacex.domain.entity.UserEntity;

import java.util.function.Consumer;

public interface UserRepository {

    public void getUser(@NonNull String id, Consumer<Status<UserEntity>> callback);
}
