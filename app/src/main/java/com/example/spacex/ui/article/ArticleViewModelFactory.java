package com.example.spacex.ui.article;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.spacex.domain.article.ArticleRepository;
import com.example.spacex.domain.article.CreateArticleUseCase;
import com.example.spacex.domain.article.DeleteArticleUseCase;
import com.example.spacex.domain.article.GetArticleListUseCase;
import com.example.spacex.domain.article.UpdateArticleUseCase;

public class ArticleViewModelFactory implements ViewModelProvider.Factory {
    private final CreateArticleUseCase createArticleUseCase;
    private final DeleteArticleUseCase deleteArticleUseCase;
    private final UpdateArticleUseCase updateArticleUseCase;
    private final GetArticleListUseCase getArticleListUseCase;

    public ArticleViewModelFactory(
            CreateArticleUseCase createArticleUseCase,
            DeleteArticleUseCase deleteArticleUseCase,
            UpdateArticleUseCase updateArticleUseCase,
            GetArticleListUseCase getArticleListUseCase
    ) {
        this.createArticleUseCase = createArticleUseCase;
        this.deleteArticleUseCase = deleteArticleUseCase;
        this.updateArticleUseCase = updateArticleUseCase;
        this.getArticleListUseCase = getArticleListUseCase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ArticleViewModel.class)) {
            return (T) new ArticleViewModel(
                    createArticleUseCase,
                    deleteArticleUseCase,
                    updateArticleUseCase,
                    getArticleListUseCase
            );
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}