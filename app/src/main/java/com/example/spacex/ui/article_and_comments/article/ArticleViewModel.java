package com.example.spacex.ui.article_and_comments.article;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spacex.data.repository.ArticleRepositoryImpl;
import com.example.spacex.domain.article.GetArticleByIdUseCase;
import com.example.spacex.domain.article.UpdateArticleUseCase;
import com.example.spacex.domain.entity.FullArticleEntity;
import com.example.spacex.domain.entity.Status;

import java.util.ArrayList;

public class ArticleViewModel extends ViewModel {

    private final MutableLiveData<State> mutableLiveData = new MutableLiveData<>();
    public final LiveData<State> stateLiveData = mutableLiveData;

    private final GetArticleByIdUseCase getArticleByIdUseCase = new GetArticleByIdUseCase(
            ArticleRepositoryImpl.getInstance()
    );

    private final UpdateArticleUseCase updateArticleUseCase = new UpdateArticleUseCase(
            ArticleRepositoryImpl.getInstance()
    );

    public void load(@NonNull String id) {
        mutableLiveData.setValue(new State(null, null, true));
        getArticleByIdUseCase.execute(id, status -> {
            mutableLiveData.postValue(fromStatus(status));
        });
    }

    private State fromStatus(Status<FullArticleEntity> status) {
        return new State(
                status.getError() != null ? status.getError().getLocalizedMessage() : null,
                status.getValue(),
                false
        );
    }

    public void like() {
        State currentState = mutableLiveData.getValue();
        FullArticleEntity article = currentState != null ? currentState.getArticle() : null;
        if (article == null) return;

        updateArticleUseCase.execute(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getUsername(),
                article.getPhotoUrl(),
                article.getLikes() + 1,
                article.getDislikes(),
                article.getComments() == null ? null : new ArrayList<>(article.getComments()),
                article.isFavourite(),
                status -> {
                    if (status.getError() == null) {
                        mutableLiveData.postValue(fromStatus(status));
                    }
                }
        );
    }

    public void dislike() {
        State currentState = mutableLiveData.getValue();
        FullArticleEntity article = currentState != null ? currentState.getArticle() : null;
        if (article == null) return;

        updateArticleUseCase.execute(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getUsername(),
                article.getPhotoUrl(),
                article.getLikes(),
                article.getDislikes() + 1,
                article.getComments() == null ? null : new ArrayList<>(article.getComments()),
                article.isFavourite(),
                status -> {
                    if (status.getError() == null) {
                        mutableLiveData.postValue(fromStatus(status));
                    }
                }
        );
    }

    public void addToFavourites() {
        State currentState = mutableLiveData.getValue();
        FullArticleEntity article = currentState != null ? currentState.getArticle() : null;
        if (article == null) return;

        updateArticleUseCase.execute(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getUsername(),
                article.getPhotoUrl(),
                article.getLikes(),
                article.getDislikes(),
                article.getComments() == null ? null : new ArrayList<>(article.getComments()),
                true,
                status -> {
                    if (status.getError() == null) {
                        mutableLiveData.postValue(fromStatus(status));
                    }
                }
        );
    }

    public static class State {
        @Nullable
        private final String errorMessage;
        @Nullable
        private final FullArticleEntity article;
        private final boolean isLoading;

        public State(@Nullable String errorMessage, @Nullable FullArticleEntity article, boolean isLoading) {
            this.errorMessage = errorMessage;
            this.article = article;
            this.isLoading = isLoading;
        }

        @Nullable
        public String getErrorMessage() {
            return errorMessage;
        }

        @Nullable
        public FullArticleEntity getArticle() {
            return article;
        }

        public boolean isLoading() {
            return isLoading;
        }
    }
}