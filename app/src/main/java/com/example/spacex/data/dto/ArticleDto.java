package com.example.spacex.data.dto;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class ArticleDto {

    @Nullable
    @SerializedName("id")
    public String id;

    @NonNull
    @SerializedName("title")
    public String title;

    @NonNull
    @SerializedName("content")
    public String content;



    public ArticleDto(

            @NonNull String title,
            @NonNull String content

    ) {
        this.title = title;
        this.content = content;

    }



        public String getId(){return id;}
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

    }

