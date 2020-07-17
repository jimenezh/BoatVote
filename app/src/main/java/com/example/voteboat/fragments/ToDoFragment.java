package com.example.voteboat.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.voteboat.R;
import com.example.voteboat.databinding.FragmentToDoBinding;

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
        return binding.getRoot();
    }
}