package com.example.spacex.ui.favourites_list;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacex.R;
import com.example.spacex.databinding.ItemArticleBinding;
import com.example.spacex.domain.entity.ItemArticleEntity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FavouritesListAdapter extends  RecyclerView.Adapter<FavouritesListAdapter.ViewHolder>{


    @NonNull
    private final Consumer<String> onItemClick;

    private final List<ItemArticleEntity> data = new ArrayList<>();

    public FavouritesListAdapter(@NonNull Consumer<String> onItemClick) {
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavouritesListAdapter.ViewHolder(
                ItemArticleBinding.inflate(
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

    public void updateData(List<ItemArticleEntity> newData) {
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemArticleBinding binding;


        public ViewHolder(@NonNull ItemArticleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ItemArticleEntity item){
            binding.articleTitle.setText(item.getTitle());
            binding.userNickname.setText(item.getUsername());
            binding.amountOfDislikes.setText(item.getDislikes().toString());
            binding.amountOfLikes.setText(item.getDislikes().toString());
            if (item.getPhotoUrl() != null) {
                Picasso.get().load(item.getPhotoUrl()).into(binding.userImage);
            } else {
                binding.userImage.setImageResource(R.drawable.ic_default_user_avatar);
            }
            binding.dislikeButton.setOnClickListener(v -> {});
            binding.likeButton.setOnClickListener(v -> {});
            binding.getRoot().setOnClickListener(v -> {
                onItemClick.accept(item.getId());
            });
        }
    }
}
