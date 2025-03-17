package com.example.spacex.domain.entity;

import androidx.annotation.NonNull;

public class ArticleEntity {
    @NonNull
    private final String id;

    @NonNull
    private final String title;

    @NonNull
    private final String content;

    public ArticleEntity(@NonNull String id, @NonNull String title, @NonNull String content) {
        this.id = id;
        this.title = title;
        this.content = content;

    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public String getContent() {
        return content;
    }

}
