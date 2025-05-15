package com.example.spacex.ui.article_and_comments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spacex.R;
import com.example.spacex.databinding.FragmentArticleScreenBinding;

public class ArticleScreenFragment extends Fragment {

    private FragmentArticleScreenBinding binding;

    public ArticleScreenFragment(){
        super(R.layout.fragment_article_screen);
    }

    private static final String KEY_ARTICLE_ID = "article_id";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentArticleScreenBinding.bind(view);
    }

    public static Bundle getBundle(@NonNull String articleId) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ARTICLE_ID, articleId);
        return bundle;
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
