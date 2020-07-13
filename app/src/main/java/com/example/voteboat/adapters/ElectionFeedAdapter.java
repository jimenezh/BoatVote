package com.example.voteboat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voteboat.databinding.ItemElectionBinding;
import com.example.voteboat.models.Election;

import java.util.ArrayList;
import java.util.List;

public class ElectionFeedAdapter extends RecyclerView.Adapter<ElectionFeedAdapter.ViewHolder> {
    
    public static final String TAG = "ElectionFeedAdapter";
    
    Context context;
    List<Election> elections;

    public ElectionFeedAdapter(Context context, List<Election> elections) {
        this.context = context;
        // Dummy data
        this.elections = new ArrayList<>();
        this.elections.add(new Election("First Post"));
        this.elections.add(new Election("Second post"));
//        this.elections = elections;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemElectionBinding binding = ItemElectionBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(elections.get(position));
    }

    @Override
    public int getItemCount() {
        return elections.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemElectionBinding binding;

        public ViewHolder(@NonNull ItemElectionBinding itemElectionBinding) {
            super(itemElectionBinding.getRoot());
            this.binding = itemElectionBinding;
        }

        public void bind(Election election) {
            binding.tvTitle.setText(election.getTitle());
        }
    }
}
