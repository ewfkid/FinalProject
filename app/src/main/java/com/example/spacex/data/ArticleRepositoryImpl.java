package com.example.spacex.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.spacex.data.dto.ArticleDto;
import com.example.spacex.data.dto.ArticleInitDto;
import com.example.spacex.data.dto.CommentDto;
import com.example.spacex.data.network.RetrofitFactory;
import com.example.spacex.data.source.ArticleApi;
import com.example.spacex.data.utils.CallToConsumer;
import com.example.spacex.data.utils.mapper.ArticleMapper;
import com.example.spacex.data.utils.mapper.CommentMapper;
import com.example.spacex.domain.article.ArticleRepository;
import com.example.spacex.domain.entity.CommentEntity;
import com.example.spacex.domain.entity.FullArticleEntity;
import com.example.spacex.domain.entity.ItemArticleEntity;
import com.example.spacex.domain.entity.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ArticleRepositoryImpl implements ArticleRepository {

    private final ArticleApi articleApi = RetrofitFactory.getInstance().getArticleApi();

    private static ArticleRepositoryImpl INSTANCE;

    private ArticleRepositoryImpl() {
    }

    public static synchronized ArticleRepositoryImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ArticleRepositoryImpl();
        }
        return INSTANCE;
    }

    @Override
    public void getAllArticles(Consumer<Status<List<ItemArticleEntity>>> callback) {
        articleApi.getAllArticles().enqueue(new CallToConsumer<>(
                callback,
                ArticleMapper::toItemArticleEntityList
        ));
    }
//    @Override
//    public void getAllArticles(Consumer<Status<List<ItemArticleEntity>>> callback) {
//        articleApi.getAllArticles().enqueue(new Callback<List<ArticleDto>>() {
//            @Override
//            public void onResponse(Call<List<ArticleDto>> call, Response<List<ArticleDto>> response) {
//
//                int statusCode = response.code();
//
//                if (response.isSuccessful()) {
//                    List<ArticleDto> articles = response.body();
//
//                    Log.d("API_Response", "Status Code: " + statusCode + ", Received articles: " + articles);
//
//                    if (articles != null && !articles.isEmpty()) {
//
//                        List<ItemArticleEntity> itemArticles = ArticleMapper.toItemArticleEntityList(articles);
//
//                        Log.d("API_Response", "Mapped ItemArticleEntities: " + itemArticles);
//
//                        callback.accept(new Status<>(statusCode, itemArticles, null));
//                    } else {
//
//                        Log.e("API_Response", "Received empty list");
//                        callback.accept(new Status<>(statusCode, new ArrayList<>(), null));
//                    }
//                } else {
//
//                    Log.e("API_Response", "Error - Status Code: " + statusCode + ", Error: " + response.errorBody());
//
//                    callback.accept(new Status<>(statusCode, null, new Throwable("Error response from server")));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<ArticleDto>> call, Throwable t) {
//
//                Log.e("API_Response", "Request failed: " + t.getMessage());
//
//                callback.accept(new Status<>(-1, null, t));
//            }
//        });
//    }


    @Override
    public void createArticle(
            @NonNull String title,
            @NonNull String content,
            @NonNull String userNickname,
            @Nullable String userAvatar,
            @NonNull Integer likes,
            @NonNull Integer dislikes,
            @Nullable ArrayList<CommentEntity> comments,
            boolean favourite,
            Consumer<Status<Void>> callback
    ) {
        articleApi.createArticle(new ArticleInitDto(
                title,
                content,
                userNickname,
                userAvatar,
                0,
                0,
                new ArrayList<>(),
                false
        )).enqueue(new CallToConsumer<>(
                        callback,
                        dto -> null
                )
        );

    }

    @Override
    public void deleteArticle(@NonNull String id, Consumer<Status<Void>> callback) {
        articleApi.deleteArticle(id).enqueue(new CallToConsumer<>(
                callback,
                dto -> null
        ));
    }

    @Override
    public void update(
            @NonNull String id,
            @NonNull String title,
            @NonNull String content,
            @NonNull String username,
            @Nullable String photoUrl,
            @NonNull Integer likes,
            @NonNull Integer dislikes,
            @Nullable ArrayList<CommentEntity> comments,
            boolean favourite,
            Consumer<Status<FullArticleEntity>> callback
    ) {
        ArrayList<CommentDto> commentsDto = null;
        if (comments != null) {
            commentsDto = new ArrayList<>(CommentMapper.toCommentDtoList(comments));
        }

        articleApi.update(
                id, new ArticleDto(id, title, content, username, photoUrl, likes, dislikes, commentsDto, favourite)
        ).enqueue(new CallToConsumer<>(callback, ArticleMapper::toFullArticleEntity));
    }

    @Override
    public void getArticle(@NonNull String id, Consumer<Status<FullArticleEntity>> callback) {
        articleApi.getArticleById(id).enqueue(new CallToConsumer<>(
                callback,
                ArticleMapper::toFullArticleEntity
        ));
    }

}
