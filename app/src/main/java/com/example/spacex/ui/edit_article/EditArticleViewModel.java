package com.example.spacex.ui.edit_article;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spacex.data.repository.ArticleRepositoryImpl;
import com.example.spacex.domain.article.GetArticleByIdUseCase;
import com.example.spacex.domain.article.UpdateArticleUseCase;
import com.example.spacex.domain.entity.FullArticleEntity;

public class EditArticleViewModel extends ViewModel {

    private final MutableLiveData<State> mutableStateLiveData = new MutableLiveData<>();
    public final LiveData<State> stateLiveData = mutableStateLiveData;

    private final MutableLiveData<Void> mutableOpenArticleLiveData = new MutableLiveData<>();
    public final LiveData<Void> openProfileLiveData = mutableOpenArticleLiveData;

    private String newTitle;

    private String newContent;

    private FullArticleEntity currentArticle;


    //* UseCases *//
    GetArticleByIdUseCase getArticleByIdUseCase = new GetArticleByIdUseCase(
            ArticleRepositoryImpl.getInstance()
    );

    UpdateArticleUseCase updateArticleUseCase = new UpdateArticleUseCase(
            ArticleRepositoryImpl.getInstance()
    );
    //* UseCases *//

    public void changeTitle(String title) {
        this.newTitle = title;
    }

    public void changeContent(String content) {
        this.newContent = content;
    }

    public void load(@NonNull String articleId) {
        mutableStateLiveData.postValue(new State(null, null, true));
        getArticleByIdUseCase.execute(articleId, status -> {
            if (status.getStatusCode() == 200 && status.getValue() != null) {
                currentArticle = status.getValue();
                mutableStateLiveData.postValue(new State(null, currentArticle, false));
            } else {
                mutableStateLiveData.postValue(new State("Failed to load user", null, false));
            }
        });
    }

    public void save(@NonNull String articleId) {
        if (currentArticle == null) {
            mutableStateLiveData.postValue(new State("Cannot save: article not loaded", null, false));
            return;
        }
        String updatedTitle = (newTitle != null && !newTitle.isEmpty()) ? newTitle : currentArticle.getTitle();
        String updatedContent = (newContent != null && !newContent.isEmpty()) ? newContent : currentArticle.getContent();

        mutableStateLiveData.postValue(new State(null, currentArticle, true));

//        updateArticleUseCase.execute(
//                currentArticle.getId(),
//                updatedTitle,
//                updatedContent,
//                currentArticle.getUsername(),
//                currentArticle.getPhotoUrl(),
//                currentArticle.getLikes(),
//                currentArticle.getDislikes(),
//                currentArticle.getComments(),
//                currentArticle.isFavourite(),
//                status -> {
//                    if (status.getError() == null && status.getValue() != null) {
//                        mutableOpenArticleLiveData.postValue(null);
//                        mutableStateLiveData.postValue(new State("Failed to update article", currentArticle, false));
//                    }
//                }
//        );
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
