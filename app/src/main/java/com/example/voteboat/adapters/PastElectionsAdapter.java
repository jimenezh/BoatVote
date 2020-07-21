package com.example.voteboat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voteboat.databinding.ItemPastElectionBinding;
import com.example.voteboat.models.Election;

import java.util.List;

public class PastElectionsAdapter extends RecyclerView.Adapter<PastElectionsAdapter.ViewHolder> {

    Context context;
    List<Election> pastElections;

    public PastElectionsAdapter(Context context, List<Election> pastElections) {
        this.context = context;
        this.pastElections = pastElections;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemPastElectionBinding binding = ItemPastElectionBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(pastElections.get(position));
    }

    @Override
    public int getItemCount() {
        return pastElections.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemPastElectionBinding binding;

        public ViewHolder(@NonNull ItemPastElectionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Election election) {
            binding.tvTitle.setText(election.getTitle());
            binding.tvDate.setText(election.getElectionDate());
        }
    }
}
