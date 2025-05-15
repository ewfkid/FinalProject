package com.example.spacex.ui.article_and_comments.article;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spacex.R;
import com.example.spacex.databinding.FragmentArticleBinding;

public class ArticleFragment extends Fragment {

    private FragmentArticleBinding binding;

    private ArticleViewModel viewModel;

    public ArticleFragment() {
        super(R.layout.fragment_article);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentArticleBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(ArticleViewModel.class);
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
