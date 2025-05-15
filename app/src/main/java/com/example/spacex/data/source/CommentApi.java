package com.example.spacex.data.source;

import com.example.spacex.data.dto.CommentDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CommentApi {

    @GET("api/comment/article/{articleId}")
    Call<List<CommentDto>> getAllComments(@Path("articleId") String articleId);
}
