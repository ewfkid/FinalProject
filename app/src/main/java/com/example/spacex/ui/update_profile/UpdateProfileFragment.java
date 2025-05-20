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
import com.example.spacex.ui.utils.OnChangeText;

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
        binding.close.setOnClickListener(v -> goBack());

        binding.editEmail.addTextChangedListener(new OnChangeText(){
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                viewModel.changeEmail(s.toString());
            }
        });
        binding.editName.addTextChangedListener(new OnChangeText(){
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                viewModel.changeName(s.toString());
            }
        });
        binding.editUsername.addTextChangedListener(new OnChangeText(){
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                viewModel.changeUsername(s.toString());
            }
        });
        binding.editPhone.addTextChangedListener(new OnChangeText(){
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                viewModel.changePhone(s.toString());
            }
        });

        binding.buttonSave.setOnClickListener(v -> viewModel.save());
        //TODO:
        binding.editUserImage.setOnClickListener(v -> {});
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
