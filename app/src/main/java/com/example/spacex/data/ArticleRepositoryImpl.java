package com.example.spacex.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.spacex.data.dto.ArticleDto;
import com.example.spacex.data.network.RetrofitFactory;
import com.example.spacex.data.source.ArticleApi;
import com.example.spacex.data.utils.CallToConsumer;
import com.example.spacex.domain.entity.ArticleEntity;
import com.example.spacex.domain.entity.FullEventEntity;
import com.example.spacex.domain.entity.Status;
import com.example.spacex.domain.article.ArticleRepository;
import com.example.spacex.domain.entity.StatusCode;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ArticleRepositoryImpl implements ArticleRepository {
    private final ArticleApi articleApi;

    public ArticleRepositoryImpl() {

        this.articleApi = RetrofitFactory.getInstance().getArticleApi();
    }

    @Override
    public void getAllArticles(@NonNull String id, Consumer<Status<List<ArticleEntity>>> callback) {
        articleApi.getAllArticles().enqueue(new CallToConsumer<>(
                callback,
                articlesDto -> {

                    if (articlesDto == null) {
                        return new ArrayList<>();
                    }

                    List<ArticleEntity> result = new ArrayList<>(articlesDto.size());
                    for (ArticleDto article : articlesDto) {
                        final String resultId = article.getId();
                        final String title = article.getTitle();
                        final String content = article.getContent();
                        if (resultId != null && title != null && content != null) {
                            result.add(new ArticleEntity(resultId, title, content));
                        }
                    }
                    return result;
                }
        ));
    }

    @Override
    public void createArticle(@NonNull String title, @NonNull String content, Consumer<Status<Void>> callback) {
        ArticleDto newArticle = new ArticleDto(title, content);
        articleApi.createArticle(newArticle).enqueue(new CallToConsumer<>(
                callback,
                response -> null
        ));
    }

    @Override
    public void deleteArticle(@NonNull String id, Consumer<Status<Void>> callback) {
        articleApi.deleteArticle(id).enqueue(new CallToConsumer<>(
                callback,
                response -> null
        ));
    }

    @Override
    public void update(@NonNull String id, @NonNull String title, @NonNull String content, Consumer<Status<Void>> callback) {
        ArticleDto updatedArticle = new ArticleDto(title, content);
        articleApi.update(id, updatedArticle).enqueue(new CallToConsumer<>(
                callback,
                response -> null
        ));
    }
}
