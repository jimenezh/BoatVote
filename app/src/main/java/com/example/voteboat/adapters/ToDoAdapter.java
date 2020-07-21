package com.example.voteboat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voteboat.databinding.ItemTodoBinding;
import com.example.voteboat.models.ToDoItem;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {


    private Context context;
    private List<ToDoItem> toDoItems;


    public ToDoAdapter(Context context, List<ToDoItem> elections) {
        this.context = context;
        this.toDoItems = elections;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTodoBinding binding = ItemTodoBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(toDoItems.get(position));
    }

    @Override
    public int getItemCount() {
        return toDoItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemTodoBinding binding;

        public ViewHolder(@NonNull ItemTodoBinding itemTodoBinding) {
            super(itemTodoBinding.getRoot());
            this.binding = itemTodoBinding;
        }

        public void bind(final ToDoItem item) {
            binding.tvElectionName.setText(item.getName());
            binding.btnRegister.starButton.setLiked(item.isRegistered());
            binding.btnDocs.starButton.setLiked(item.hasDocuments());
            binding.btnVote.starButton.setLiked(item.hasVoted());

            setRegisteredListener(item);
            setDocumentsListener(item);
            setVoteListener(item);
        }



        private void setVoteListener(final ToDoItem item) {
            binding.btnVote.starButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    // add to list
                    if (!item.hasVoted())
                        item.setVoted(true);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    // remove from list if registered
                    if (item.hasVoted())
                        item.setVoted(false);
                }
            });
        }

        private void setDocumentsListener(final ToDoItem item) {
            binding.btnDocs.starButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    // add to list
                    if (!item.hasDocuments())
                        item.setDocuments(true);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    // remove from list if registered
                    if (item.isRegistered())
                        item.setDocuments(false);
                }
            });
        }

        private void setRegisteredListener(final ToDoItem item) {
            binding.btnRegister.starButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    // add to list
                    if (!item.isRegistered())
                        item.setRegistered(true);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    // remove from list if registered
                    if (item.isRegistered())
                        item.setRegistered(false);
                }
            });
        }
    }
}
