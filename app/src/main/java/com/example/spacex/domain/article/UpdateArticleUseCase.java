package com.example.spacex.domain.article;

import androidx.annotation.NonNull;

import com.example.spacex.domain.entity.Status;

import java.util.function.Consumer;

public class UpdateArticleUseCase {
    private final ArticleRepository repo;

    public UpdateArticleUseCase(ArticleRepository repo) {
        this.repo = repo;
    }

    public void execute(
            @NonNull String id,
            @NonNull String title,
            @NonNull String content,
            Consumer<Status<Void>> callback
    ) {
        repo.update(id, title, content, callback);
    }
}
