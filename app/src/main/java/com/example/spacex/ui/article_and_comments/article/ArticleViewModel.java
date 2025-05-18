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
        mutableLiveData.setValue(new State(null, null, true, false, false));
        getArticleByIdUseCase.execute(id, status -> {
            FullArticleEntity article = status.getValue();
            mutableLiveData.postValue(new State(
                    status.getError() != null ? status.getError().getLocalizedMessage() : null,
                    article,
                    false,
                    false,
                    false
            ));
        });

    }

    public void like() {
        State currentState = mutableLiveData.getValue();
        if (currentState == null || currentState.getArticle() == null){
            return;
        }

        FullArticleEntity article = currentState.getArticle();
        boolean liked = currentState.isLikedByUser();
        int newLikes = liked ? article.getLikes() - 1 : article.getLikes() + 1;

        updateArticleUseCase.execute(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getUsername(),
                article.getPhotoUrl(),
                Integer.valueOf(newLikes),
                article.getDislikes(),
                article.getComments() == null ? null : new ArrayList<>(article.getComments()),
                article.isFavourite(),
                status -> {
                    FullArticleEntity updatedArticle = status.getValue();
                    if (updatedArticle != null) {
                        mutableLiveData.postValue(
                                new State(null, updatedArticle, false, !liked, currentState.isDislikedByUser())
                        );
                    } else {
                        mutableLiveData.postValue(
                                new State("Failed to like", article, false, liked, currentState.isDislikedByUser())
                        );
                    }
                }
        );
    }

    public void dislike() {
        State currentState = mutableLiveData.getValue();
        if (currentState == null || currentState.getArticle() == null) return;

        FullArticleEntity article = currentState.getArticle();
        boolean disliked = currentState.isDislikedByUser();
        int newDislikes = disliked ? article.getDislikes() - 1 : article.getDislikes() + 1;

        updateArticleUseCase.execute(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getUsername(),
                article.getPhotoUrl(),
                article.getLikes(),
                Integer.valueOf(newDislikes),
                article.getComments() == null ? null : new ArrayList<>(article.getComments()),
                article.isFavourite(),
                status -> {
                    FullArticleEntity updatedArticle = status.getValue();
                    if (updatedArticle != null) {
                        mutableLiveData.postValue(
                                new State(null, updatedArticle, false, currentState.isLikedByUser(), !disliked)
                        );
                    } else {
                        mutableLiveData.postValue(
                                new State("Failed to dislike", article, false, currentState.isLikedByUser(), disliked)
                        );
                    }
                }
        );
    }

    public void addToFavourites() {
        State currentState = mutableLiveData.getValue();
        if (currentState == null || currentState.getArticle() == null) return;

        FullArticleEntity article = currentState.getArticle();
        boolean newFavourite = !article.isFavourite();

        updateArticleUseCase.execute(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getUsername(),
                article.getPhotoUrl(),
                article.getLikes(),
                article.getDislikes(),
                article.getComments() == null ? null : new ArrayList<>(article.getComments()),
                newFavourite,
                status -> {
                    FullArticleEntity updatedArticle = status.getValue();
                    if (updatedArticle != null) {
                        mutableLiveData.postValue(
                                new State(null, updatedArticle, false, currentState.isLikedByUser(), currentState.isDislikedByUser())
                        );
                    } else {
                        mutableLiveData.postValue(
                                new State("Failed add to favourites", article, false, currentState.isLikedByUser(), currentState.isDislikedByUser())
                        );
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

        private final boolean likedByUser;
        private final boolean dislikedByUser;

        public State(@Nullable String errorMessage, @Nullable FullArticleEntity article, boolean isLoading,
                     boolean likedByUser, boolean dislikedByUser) {
            this.errorMessage = errorMessage;
            this.article = article;
            this.isLoading = isLoading;
            this.likedByUser = likedByUser;
            this.dislikedByUser = dislikedByUser;
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

        public boolean isLikedByUser() {
            return likedByUser;
        }

        public boolean isDislikedByUser() {
            return dislikedByUser;
        }
    }
}
