package com.example.spacex.ui.update_profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.spacex.R;
import com.example.spacex.databinding.FragmentUpdateProfileBinding;
import com.example.spacex.domain.entity.UserEntity;
import com.example.spacex.ui.update_profile.factory.UpdateProfileViewModelFactory;
import com.example.spacex.ui.utils.OnChangeText;
import com.example.spacex.ui.utils.UserSessionManager;
import com.example.spacex.ui.utils.Utils;
import com.squareup.picasso.Picasso;

public class UpdateProfileFragment extends Fragment {

    private FragmentUpdateProfileBinding binding;
    private UpdateProfileViewModel viewModel;
    private static final String KEY_ID = "user_id";
    private String userId;

    public UpdateProfileFragment() {
        super(R.layout.fragment_update_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentUpdateProfileBinding.bind(view);
        UserSessionManager userSessionManager = new UserSessionManager(requireContext());
        UpdateProfileViewModelFactory factory = new UpdateProfileViewModelFactory(userSessionManager);
        viewModel = new ViewModelProvider(this, factory).get(UpdateProfileViewModel.class);
        String id = getArguments() != null ? getArguments().getString(KEY_ID) : null;
        if (id == null) throw new IllegalStateException("Id cannot be null");
        userId = id;
        viewModel.load(userId);
        subscribe(viewModel, userId);
        binding.close.setOnClickListener(v -> goBack());
        binding.editEmail.addTextChangedListener(new OnChangeText() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                viewModel.changeEmail(s.toString());
            }
        });
        binding.editName.addTextChangedListener(new OnChangeText() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                viewModel.changeName(s.toString());
            }
        });
        binding.editPhone.addTextChangedListener(new OnChangeText() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                viewModel.changePhone(s.toString());
            }
        });


        binding.buttonSave.setOnClickListener(v -> viewModel.save(userId));
    }



    private void subscribe(UpdateProfileViewModel viewModel, @NonNull String userId) {
        viewModel.stateLiveData.observe(getViewLifecycleOwner(), state -> {
            boolean isSuccess = !state.isLoading()
                    && state.getErrorMessage() == null
                    && state.getUser() != null;
            binding.loading.setVisibility(Utils.visibleOrGone(state.isLoading()));
            binding.error.setVisibility(Utils.visibleOrGone(state.getErrorMessage() != null));
            binding.error.setText(state.getErrorMessage());
            binding.constraintParent.setVisibility(Utils.visibleOrGone(isSuccess));
            if (isSuccess) {
                UserEntity user = state.getUser();
                if (user.getPhotoUrl() != null) {
                    Picasso.get().load(user.getPhotoUrl()).into(binding.userImage);
                } else {
                    binding.userImage.setImageResource(R.drawable.ic_default_user_avatar);
                }
                if (binding.editName.getText().toString().isEmpty()) {
                    binding.editName.setText(user.getName());
                }
                if (binding.editEmail.getText().toString().isEmpty()) {
                    binding.editEmail.setText(user.getEmail());
                }
                if (!TextUtils.isEmpty(user.getPhone()) && binding.editPhone.getText().toString().isEmpty()) {
                    binding.editPhone.setText(user.getPhone());
                }
            }
        });
        viewModel.openProfileLiveData.observe(getViewLifecycleOwner(), unused -> goBack());
    }

    private void goBack() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(
                R.id.profileFragment,
                null,
                new NavOptions.Builder()
                        .setPopUpTo(R.id.eventListFragment, true)
                        .build()
        );
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
