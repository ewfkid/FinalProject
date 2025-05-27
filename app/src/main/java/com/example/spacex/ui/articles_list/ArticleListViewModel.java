package com.example.spacex.ui.articles_list;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spacex.data.repository.ArticleRepositoryImpl;
import com.example.spacex.data.repository.FavouritesRepositoryImpl;
import com.example.spacex.domain.article.GetArticleListUseCase;
import com.example.spacex.domain.entity.ItemArticleEntity;
import com.example.spacex.domain.entity.Status;
import com.example.spacex.domain.favourites.AddToFavouritesUseCase;
import com.example.spacex.domain.favourites.RemoveFromFavouritesUseCase;

import java.util.List;

public class ArticleListViewModel extends ViewModel {

    private final MutableLiveData<State> mutableLiveData = new MutableLiveData<>();
    public final LiveData<State> stateLiveData = mutableLiveData;

    private final MutableLiveData<Boolean> isFavouriteLiveData = new MutableLiveData<>();
    public LiveData<Boolean> getIsFavouriteLiveData() {
        return isFavouriteLiveData;
    }

    // ** UseCases ** //
    private final GetArticleListUseCase getArticleListUseCase = new GetArticleListUseCase(
            ArticleRepositoryImpl.getInstance()
    );

    private final AddToFavouritesUseCase addToFavouritesUseCase = new AddToFavouritesUseCase(
            FavouritesRepositoryImpl.getInstance()
    );

    private final RemoveFromFavouritesUseCase removeFromFavouritesUseCase = new RemoveFromFavouritesUseCase(
            FavouritesRepositoryImpl.getInstance()
    );
    // ** UseCases ** //

    private String currentArticleId;

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
        getArticleListUseCase.execute(status -> mutableLiveData.postValue(fromStatus(status)));
    }

    public void addToFavourites() {
        Boolean current = isFavouriteLiveData.getValue();
        boolean newValue = current == null || !current;
        isFavouriteLiveData.setValue(newValue);

        if (currentArticleId == null) return;

        if (newValue) {
            addToFavouritesUseCase.execute(currentArticleId, status -> {
                if (status.getError() != null) {
                    isFavouriteLiveData.postValue(false);
                } else {
                    update();
                }
            });
        } else {
            removeFromFavouritesUseCase.execute(currentArticleId, status -> {
                if (status.getError() != null) {
                    isFavouriteLiveData.postValue(true);
                } else {
                    update();
                }
            });
        }
    }


    public void setCurrentArticle(String articleId, boolean isFavourite) {
        currentArticleId = articleId;
        isFavouriteLiveData.setValue(isFavourite);
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