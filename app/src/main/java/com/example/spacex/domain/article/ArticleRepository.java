package com.example.spacex.domain.article;

import androidx.annotation.NonNull;

import com.example.spacex.domain.entity.ArticleEntity;
import com.example.spacex.domain.entity.Status;

import java.util.List;
import java.util.function.Consumer;

public interface ArticleRepository {

    void getAllArticles(@NonNull String id, Consumer<Status<List<ArticleEntity>>> callback);
    void createArticle(@NonNull String title, @NonNull String content, Consumer<Status<Void>> callback);
    void deleteArticle(@NonNull String id, Consumer<Status<Void>> callback);
    void update(@NonNull String id, @NonNull String title, @NonNull String content, Consumer<Status<Void>> callback);
}
