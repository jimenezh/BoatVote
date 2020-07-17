package com.example.voteboat.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.voteboat.R;
import com.example.voteboat.adapters.ToDoAdapter;
import com.example.voteboat.databinding.FragmentToDoBinding;
import com.example.voteboat.models.User;

public class ToDoFragment extends Fragment {
    public static final String TAG ="ToDoFragment";
    FragmentToDoBinding binding;

    public ToDoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentToDoBinding.inflate(inflater);

        binding.rvToDoList.setAdapter(new ToDoAdapter(getContext(), User.getStarredElections()));
        binding.rvToDoList.setLayoutManager(new LinearLayoutManager(getContext()));

        return binding.getRoot();
    }
}