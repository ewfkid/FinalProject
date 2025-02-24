package com.example.spacex.ui.list;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacex.databinding.ItemEventBinding;
import com.example.spacex.domain.entity.ItemEventEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {

    private final List<ItemEventEntity> data = new ArrayList<>();

    @NonNull
    private final Consumer<String> onItemClick;

    public EventListAdapter(@NonNull Consumer<String> onItemClick) {
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                ItemEventBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateData(List<ItemEventEntity> newData) {
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemEventBinding binding;

        public ViewHolder(@NonNull ItemEventBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ItemEventEntity item) {
            binding.eventTitle.setText(item.getTitle());
            binding.getRoot().setOnClickListener(v -> {
                onItemClick.accept(item.getId());
            });
        }
    }

}
