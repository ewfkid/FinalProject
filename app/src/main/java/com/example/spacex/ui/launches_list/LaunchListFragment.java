package com.example.spacex.ui.launches_list;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spacex.R;
import com.example.spacex.databinding.FragmentLaunchesListBinding;

public class LaunchListFragment extends Fragment {

    private FragmentLaunchesListBinding binding;

    private LaunchListViewModel viewModel;

    public LaunchListFragment() {
        super(R.layout.fragment_launches_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentLaunchesListBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(LaunchListViewModel.class);

    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
