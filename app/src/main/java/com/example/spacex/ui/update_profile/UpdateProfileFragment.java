package com.example.spacex.ui.update_profile;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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

    public UpdateProfileFragment() {
        super(R.layout.fragment_update_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentUpdateProfileBinding.bind(view);
        UserSessionManager userSessionManager = new UserSessionManager(requireContext());
        UpdateProfileViewModelFactory factory = new UpdateProfileViewModelFactory(userSessionManager);
        UpdateProfileViewModel viewModel = new ViewModelProvider(this, factory).get(UpdateProfileViewModel.class);
        String id = getArguments() != null ? getArguments().getString(KEY_ID) : null;
        if (id == null) throw new IllegalStateException("Id cannot be null");
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
        viewModel.load(id);
        subscribe(viewModel, id);
    }

    private void subscribe(final UpdateProfileViewModel viewModel, @NonNull String userId) {
        viewModel.stateLiveData.observe(getViewLifecycleOwner(), state -> {
            boolean isSuccess = !state.isLoading()
                    && state.getErrorMessage() == null
                    && state.getUser() != null;
            binding.loading.setVisibility(Utils.visibleOrGone(state.isLoading()));
            binding.error.setVisibility(Utils.visibleOrGone(state.getErrorMessage() != null));
            binding.error.setText(state.getErrorMessage());
            binding.constraintParent.setVisibility(Utils.visibleOrGone(isSuccess));
            binding.loading.setVisibility(Utils.visibleOrGone(state.isLoading()));
            binding.error.setVisibility(Utils.visibleOrGone(state.getErrorMessage() != null));
            if (isSuccess) {
                UserEntity user = state.getUser();
                if (user.getPhotoUrl() != null) {
                    Picasso.get().load(user.getPhotoUrl()).into(binding.userImage);
                } else {
                    binding.userImage.setImageResource(R.drawable.ic_default_user_avatar);
                }
            }
            binding.buttonSave.setOnClickListener(v -> viewModel.save(userId));
            binding.editUserImage.setOnClickListener(v -> {});
        });
    }

    private void goBack() {
        final View view = getView();
        if (view == null) return;
        Navigation.findNavController(view).navigate(R.id.action_updateProfileFragment_to_profileFragment);
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
