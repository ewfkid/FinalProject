package com.example.spacex.ui.update_profile;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spacex.R;
import com.example.spacex.databinding.FragmentUpdateProfileBinding;

public class UpdateProfileFragment extends Fragment {

    private FragmentUpdateProfileBinding binding;

    private UpdateProfileViewModel viewModel;

    private static final String KEY_ID = "user_id";

    public UpdateProfileFragment() {
        super(R.layout.fragment_update_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentUpdateProfileBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(UpdateProfileViewModel.class);
    }

    public static Bundle getBundle(@NonNull String id) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ID, id);
        return bundle;
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
