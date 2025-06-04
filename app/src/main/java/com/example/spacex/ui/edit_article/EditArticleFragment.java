package com.example.spacex.ui.edit_article;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.spacex.R;
import com.example.spacex.databinding.FragmentEditArticleBinding;
import com.example.spacex.domain.entity.FullArticleEntity;
import com.example.spacex.ui.article_and_comments.SharedScreenFragment;
import com.example.spacex.ui.utils.OnChangeText;
import com.example.spacex.ui.utils.Utils;

public class EditArticleFragment extends Fragment {

    private final static String KEY_ARTICLE_ID = "articleId";

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
        String articleId = getArguments() != null ? getArguments().getString(KEY_ARTICLE_ID) : null;
        if (articleId == null) throw new IllegalStateException("Id cannot be null");
        viewModel.load(articleId);
        binding.etArticleTitle.addTextChangedListener(new OnChangeText() {
            @Override
            public void afterTextChanged(Editable s) {
                viewModel.changeTitle(s.toString());
            }
        });
        binding.etArticleContent.addTextChangedListener(new OnChangeText() {
            @Override
            public void afterTextChanged(Editable s) {
                viewModel.changeContent(s.toString());
            }
        });
        subscribe(viewModel);
        viewModel.errorLiveData.observe(getViewLifecycleOwner(), error ->
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show()
        );
        viewModel.openProfileLiveData.observe(getViewLifecycleOwner(), unused -> viewArticleFragment(articleId));
        binding.save.setOnClickListener(v -> viewModel.save());
    }

    private void viewArticleFragment(@NonNull String articleId) {
        View view = getView();
        if (view == null) return;
        Navigation.findNavController(view).navigate(
                R.id.action_editArticleFragment_to_articleScreenFragment,
                SharedScreenFragment.getBundle(articleId)
        );
    }


    private void subscribe(final EditArticleViewModel viewModel) {
        viewModel.stateLiveData.observe(getViewLifecycleOwner(), state -> {
            boolean isSuccess = !state.isLoading()
                    && state.getErrorMessage() == null
                    && state.getArticle() != null;

            binding.loading.setVisibility(Utils.visibleOrGone(state.isLoading()));
            binding.error.setVisibility(Utils.visibleOrGone(state.getErrorMessage() != null));
            binding.error.setText(state.getErrorMessage());
            binding.relativeParent.setVisibility(Utils.visibleOrGone(isSuccess));

            if (isSuccess) {
                FullArticleEntity article = state.getArticle();
                if (binding.etArticleTitle.getText().toString().isEmpty()) {
                    binding.etArticleTitle.setText(article.getTitle());
                }

                if (binding.etArticleContent.getText().toString().isEmpty()) {
                    binding.etArticleContent.setText(article.getContent());
                }
            }
        });
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
