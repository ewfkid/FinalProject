package com.example.spacex.ui.article_and_comments.comments_list;


import android.os.Bundle;
import android.text.Editable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spacex.R;
import com.example.spacex.databinding.FragmentCommentsListBinding;
import com.example.spacex.ui.article_and_comments.comments_list.factory.CommentListViewModelFactory;
import com.example.spacex.ui.utils.OnChangeText;
import com.example.spacex.ui.utils.Utils;

public class CommentListFragment extends Fragment {

    private FragmentCommentsListBinding binding;
    private CommentListViewModel viewModel;
    private static final String KEY_ARTICLE_ID = "articleId";

    public CommentListFragment() {
        super(R.layout.fragment_comments_list);
    }

    public static CommentListFragment newInstance(@NonNull String articleId) {
        CommentListFragment fragment = new CommentListFragment();
        Bundle args = new Bundle();
        args.putString(KEY_ARTICLE_ID, articleId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentCommentsListBinding.bind(view);

        String articleId = getArguments() != null ? getArguments().getString(KEY_ARTICLE_ID) : null;
        if (articleId == null) throw new IllegalStateException("Article Id cannot be null");

        CommentListViewModelFactory factory = new CommentListViewModelFactory(articleId);
        viewModel = new ViewModelProvider(this, factory).get(CommentListViewModel.class);

        CommentListAdapter adapter = new CommentListAdapter();
        binding.recycler.setAdapter(adapter);
        binding.refresh.setOnRefreshListener(() -> viewModel.update(articleId));
        subscribe(viewModel, adapter);
        viewModel.update(articleId);
    }

    private void subscribe(final CommentListViewModel viewModel, CommentListAdapter adapter) {
        viewModel.stateLiveData.observe(getViewLifecycleOwner(), state -> {
            boolean isSuccess = !state.isLoading()
                    && state.getErrorMessage() == null
                    && state.getItems() != null;

            binding.refresh.setEnabled(!state.isLoading());
            if (!state.isLoading()) binding.refresh.setRefreshing(false);
            binding.recycler.setVisibility(Utils.visibleOrGone(isSuccess));
            binding.error.setVisibility(Utils.visibleOrGone(state.getErrorMessage() != null));
            binding.loading.setVisibility(Utils.visibleOrGone(state.isLoading()));

            binding.error.setText(state.getErrorMessage());

            if (isSuccess) {
                adapter.updateData(state.getItems());
            }
        });
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
