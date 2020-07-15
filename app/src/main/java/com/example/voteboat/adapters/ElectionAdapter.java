package com.example.voteboat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voteboat.databinding.ItemElectionBinding;
import com.example.voteboat.models.Election;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ElectionAdapter extends RecyclerView.Adapter<ElectionAdapter.ViewHolder> {

    public static final String TAG = "ElectionFeedAdapter";

    Context context;
    List<Election> elections;

    public ElectionAdapter(Context context, List<Election> elections) {
        this.context = context;
        // Dummy data
        this.elections = elections;
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ItemElectionBinding binding;

        public ViewHolder(@NonNull ItemElectionBinding itemElectionBinding) {
            super(itemElectionBinding.getRoot());
            this.binding = itemElectionBinding;
            itemElectionBinding.getRoot().setOnClickListener(this);
        }

        public void bind(Election election) {
            binding.tvTitle.setText(election.getTitle());
            binding.tvDate.setText(election.getElectionDate().toString());
            // TODO: bind proper selector for star
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(context,"Pressed on election "+getAdapterPosition(),Toast.LENGTH_LONG).show();
        }
    }
}
