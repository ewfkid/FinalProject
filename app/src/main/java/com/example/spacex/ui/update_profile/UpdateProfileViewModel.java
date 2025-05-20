package com.example.spacex.ui.update_profile;

import androidx.lifecycle.ViewModel;

import com.example.spacex.data.repository.UserRepositoryImpl;
import com.example.spacex.domain.user.GetUserByIdUseCase;
import com.example.spacex.domain.user.UpdateUserUseCase;

public class UpdateProfileViewModel extends ViewModel {
    
    UpdateUserUseCase updateUserUseCase = new UpdateUserUseCase(
            UserRepositoryImpl.getInstance()
    );
    
    GetUserByIdUseCase getUserByIdUseCase = new GetUserByIdUseCase(
            UserRepositoryImpl.getInstance()
    );

    public void changeEmail(String newEmail) {
    }

    public void changeName(String newName) {
    }

    public void changeUsername(String newUsername) {
    }

    public void changePhone(String newPhone) {
    }

    public void save() {
    }
}
