package com.example.spacex.ui.create_article;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.spacex.data.repository.ArticleRepositoryImpl;
import com.example.spacex.domain.article.CreateArticleUseCase;

import java.util.ArrayList;

public class CreateArticleViewModel extends AndroidViewModel {

    private final MutableLiveData<String> mutableErrorLiveData = new MutableLiveData<>();
    public final LiveData<String> errorLiveData = mutableErrorLiveData;

    private final MutableLiveData<Boolean> mutableOpenArticlesList = new MutableLiveData<>();
    public final LiveData<Boolean> openArticlesListLiveData = mutableOpenArticlesList;

    private final CreateArticleUseCase createArticleUseCase = new CreateArticleUseCase(
            ArticleRepositoryImpl.getInstance()
    );

    private final SharedPreferences sharedPreferences;

    private String title;
    private String content;

    public CreateArticleViewModel(@NonNull Application application) {
        super(application);
        sharedPreferences = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
    }

    public void changeTitle(@NonNull String title) {
        this.title = title;
    }

    public void changeContent(@NonNull String content) {
        this.content = content;
    }

    public void create() {
        if (title == null || title.isEmpty()) {
            mutableErrorLiveData.postValue("Title cannot be empty");
            return;
        }
        if (content == null || content.isEmpty()) {
            mutableErrorLiveData.postValue("Content cannot be empty");
            return;
        }

        String username = sharedPreferences.getString("username", null);

        String photoUrl = sharedPreferences.getString("photoUrl", null);

        if (username == null) {
            mutableErrorLiveData.postValue("User not logged in");
            return;
        }

        createArticleUseCase.execute(
                title,
                content,
                username,
                photoUrl,
                0,
                0,
                new ArrayList<>(),
                false,
                status -> {
                    if (status.getStatusCode() == 200 && status.getError() == null) {
                        mutableOpenArticlesList.postValue(true);
                    } else {
                        mutableErrorLiveData.postValue("Something went wrong. Try again later");
                    }
                }
        );
    }
}
