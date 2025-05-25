package com.example.spacex.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.spacex.data.dto.ReactionInitDto;
import com.example.spacex.data.network.RetrofitFactory;
import com.example.spacex.data.source.ReactionApi;
import com.example.spacex.data.utils.CallToConsumer;
import com.example.spacex.domain.entity.Status;
import com.example.spacex.domain.reaction.ReactionRepository;

import java.io.IOException;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReactionRepositoryImpl implements ReactionRepository {

    private final ReactionApi reactionApi = RetrofitFactory.getInstance().getReactionApi();

    private static ReactionRepositoryImpl INSTANCE;

    private ReactionRepositoryImpl() {
    }

    public static synchronized ReactionRepositoryImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ReactionRepositoryImpl();
        }
        return INSTANCE;
    }


//    @Override
//    public void addReaction(
//            @NonNull String articleId,
//            @NonNull String userId,
//            @NonNull String type,
//            Consumer<Status<Void>> callback
//    ) {
//        reactionApi.addReaction(new ReactionInitDto(articleId, userId, type)).enqueue(new CallToConsumer<>(
//                callback,
//                dto -> null
//        ));
//    }
//
//    @Override
//    public void deleteReaction(@NonNull String id, Consumer<Status<Void>> callback) {
//        reactionApi.removeReaction(id).enqueue(new CallToConsumer<>(
//                callback,
//                dto -> null
//        ));
//    }

    @Override
    public void addReaction(
            @NonNull String articleId,
            @NonNull String userId,
            @NonNull String type,
            Consumer<Status<Void>> callback
    ) {
        ReactionInitDto reactionInitDto = new ReactionInitDto(articleId, userId, type);
        Log.d("Reaction_Add", "Sending addReaction request with data: " + reactionInitDto);

        reactionApi.addReaction(reactionInitDto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                int statusCode = response.code();

                if (response.isSuccessful()) {
                    Log.d("Reaction_Add", "Success - Status Code: " + statusCode);
                    callback.accept(new Status<>(statusCode, null, null));
                } else {
                    try {
                        Log.e("Reaction_Add", "Error - Status Code: " + statusCode + ", Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("Reaction_Add", "Error reading error body", e);
                    }
                    callback.accept(new Status<>(statusCode, null, new Throwable("Error response from server")));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Reaction_Add", "Request failed: " + t.getMessage(), t);
                callback.accept(new Status<>(-1, null, t));
            }
        });
    }

    @Override
    public void deleteReaction(@NonNull String id, Consumer<Status<Void>> callback) {
        Log.d("Reaction_Delete", "Sending deleteReaction request for ID: " + id);

        reactionApi.removeReaction(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                int statusCode = response.code();

                if (response.isSuccessful()) {
                    Log.d("Reaction_Delete", "Success - Status Code: " + statusCode);
                    callback.accept(new Status<>(statusCode, null, null));
                } else {
                    try {
                        Log.e("Reaction_Delete", "Error - Status Code: " + statusCode + ", Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("Reaction_Delete", "Error reading error body", e);
                    }
                    callback.accept(new Status<>(statusCode, null, new Throwable("Error response from server")));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Reaction_Delete", "Request failed: " + t.getMessage(), t);
                callback.accept(new Status<>(-1, null, t));
            }
        });
    }
}
