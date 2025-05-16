package com.example.spacex.ui.article_and_comments.comments_list;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spacex.data.CommentRepositoryImpl;
import com.example.spacex.domain.comment.GetCommentListUseCase;
import com.example.spacex.domain.entity.CommentEntity;
import com.example.spacex.domain.entity.Status;

import java.util.List;

public class CommentListViewModel extends ViewModel {

    private final MutableLiveData<State> mutableLiveData = new MutableLiveData<>();

    public final LiveData<State> stateLiveData = mutableLiveData;

    private final GetCommentListUseCase getCommentListUseCase = new GetCommentListUseCase(
            CommentRepositoryImpl.getInstance()
    );

    public CommentListViewModel(@NonNull String articleId){
        update(articleId);
    }

    public void update(@NonNull String articleId) {
        mutableLiveData.setValue(new State(null, null, true));
        getCommentListUseCase.execute(articleId, status -> {
            mutableLiveData.postValue(fromStatus(status));
        });
    }

    private State fromStatus(Status<List<CommentEntity>> status) {
        List<CommentEntity> comments = status.getValue();

        return new State(
                status.getError() != null ? status.getError().getLocalizedMessage() : null,
                status.getValue(),
                false
        );
    }


    public class State {

        @Nullable
        private final String errorMessage;

        @Nullable
        private final List<CommentEntity> items;

        private final boolean isLoading;

        public State(@Nullable String errorMessage, @Nullable List<CommentEntity> items, boolean isLoading) {
            this.errorMessage = errorMessage;
            this.items = items;
            this.isLoading = isLoading;
        }

        @Nullable
        public String getErrorMessage() {
            return errorMessage;
        }

        @Nullable
        public List<CommentEntity> getItems() {
            return items;
        }

        public boolean isLoading() {
            return isLoading;
        }
    }
}
