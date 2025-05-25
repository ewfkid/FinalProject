package com.example.spacex.ui.article_and_comments.comments_list.factory;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.spacex.ui.article_and_comments.comments_list.CommentListViewModel;

public class CommentListViewModelFactory implements ViewModelProvider.Factory {

    private final String articleId;
    private final SharedPreferences sharedPreferences;

    public CommentListViewModelFactory(String articleId, SharedPreferences sharedPreferences) {
        this.articleId = articleId;
        this.sharedPreferences = sharedPreferences;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CommentListViewModel.class)) {
            return (T) new CommentListViewModel(articleId, sharedPreferences);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

