package com.example.spacex.ui.article_and_comments.comments_list;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spacex.R;
import com.example.spacex.databinding.FragmentCommentsListBinding;

public class CommentListFragment extends Fragment {

    private FragmentCommentsListBinding binding;

    private CommentListViewModel viewModel;

    public CommentListFragment(){
        super(R.layout.fragment_comments_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentCommentsListBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(CommentListViewModel.class);
        final CommentListAdapter adapter = new CommentListAdapter();
        binding.recycler.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
