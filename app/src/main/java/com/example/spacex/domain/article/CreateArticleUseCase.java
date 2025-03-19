package com.example.spacex.domain.article;

import androidx.annotation.NonNull;

import com.example.spacex.domain.entity.Status;

import java.util.function.Consumer;

public class CreateArticleUseCase {
    private final ArticleRepository repo;

    public CreateArticleUseCase(ArticleRepository repo) {this.repo = repo;}

    public void execute(
            @NonNull String title,
            @NonNull String content,
            Consumer<Status<Void>> callback
    ) {

        repo.createArticle(title, content, callback);

    }
}
