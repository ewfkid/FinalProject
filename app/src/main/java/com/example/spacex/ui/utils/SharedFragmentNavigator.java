package com.example.spacex.ui.utils;

import androidx.annotation.NonNull;

public interface SharedFragmentNavigator {
    void onEditArticleRequested(@NonNull String articleId);

    void onArticlesListRequested();
}
