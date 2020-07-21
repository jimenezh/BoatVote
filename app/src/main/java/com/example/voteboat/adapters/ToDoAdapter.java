package com.example.voteboat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voteboat.databinding.ItemTodoBinding;
import com.example.voteboat.models.Election;
import com.example.voteboat.models.ToDoItem;
import com.example.voteboat.models.User;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
            // Check to see if to do date is valid for item's election
            Election e = (Election) item.get("election");
            if (hasElectionPassed(e)) {
                // Update election item
                if (!e.getHasPassed())
                    e.setElectionHasPassed();
                // Add to user's past elections if user voted
                if(item.hasVoted())
                    User.addToPastElections(e);
                // Remove items from UI + database
                toDoItems.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
                item.deleteInBackground();
            }

            binding.tvElectionName.setText(item.getName());
            binding.btnRegister.starButton.setLiked(item.isRegistered());
            binding.btnDocs.starButton.setLiked(item.hasDocuments());
            binding.btnVote.starButton.setLiked(item.hasVoted());

            setRegisteredListener(item);
            setDocumentsListener(item);
            setVoteListener(item);
        }

        private boolean hasElectionPassed(Election election) {
            String d = election.getElectionDate();
            try {
                Date electionDate = new SimpleDateFormat("MM/dd/yyyy").parse(d);
                Date today = new Date();
                return electionDate.before(today);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return false;
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
