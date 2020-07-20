package com.example.voteboat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voteboat.databinding.ItemRepresentativeBinding;
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
        ItemRepresentativeBinding binding = ItemRepresentativeBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(representatives.get(position));
    }

    @Override
    public int getItemCount() {
        return representatives.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemRepresentativeBinding binding;

        public ViewHolder(@NonNull ItemRepresentativeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Representative representative) {
            binding.tvRepName.setText(representative.getName());
            binding.tvParty.setText(representative.getParty());
            setText(binding.tvPhone, representative.getPhoneNumber());
            setText(binding.tvPhone, representative.getPhoneNumber());
            setText(binding.tvEmail, representative.getEmail());
            setText(binding.tvUrl,representative.getUrl());

        }

        // In case Representative fields aren't available, check if null
        // and set appropriate text + visibility
        private void setText(TextView textView, String text) {
            if(text == null)
                textView.setVisibility(View.GONE);
            else{
                textView.setText(text);
                textView.setVisibility(View.VISIBLE);
            }
        }
    }
}
