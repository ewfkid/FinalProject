package com.example.spacex.ui.article;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spacex.domain.article.CreateArticleUseCase;
import com.example.spacex.domain.article.DeleteArticleUseCase;
import com.example.spacex.domain.article.GetArticleListUseCase;
import com.example.spacex.domain.article.UpdateArticleUseCase;
import com.example.spacex.domain.entity.ArticleEntity;
import com.example.spacex.domain.article.ArticleRepository;
import com.example.spacex.domain.entity.Status;
import com.example.spacex.domain.entity.StatusCode;

import java.util.ArrayList;
import java.util.List;

public class ArticleViewModel extends ViewModel {
    private final MutableLiveData<List<ArticleEntity>> articlesLiveData;
    private final MutableLiveData<Status<List<ArticleEntity>>> statusLiveData;
    private final CreateArticleUseCase createArticleUseCase;
    private final DeleteArticleUseCase deleteArticleUseCase;
    private final UpdateArticleUseCase updateArticleUseCase;
    private final GetArticleListUseCase getArticleListUseCase;

    public ArticleViewModel(CreateArticleUseCase createArticleUseCase,
                            DeleteArticleUseCase deleteArticleUseCase,
                            UpdateArticleUseCase updateArticleUseCase,
                            GetArticleListUseCase getArticleListUseCase) {
        this.createArticleUseCase = createArticleUseCase;
        this.deleteArticleUseCase = deleteArticleUseCase;
        this.updateArticleUseCase = updateArticleUseCase;
        this.getArticleListUseCase = getArticleListUseCase;
        articlesLiveData = new MutableLiveData<>(new ArrayList<>());
        statusLiveData = new MutableLiveData<>();
    }

    public LiveData<List<ArticleEntity>> getArticles() {
        return articlesLiveData;
    }

    public LiveData<Status<List<ArticleEntity>>> getStatus() {
        return statusLiveData;
    }

    public void addArticle(String title, String content) {
        createArticleUseCase.execute(title, content, status -> {
            if (status.isSuccess()) {
                loadAllArticles();
            } else {
                Log.e("ArticleViewModel", "Error adding article: " +
                        (status.getError() != null ? status.getError().getMessage() : "Unknown error"));
            }
        });
    }

    public void deleteArticle(String id) {
        deleteArticleUseCase.execute(id, status -> {
            if (status.isSuccess()) {
                loadAllArticles();
            } else {
                Log.e("ArticleViewModel", "Error deleting article: " +
                        (status.getError() != null ? status.getError().getMessage() : "Unknown error"));
            }
        });
    }

    public void updateArticle(String id, String title, String content) {
        updateArticleUseCase.execute(id, title, content, status -> {
            if (status.isSuccess()) {
                loadAllArticles();
            } else {
                Log.e("ArticleViewModel", "Error updating article: " +
                        (status.getError() != null ? status.getError().getMessage() : "Unknown error"));
            }
        });
    }

    public void loadAllArticles() {
        statusLiveData.setValue(new Status<>(StatusCode.LOADING, null, null));
        getArticleListUseCase.execute("all", status -> {
            if (status.isSuccess()) {
                List<ArticleEntity> articles = status.getValue();
                if (articles != null) {
                    articlesLiveData.postValue(articles);
                }
            } else {
                Log.e("ArticleViewModel", "Error loading articles: " +
                        (status.getError() != null ? status.getError().getMessage() : "Unknown error"));
            }
            statusLiveData.setValue(status);
        });
    }
}