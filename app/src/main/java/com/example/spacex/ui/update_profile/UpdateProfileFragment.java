package com.example.spacex.ui.update_profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.example.spacex.R;
import com.example.spacex.databinding.FragmentUpdateProfileBinding;
import com.example.spacex.domain.entity.UserEntity;
import com.example.spacex.ui.update_profile.factory.UpdateProfileViewModelFactory;
import com.example.spacex.ui.utils.OnChangeText;
import com.example.spacex.ui.service.UserSessionManager;
import com.example.spacex.ui.utils.Utils;
import com.squareup.picasso.Picasso;

public class UpdateProfileFragment extends Fragment {

    private FragmentUpdateProfileBinding binding;
    private UpdateProfileViewModel viewModel;
    private static final String KEY_ID = "user_id";
    private String userId;

    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<CropImageContractOptions> cropImageActivity;

    public UpdateProfileFragment() {
        super(R.layout.fragment_update_profile);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        Log.d("UpdateProfile", "Image picked: " + imageUri);
                        startCrop(imageUri);
                    }
                });

        cropImageActivity = registerForActivityResult(new CropImageContract(), result -> {
            Uri croppedImageUri = result.getUriContent();
            if (croppedImageUri != null) {
                Log.d("UpdateProfile", "Cropped image URI: " + croppedImageUri);
                viewModel.uploadAvatar(croppedImageUri, requireActivity().getContentResolver());
            } else if (result.getError() != null) {
                Log.e("UpdateProfile", "Crop error: " + result.getError().getMessage());
                Toast.makeText(requireContext(), "Failed to crop: " + result.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

        Log.d("UpdateProfile", "Loading user ID: " + userId);
        viewModel.load(userId);
        subscribe(viewModel);

        binding.close.setOnClickListener(v -> goBack());

        binding.editEmail.addTextChangedListener(new OnChangeText() {
            @Override
            public void afterTextChanged(Editable s) {
                viewModel.changeEmail(s.toString());
            }
        });
        binding.editName.addTextChangedListener(new OnChangeText() {
            @Override
            public void afterTextChanged(Editable s) {
                viewModel.changeName(s.toString());
            }
        });
        binding.editPhone.addTextChangedListener(new OnChangeText() {
            @Override
            public void afterTextChanged(Editable s) {
                viewModel.changePhone(s.toString());
            }
        });

        binding.editUserImage.setOnClickListener(v -> launchGalleryPicker());

        binding.buttonSave.setOnClickListener(v -> {
            Log.d("UpdateProfile", "Save button clicked for user: " + userId);
            viewModel.save(userId);
        });
    }

    private void subscribe(UpdateProfileViewModel viewModel) {
        viewModel.stateLiveData.observe(getViewLifecycleOwner(), state -> {
            boolean isSuccess = !state.isLoading()
                    && state.getErrorMessage() == null
                    && state.getUser() != null;

            Log.d("UpdateProfile", "State update: loading=" + state.isLoading() +
                    ", error=" + state.getErrorMessage() +
                    ", user=" + (state.getUser() != null ? state.getUser().getId() : "null"));

            binding.loading.setVisibility(Utils.visibleOrGone(state.isLoading()));
            binding.error.setVisibility(Utils.visibleOrGone(state.getErrorMessage() != null));
            binding.error.setText(state.getErrorMessage());
            binding.constraintParent.setVisibility(Utils.visibleOrGone(isSuccess));

            if (isSuccess) {
                UserEntity user = state.getUser();
                Log.d("UpdateProfile", "User loaded: " + user.getName() + ", photoUrl: " + user.getPhotoUrl());

                if (user.getPhotoUrl() != null) {
                    Picasso.get()
                            .load(user.getPhotoUrl())
                            .placeholder(R.drawable.ic_default_user_avatar)
                            .error(R.drawable.ic_default_user_avatar)
                            .into(binding.userImage, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    Log.d("UpdateProfile", "Avatar loaded successfully.");
                                }

                                @Override
                                public void onError(Exception e) {
                                    Log.e("UpdateProfile", "Failed to load avatar: " + e.getMessage());
                                }
                            });
                } else {
                    Log.d("UpdateProfile", "No avatar URL found. Setting default image.");
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


        viewModel.openProfileLiveData.observe(getViewLifecycleOwner(), unused -> {
            Log.d("UpdateProfile", "Navigation back to profile.");
            goBack();
        });
    }

    private void launchGalleryPicker() {
        Log.d("UpdateProfile", "Launching gallery picker...");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void startCrop(Uri imageUri) {
        Log.d("UpdateProfile", "Starting crop for image: " + imageUri);
        CropImageOptions options = new CropImageOptions();
        options.guidelines = CropImageView.Guidelines.ON;
        options.fixAspectRatio = true;
        options.aspectRatioX = 1;
        options.aspectRatioY = 1;
        options.cropShape = CropImageView.CropShape.RECTANGLE;
        options.showCropOverlay = true;
        options.outputCompressFormat = Bitmap.CompressFormat.PNG;

        CropImageContractOptions contractOptions = new CropImageContractOptions(imageUri, options);
        cropImageActivity.launch(contractOptions);
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
