package com.example.spacex.ui.event;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spacex.data.EventRepositoryImpl;
import com.example.spacex.domain.event.GetEventByIdUseCase;
import com.example.spacex.domain.entity.FullEventEntity;

public class EventViewModel extends ViewModel {

    private final MutableLiveData<State> mutableStateLiveData = new MutableLiveData<>();

    public final LiveData<State> stateLiveData = mutableStateLiveData;

    public final GetEventByIdUseCase getEventByIdUseCase = new GetEventByIdUseCase(
            EventRepositoryImpl.getInstance()
    );

    public void load(@NonNull String id) {
        mutableStateLiveData.setValue(new State(null, null, true));
        getEventByIdUseCase.execute(id, (status) -> {
            mutableStateLiveData.postValue(new State(
                    status.getError() != null ? status.getError().getLocalizedMessage() : null,
                    status.getValue(),
                    false
            ));
        });
    }

    class State {
        @Nullable
        private final String errorMessage;

        @Nullable
        private final FullEventEntity event;

        private final boolean isLoading;

        public State(@Nullable String errorMessage, @Nullable FullEventEntity event, boolean isLoading) {
            this.errorMessage = errorMessage;
            this.event = event;
            this.isLoading = isLoading;
        }

        @Nullable
        public String getErrorMessage() {
            return errorMessage;
        }

        @Nullable
        public FullEventEntity getEvent() {
            return event;
        }

        public boolean isLoading() {
            return isLoading;
        }
    }
}
