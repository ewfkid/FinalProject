package com.example.spacex.ui.create_article;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spacex.R;
import com.example.spacex.databinding.FragmentCreateArticleBinding;

public class CreateArticleFragment extends Fragment {

    private FragmentCreateArticleBinding binding;

    private CreateArticleViewModel viewModel;

    public CreateArticleFragment() {
        super(R.layout.fragment_create_article);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentCreateArticleBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(CreateArticleViewModel.class);
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
