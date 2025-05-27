package com.example.spacex.ui.favourites_list;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spacex.data.repository.FavouritesRepositoryImpl;
import com.example.spacex.domain.entity.ItemArticleEntity;
import com.example.spacex.domain.entity.Status;
import com.example.spacex.domain.favourites.AddToFavouritesUseCase;
import com.example.spacex.domain.favourites.GetFavouritesListUseCase;
import com.example.spacex.domain.favourites.RemoveFromFavouritesUseCase;

import java.util.List;

public class FavouritesListViewModel extends ViewModel {

    private final MutableLiveData<State> mutableLiveData = new MutableLiveData<>();
    public final LiveData<State> stateLiveData = mutableLiveData;

    // ** UseCases ** //
    private final GetFavouritesListUseCase getFavouritesListUseCase = new GetFavouritesListUseCase(
            FavouritesRepositoryImpl.getInstance()
    );

    private final RemoveFromFavouritesUseCase removeFromFavouritesUseCase = new RemoveFromFavouritesUseCase(
            FavouritesRepositoryImpl.getInstance()
    );
    // ** UseCases ** //

    public FavouritesListViewModel() {
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
        getFavouritesListUseCase.execute(status -> {
            mutableLiveData.postValue(fromStatus(status));
        });
    }

    public void removeFromFavourites(String articleId) {
        removeFromFavouritesUseCase.execute(articleId, status -> {
            if (status.getError() == null) {
                update();
            }
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
