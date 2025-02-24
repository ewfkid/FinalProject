package com.example.spacex.data.source;

import com.example.spacex.data.dto.AccountDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserApi {

    @GET("/api/person/username/{username}")
    Call<Void> isExist(@Path("username") String username);

    @POST("/api/person/register")
    Call<Void> register(@Body AccountDto dto);

    @GET("/api/person/login")
    Call<Void> login();
}
