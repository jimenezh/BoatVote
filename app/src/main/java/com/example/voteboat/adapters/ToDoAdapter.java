package com.example.voteboat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voteboat.databinding.ItemTodoBinding;
import com.example.voteboat.models.ToDoItem;

import java.util.ArrayList;
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

        public void bind(ToDoItem item) {
            binding.tvElectionName.setText(item.getName());
            // TODO: actually bind things

        }
    }
}
