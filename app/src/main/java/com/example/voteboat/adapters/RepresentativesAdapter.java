package com.example.voteboat.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.voteboat.models.Representative;

import java.util.List;

public class RepresentativesAdapter extends RecyclerView.Adapter<RepresentativesAdapter.ViewHolder> {

    Context context;
    List<Representative> representatives;

    public RepresentativesAdapter(Context context, List<Representative> representatives) {
        this.context = context;
        this.representatives = representatives;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
