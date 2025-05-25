package com.example.spacex.ui.articles_list;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.spacex.R;
import com.example.spacex.databinding.FragmentArticlesListBinding;
import com.example.spacex.ui.article_and_comments.SharedScreenFragment;
import com.example.spacex.ui.utils.Utils;

public class ArticleListFragment extends Fragment {

    private FragmentArticlesListBinding binding;

    private ArticleListViewModel viewModel;

    public ArticleListFragment() {
        super(R.layout.fragment_articles_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentArticlesListBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(ArticleListViewModel.class);
        final ArticleListAdapter adapter = new ArticleListAdapter(id -> viewArticle(id));
        subscribe(viewModel, adapter);
        binding.recycler.setAdapter(adapter);
    }

    private void subscribe(final ArticleListViewModel viewModel, ArticleListAdapter adapter){
        viewModel.stateLiveData.observe(getViewLifecycleOwner(), state -> {
            boolean isSuccess = !state.isLoading()
                    && state.getErrorMessage() == null
                    && state.getItems() != null;
            binding.refresh.setEnabled(!state.isLoading());
            if (!state.isLoading()) binding.refresh.setRefreshing(false);
            binding.recycler.setVisibility(Utils.visibleOrGone(isSuccess));
            binding.error.setVisibility(Utils.visibleOrGone(state.getErrorMessage() != null));
            binding.loading.setVisibility(Utils.visibleOrGone(state.isLoading()));
            binding.addArticleButton.setOnClickListener(v -> openCreateArticleFragment());
            binding.error.setText(state.getErrorMessage());
            if (isSuccess) {
                adapter.updateData(state.getItems());
            }
        });
    }

    private void viewArticle(@NonNull String articleId) {
        View view = getView();
        if (view == null) return;
        Navigation.findNavController(view).navigate(
                R.id.action_articlesListFragment_to_articleScreenFragment,
                SharedScreenFragment.getBundle(articleId)
        );
    }

    private void openCreateArticleFragment(){
        View view = getView();
        if (view == null) return;
        Navigation.findNavController(view).navigate(
                R.id.action_articlesListFragment_to_createArticleFragment
        );
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
