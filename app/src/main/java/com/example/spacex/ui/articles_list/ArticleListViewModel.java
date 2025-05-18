package com.example.spacex.ui.articles_list;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spacex.data.ArticleRepositoryImpl;
import com.example.spacex.domain.article.CreateArticleUseCase;
import com.example.spacex.domain.article.GetArticleListUseCase;
import com.example.spacex.domain.article.UpdateArticleUseCase;
import com.example.spacex.domain.entity.ItemArticleEntity;
import com.example.spacex.domain.entity.Status;

import java.util.List;

public class ArticleListViewModel extends ViewModel {

    private final MutableLiveData<State> mutableLiveData = new MutableLiveData<>();
    public final LiveData<State> stateLiveData = mutableLiveData;

    // ** UseCases ** //
    private final GetArticleListUseCase getArticleListUseCase = new GetArticleListUseCase(
            ArticleRepositoryImpl.getInstance()
    );

    private final UpdateArticleUseCase updateArticleUseCase = new UpdateArticleUseCase(
            ArticleRepositoryImpl.getInstance()
    );

    private final CreateArticleUseCase createArticleUseCase = new CreateArticleUseCase(
            ArticleRepositoryImpl.getInstance()
    );
    // ** UseCases ** //


    public ArticleListViewModel() {
        update();
    }


    private State fromStatus(Status<List<ItemArticleEntity>> status) {
        return new State(
                status.getError() != null ? status.getError().getLocalizedMessage() : null,
                status.getValue(),
                false
        );
    }

    public void update() {
        mutableLiveData.setValue(new State(null, null, true));
        getArticleListUseCase.execute(status -> {
            mutableLiveData.postValue(fromStatus(status));
        });
    }


    public class State {

        @Nullable
        private final String errorMessage;

        @Nullable
        private final List<ItemArticleEntity> items;

        private final boolean isLoading;

        public State(
                @Nullable String errorMessage,
                @Nullable List<ItemArticleEntity> items,
                boolean isLoading
        ) {
            this.errorMessage = errorMessage;
            this.items = items;
            this.isLoading = isLoading;
        }

        @Nullable
        public String getErrorMessage() {
            return errorMessage;
        }

        @Nullable
        public List<ItemArticleEntity> getItems() {
            return items;
        }

        public boolean isLoading() {
            return isLoading;
        }
    }
}