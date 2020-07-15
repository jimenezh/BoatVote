package com.example.voteboat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voteboat.databinding.ItemElectionBinding;
import com.example.voteboat.databinding.ItemRaceBinding;
import com.example.voteboat.models.Race;

import java.util.List;

public class RaceAdapter extends RecyclerView.Adapter<RaceAdapter.ViewHolder> {

    public static final String TAG = "ElectionFeedAdapter";

    private Context context;
    private List<Race> races;

    public RaceAdapter(Context context, List<Race> races) {
        this.context = context;
        this.races = races;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRaceBinding binding = ItemRaceBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(races.get(position));
    }

    @Override
    public int getItemCount() {
        return races.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ItemRaceBinding binding;

        public ViewHolder(@NonNull ItemRaceBinding itemRaceBinding) {
            super(itemRaceBinding.getRoot());
            this.binding = itemRaceBinding;
            itemRaceBinding.getRoot().setOnClickListener(this);
        }

        public void bind(Race race) {
            binding.tvTitle.setText(race.getOffice());
            // TODO: bind proper selector for star
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(context,"Pressed on race "+getAdapterPosition(),Toast.LENGTH_LONG).show();
        }
    }
}
