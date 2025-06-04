package com.example.spacex.ui.edit_article;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spacex.R;
import com.example.spacex.databinding.FragmentEditArticleBinding;
import com.example.spacex.ui.utils.OnChangeText;

public class EditArticleFragment extends Fragment {

    private FragmentEditArticleBinding binding;

    private EditArticleViewModel viewModel;

    public EditArticleFragment() {
        super(R.layout.fragment_edit_article);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentEditArticleBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(EditArticleViewModel.class);
        binding.etArticleTitle.addTextChangedListener(new OnChangeText() {
            @Override
            public void afterTextChanged(Editable s) {
                viewModel.changeTitle(s.toString());
            }
        });
        binding.etArticleContent.addTextChangedListener(new OnChangeText(){
            @Override
            public void afterTextChanged(Editable s) {
                viewModel.changeContent(s.toString());
            }
        });
        binding.save.setOnClickListener(v -> {});
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
