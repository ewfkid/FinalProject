package com.example.spacex.ui.article_and_comments.comments_list.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.spacex.ui.article_and_comments.comments_list.CommentListViewModel;

public class CommentListViewModelFactory implements ViewModelProvider.Factory {

    private final String articleId;

    public CommentListViewModelFactory(String articleId) {
        this.articleId = articleId;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CommentListViewModel.class)) {
            return (T) new CommentListViewModel(articleId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
