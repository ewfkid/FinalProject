package com.example.spacex.ui.event;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spacex.R;
import com.example.spacex.databinding.FragmentEventBinding;
import com.example.spacex.domain.entity.FullEventEntity;

public class EventFragment extends Fragment {

    private FragmentEventBinding binding;

    private EventViewModel viewModel;

    private static final String KEY_ID = "id";

    public EventFragment() {
        super(R.layout.fragment_event);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentEventBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(EventViewModel.class);
        viewModel.stateLiveData.observe(getViewLifecycleOwner(), state -> {
            final FullEventEntity entity = state.getEvent();
            if (entity == null) return;
            binding.tvTitle.setText(entity.getTitle());
            binding.tvEventDetails.setText(entity.getEventDetails());
            binding.tvEventDate.setText(entity.getEventDateUtc());
            binding.tvFlightNumber.setText(entity.getFlightNumber());
        });

        String id = getArguments() != null ? getArguments().getString(KEY_ID) : null;
        if (id == null) throw new IllegalStateException("ID cannot be null");
        viewModel.load(id);
    }

    @Override
    public void onDestroy() {
        binding = null;
        super.onDestroy();
    }

    public static Bundle getBundle(@NonNull String id){
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ID, id);
        return bundle;
    }
}
