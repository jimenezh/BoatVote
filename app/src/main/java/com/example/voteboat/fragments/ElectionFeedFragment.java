package com.example.voteboat.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.voteboat.adapters.ElectionFeedAdapter;
import com.example.voteboat.databinding.FragmentElectionFeedBinding;
import com.example.voteboat.models.Election;

import java.util.ArrayList;
import java.util.List;

public class ElectionFeedFragment extends Fragment {

    FragmentElectionFeedBinding binding;
    ElectionFeedAdapter adapter;
    List<Election> elections;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentElectionFeedBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Inititalizing empty list
        elections = new ArrayList<>();
        // Setting adapter
        adapter = new ElectionFeedAdapter(getContext(),elections);
        binding.rvElections.setAdapter(adapter);
        binding.rvElections.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}