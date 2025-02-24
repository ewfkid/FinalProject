package com.example.spacex.domain.entity;

import androidx.annotation.NonNull;

public class ItemEventEntity {

    @NonNull
    private final String id;

    @NonNull
    private final String title;

    public ItemEventEntity(@NonNull String id, @NonNull String title) {
        this.id = id;
        this.title = title;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }
}
